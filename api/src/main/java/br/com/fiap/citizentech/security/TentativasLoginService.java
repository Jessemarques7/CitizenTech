package br.com.fiap.citizentech.security;

import br.com.fiap.citizentech.config.CitizenTechProperties;
import br.com.fiap.citizentech.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Bloqueio temporário após várias senhas erradas para o mesmo e-mail (mitiga força bruta). */
@Service
public class TentativasLoginService {

    private final int maxTentativas;
    private final Duration bloqueio;
    private final Map<String, Registro> registros = new ConcurrentHashMap<>();

    public TentativasLoginService(CitizenTechProperties props) {
        this.maxTentativas = props.login().maxTentativas();
        this.bloqueio = Duration.ofMinutes(props.login().bloqueioMinutos());
    }

    public void verificarBloqueio(String email) {
        Registro r = registros.get(chave(email));
        if (r != null && r.falhas >= maxTentativas && r.ultimaFalha.plus(bloqueio).isAfter(Instant.now())) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS,
                    "Muitas tentativas de login. Aguarde " + bloqueio.toMinutes() + " minutos e tente novamente.");
        }
    }

    public void registrarFalha(String email) {
        registros.compute(chave(email), (k, r) -> {
            if (r == null || r.ultimaFalha.plus(bloqueio).isBefore(Instant.now())) {
                return new Registro(1, Instant.now());
            }
            return new Registro(r.falhas + 1, Instant.now());
        });
    }

    public void registrarSucesso(String email) {
        registros.remove(chave(email));
    }

    private String chave(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private record Registro(int falhas, Instant ultimaFalha) {
    }
}
