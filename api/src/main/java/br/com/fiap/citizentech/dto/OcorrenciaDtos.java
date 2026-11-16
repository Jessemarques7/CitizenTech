package br.com.fiap.citizentech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class OcorrenciaDtos {

    private OcorrenciaDtos() {
    }

    /**
     * Relato de barreira. O autor é sempre o usuário do token; {@code usuarioId} é aceito apenas
     * por compatibilidade com o contrato do app e é ignorado.
     */
    public record OcorrenciaRequest(
            String usuarioId,

            @NotBlank(message = "é obrigatório")
            String localId,

            @NotBlank(message = "é obrigatória")
            String categoria,

            @NotBlank(message = "é obrigatória")
            @Size(min = 12, max = 500, message = "deve ter entre 12 e 500 caracteres")
            @Pattern(regexp = Validacoes.SEM_HTML, message = Validacoes.MSG_SEM_HTML)
            String descricao
    ) {
    }

    public record StatusRequest(@NotBlank(message = "é obrigatório") String status) {
    }

    public record OcorrenciaResponse(
            String id,
            String localId,
            String localNome,
            String categoria,
            String descricao,
            String status,
            String criadoEm,
            int progresso,
            String usuarioId,
            String usuarioNome,
            LocalDateTime dataCriacao,
            LocalDateTime dataAtualizacao
    ) {
    }
}
