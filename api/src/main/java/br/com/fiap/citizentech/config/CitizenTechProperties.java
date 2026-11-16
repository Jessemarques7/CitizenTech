package br.com.fiap.citizentech.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/** Configurações da aplicação (application.properties). Segredos vêm de variáveis de ambiente. */
@ConfigurationProperties(prefix = "citizentech")
public record CitizenTechProperties(Jwt jwt, Iot iot, Cors cors, Login login) {

    public record Jwt(String secret, long expiracaoMinutos, String emissor) {
    }

    public record Iot(String deviceKey, long minutosDesatualizada) {
    }

    public record Cors(List<String> allowedOrigins) {
    }

    public record Login(int maxTentativas, long bloqueioMinutos) {
    }
}
