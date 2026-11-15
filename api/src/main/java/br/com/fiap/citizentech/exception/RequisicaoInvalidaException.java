package br.com.fiap.citizentech.exception;

import org.springframework.http.HttpStatus;

public class RequisicaoInvalidaException extends ApiException {

    public RequisicaoInvalidaException(String mensagem) {
        super(HttpStatus.BAD_REQUEST, mensagem);
    }
}
