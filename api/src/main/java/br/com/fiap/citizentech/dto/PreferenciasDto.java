package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.Preferencia;
import jakarta.validation.constraints.NotNull;

public record PreferenciasDto(
        @NotNull(message = "é obrigatório") Boolean evitarEscadas,
        @NotNull(message = "é obrigatório") Boolean priorizarRampas,
        @NotNull(message = "é obrigatório") Boolean alertasVisuais,
        @NotNull(message = "é obrigatório") Boolean altoContraste,
        String usuarioId
) {
    public static PreferenciasDto de(Preferencia p) {
        return new PreferenciasDto(p.isEvitarEscadas(), p.isPriorizarRampas(), p.isAlertasVisuais(),
                p.isAltoContraste(), String.valueOf(p.getUsuarioId()));
    }
}
