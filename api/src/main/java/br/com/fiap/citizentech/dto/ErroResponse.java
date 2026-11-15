package br.com.fiap.citizentech.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/** Formato único de erro. Não expõe stack trace nem detalhes internos. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, String> erros
) {
    public static ErroResponse de(int status, String message, String path) {
        return new ErroResponse(status, message, path, LocalDateTime.now(), null);
    }
}
