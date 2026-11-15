package br.com.fiap.citizentech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public final class LocalDtos {

    private LocalDtos() {
    }

    /** Formato consumido pelo app (ver API_CONTRATO.md). */
    public record LocalResponse(
            String id,
            String nome,
            String categoria,
            String endereco,
            String statusAcessibilidade,
            String resumo,
            List<String> recursos,
            List<String> obstaculos,
            int ocupacao,
            String nivelOcupacao,
            String ultimaLeitura,
            String atualizadoEm,
            boolean leituraDesatualizada,
            String confianca
    ) {
    }

    /** Cadastro/edição de local (somente ADMIN). */
    public record LocalRequest(
            @Pattern(regexp = Validacoes.SLUG, message = "use letras minúsculas, números e hífen (ex.: centro-cultural)")
            @Size(max = 40, message = "deve ter no máximo 40 caracteres")
            String id,

            @NotBlank(message = "é obrigatório") @Size(max = 100, message = "deve ter no máximo 100 caracteres")
            @Pattern(regexp = Validacoes.SEM_HTML, message = Validacoes.MSG_SEM_HTML)
            String nome,

            @NotBlank(message = "é obrigatória") @Size(max = 50, message = "deve ter no máximo 50 caracteres")
            @Pattern(regexp = Validacoes.SEM_HTML, message = Validacoes.MSG_SEM_HTML)
            String categoria,

            @NotBlank(message = "é obrigatório") @Size(max = 150, message = "deve ter no máximo 150 caracteres")
            @Pattern(regexp = Validacoes.SEM_HTML, message = Validacoes.MSG_SEM_HTML)
            String endereco,

            @NotBlank(message = "é obrigatório (Acessível, Parcial ou Inacessível)")
            String statusAcessibilidade,

            @Size(max = 400, message = "deve ter no máximo 400 caracteres")
            @Pattern(regexp = Validacoes.SEM_HTML, message = Validacoes.MSG_SEM_HTML)
            String resumo,

            @Size(max = 60, message = "deve ter no máximo 60 caracteres")
            @Pattern(regexp = Validacoes.SEM_HTML, message = Validacoes.MSG_SEM_HTML)
            String confianca,

            @NotNull(message = "é obrigatório (pode ser lista vazia)")
            @Size(max = 20, message = "aceita no máximo 20 itens")
            List<@NotBlank @Size(max = 150) @Pattern(regexp = Validacoes.SEM_HTML, message = Validacoes.MSG_SEM_HTML) String> recursos,

            @NotNull(message = "é obrigatório (pode ser lista vazia)")
            @Size(max = 20, message = "aceita no máximo 20 itens")
            List<@NotBlank @Size(max = 150) @Pattern(regexp = Validacoes.SEM_HTML, message = Validacoes.MSG_SEM_HTML) String> obstaculos
    ) {
    }
}
