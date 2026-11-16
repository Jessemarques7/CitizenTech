package br.com.fiap.citizentech.config;

import br.com.fiap.citizentech.dto.ErroResponse;
import br.com.fiap.citizentech.security.AutenticacaoFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AutenticacaoFilter autenticacaoFilter) throws Exception {
        http
                // API stateless com token no cabeçalho: não há cookie de sessão para CSRF explorar.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(h -> h
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; "
                                        + "img-src 'self' data:; frame-ancestors 'none'"))
                        .frameOptions(f -> f.deny())
                        .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/registrar").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // IoT: só dispositivo autorizado (X-Device-Key) ou administrador
                        .requestMatchers(HttpMethod.POST, "/api/sensores/leituras").hasAnyRole("DEVICE", "ADMIN")
                        .requestMatchers("/api/sensores/**").hasRole("ADMIN")
                        // Ações administrativas
                        .requestMatchers(HttpMethod.POST, "/api/locais/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/locais/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/locais/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/ocorrencias/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/alertas/**").hasRole("ADMIN")
                        // Demais rotas da API: cidadão ou admin logado (regras de "dono" ficam nos services)
                        .requestMatchers("/api/**").hasAnyRole("CIDADAO", "ADMIN")
                        .anyRequest().denyAll())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) ->
                                erro(req, res, 401, "Faça login para continuar (token ausente, inválido ou expirado)."))
                        .accessDeniedHandler((req, res, ex) ->
                                erro(req, res, 403, "Seu perfil não tem permissão para esta operação.")))
                .addFilterBefore(autenticacaoFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** O filtro roda só dentro da cadeia do Spring Security, não como filtro de servlet avulso. */
    @Bean
    public FilterRegistrationBean<AutenticacaoFilter> desabilitarRegistroAutomatico(AutenticacaoFilter filtro) {
        FilterRegistrationBean<AutenticacaoFilter> registro = new FilterRegistrationBean<>(filtro);
        registro.setEnabled(false);
        return registro;
    }

    /** BCrypt com salt aleatório por senha; a senha original nunca é armazenada. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** CORS liberado apenas para as origens configuradas (citizentech.cors.allowed-origins). */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(CitizenTechProperties props) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(props.cors().allowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", AutenticacaoFilter.HEADER_DEVICE_KEY));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    private void erro(HttpServletRequest req, HttpServletResponse res, int status, String mensagem) throws IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(res.getOutputStream(), ErroResponse.de(status, mensagem, req.getRequestURI()));
    }
}
