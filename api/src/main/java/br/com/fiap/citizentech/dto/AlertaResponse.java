package br.com.fiap.citizentech.dto;

import java.time.LocalDateTime;

/** Formato consumido pelo app. {@code simulado} deixa claro que não é alerta oficial da Defesa Civil. */
public record AlertaResponse(
        String id,
        String tipo,
        String titulo,
        String local,
        String localId,
        double valor,
        String unidade,
        String atualizadoEm,
        String descricao,
        String status,
        String sensor,
        boolean simulado,
        LocalDateTime dataCriacao
) {
}
