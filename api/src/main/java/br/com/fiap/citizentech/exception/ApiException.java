package br.com.fiap.citizentech.exception;

import org.springframework.http.HttpStatus;

/** Erro de negócio com status HTTP associado. A mensagem é segura para exibir ao usuário. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
