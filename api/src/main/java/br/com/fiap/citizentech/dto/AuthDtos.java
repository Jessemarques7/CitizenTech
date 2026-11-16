package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank(message = "é obrigatório") @Email(message = "deve ser um e-mail válido") String email,
            @NotBlank(message = "é obrigatória") String senha
    ) {
    }

    /** Cadastro público sempre cria perfil CIDADAO: o perfil não é aceito no corpo (evita escalada de privilégio). */
    public record RegistroRequest(
            @NotBlank(message = "é obrigatório")
            @Size(min = 3, max = 100, message = "deve ter entre 3 e 100 caracteres")
            @Pattern(regexp = Validacoes.SEM_HTML, message = Validacoes.MSG_SEM_HTML)
            String nome,

            @NotBlank(message = "é obrigatório")
            @Email(message = "deve ser um e-mail válido")
            @Size(max = 120, message = "deve ter no máximo 120 caracteres")
            String email,

            @NotBlank(message = "é obrigatória")
            @Pattern(regexp = Validacoes.SENHA_FORTE,
                    message = "deve ter de 8 a 72 caracteres, com pelo menos uma letra e um número")
            String senha
    ) {
    }

    public record UsuarioResponse(String id, String nome, String email, String perfil) {
        public static UsuarioResponse de(Usuario u) {
            return new UsuarioResponse(String.valueOf(u.getId()), u.getNome(), u.getEmail(), u.getPerfil().name());
        }
    }

    public record TokenResponse(String token, String tipo, Instant expiraEm, UsuarioResponse usuario) {
    }
}
