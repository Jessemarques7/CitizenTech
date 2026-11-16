package br.com.fiap.citizentech.security;

import br.com.fiap.citizentech.config.CitizenTechProperties;
import br.com.fiap.citizentech.model.Perfil;
import br.com.fiap.citizentech.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/** Emite e valida tokens JWT assinados com HMAC-SHA256. */
@Service
public class JwtService {

    private final Algorithm algoritmo;
    private final JWTVerifier verificador;
    private final String emissor;
    private final long expiracaoMinutos;

    public JwtService(CitizenTechProperties props) {
        String segredo = props.jwt().secret();
        if (segredo == null || segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("citizentech.jwt.secret deve ter pelo menos 32 bytes.");
        }
        this.algoritmo = Algorithm.HMAC256(segredo);
        this.emissor = props.jwt().emissor();
        this.expiracaoMinutos = props.jwt().expiracaoMinutos();
        this.verificador = JWT.require(algoritmo).withIssuer(emissor).build();
    }

    public Token gerar(Usuario usuario) {
        Instant expira = Instant.now().plus(expiracaoMinutos, ChronoUnit.MINUTES);
        String token = JWT.create()
                .withIssuer(emissor)
                .withSubject(String.valueOf(usuario.getId()))
                .withClaim("email", usuario.getEmail())
                .withClaim("perfil", usuario.getPerfil().name())
                .withIssuedAt(Instant.now())
                .withExpiresAt(expira)
                .sign(algoritmo);
        return new Token(token, expira);
    }

    /** Retorna vazio se o token for inválido, adulterado ou expirado. */
    public Optional<UsuarioAutenticado> validar(String token) {
        try {
            DecodedJWT jwt = verificador.verify(token);
            return Optional.of(new UsuarioAutenticado(
                    Long.valueOf(jwt.getSubject()),
                    jwt.getClaim("email").asString(),
                    Perfil.valueOf(jwt.getClaim("perfil").asString())));
        } catch (JWTVerificationException | IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public record Token(String valor, Instant expiraEm) {
    }
}
