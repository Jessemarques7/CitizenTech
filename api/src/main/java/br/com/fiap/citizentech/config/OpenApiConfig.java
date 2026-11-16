package br.com.fiap.citizentech.config;

import br.com.fiap.citizentech.security.AutenticacaoFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI citizenTechOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CitizenTech API")
                        .version("1.0.0")
                        .description("""
                                API REST da CitizenTech - Acessibilidade Urbana Inteligente (Global Solution FIAP 2026/2).

                                **Como testar:** chame `POST /api/auth/login` com um usuário de demonstração,
                                copie o `token` e clique em **Authorize** (bearerAuth).

                                - Admin: `admin@citizentech.com` / `Admin@2026`
                                - Cidadã: `ana@citizentech.com` / `Cidadao@2026`
                                - Sensores IoT: cabeçalho `X-Device-Key` (deviceKey) em `POST /api/sensores/leituras`
                                """))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
                        .addSecuritySchemes("deviceKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER)
                                .name(AutenticacaoFilter.HEADER_DEVICE_KEY)))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
