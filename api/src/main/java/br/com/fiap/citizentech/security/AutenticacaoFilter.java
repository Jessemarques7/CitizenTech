package br.com.fiap.citizentech.security;

import br.com.fiap.citizentech.config.CitizenTechProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

/**
 * Autentica a requisição por:
 * <ul>
 *   <li>{@code Authorization: Bearer <jwt>} - usuários (CIDADAO ou ADMIN);</li>
 *   <li>{@code X-Device-Key: <chave>} - sensores IoT (ROLE_DEVICE), só para enviar leituras.</li>
 * </ul>
 * Sem credencial válida a requisição segue anônima e as regras do SecurityConfig respondem 401/403.
 */
@Component
public class AutenticacaoFilter extends OncePerRequestFilter {

    public static final String HEADER_DEVICE_KEY = "X-Device-Key";

    private final JwtService jwtService;
    private final byte[] deviceKey;

    public AutenticacaoFilter(JwtService jwtService, CitizenTechProperties props) {
        this.jwtService = jwtService;
        this.deviceKey = props.iot().deviceKey().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        String chaveDispositivo = request.getHeader(HEADER_DEVICE_KEY);

        if (auth != null && auth.startsWith("Bearer ")) {
            jwtService.validar(auth.substring(7).trim()).ifPresent(usuario -> autenticar(usuario,
                    List.of(new SimpleGrantedAuthority("ROLE_" + usuario.perfil().name()))));
        } else if (chaveDispositivo != null && chaveValida(chaveDispositivo)) {
            autenticar(new UsuarioAutenticado(null, "dispositivo-iot", null),
                    List.of(new SimpleGrantedAuthority("ROLE_DEVICE")));
        }
        chain.doFilter(request, response);
    }

    /** Comparação em tempo constante para não vazar a chave por timing. */
    private boolean chaveValida(String recebida) {
        return MessageDigest.isEqual(deviceKey, recebida.getBytes(StandardCharsets.UTF_8));
    }

    private void autenticar(UsuarioAutenticado principal, List<SimpleGrantedAuthority> perfis) {
        var autenticacao = new UsernamePasswordAuthenticationToken(principal, null, perfis);
        SecurityContextHolder.getContext().setAuthentication(autenticacao);
    }
}
