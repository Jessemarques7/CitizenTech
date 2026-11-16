package br.com.fiap.citizentech.exception;

import br.com.fiap.citizentech.dto.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErroResponse> negocio(ApiException e, HttpServletRequest req) {
        return responder(e.getStatus(), e.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, String> erros = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> erros.putIfAbsent(f.getField(), f.getDefaultMessage()));
        ErroResponse corpo = new ErroResponse(400, "Dados inválidos. Verifique os campos informados.",
                req.getRequestURI(), LocalDateTime.now(), erros);
        return ResponseEntity.badRequest().body(corpo);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErroResponse> validacaoParametro(HandlerMethodValidationException e, HttpServletRequest req) {
        String detalhes = e.getAllErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .distinct()
                .reduce((a, b) -> a + "; " + b)
                .orElse("verifique os valores enviados");
        return responder(HttpStatus.BAD_REQUEST, "Dados inválidos: " + detalhes + ".", req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> jsonInvalido(HttpMessageNotReadableException e, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, "Corpo da requisição ausente ou com JSON inválido.", req);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErroResponse> parametroInvalido(Exception e, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, "Parâmetro ausente ou com formato inválido.", req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> integridade(DataIntegrityViolationException e, HttpServletRequest req) {
        log.warn("Violação de integridade em {}: {}", req.getRequestURI(), e.getMostSpecificCause().getMessage());
        return responder(HttpStatus.CONFLICT, "A operação viola uma regra de integridade dos dados.", req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> acessoNegado(AccessDeniedException e, HttpServletRequest req) {
        return responder(HttpStatus.FORBIDDEN, "Seu perfil não tem permissão para esta operação.", req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> metodo(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP não suportado nesta rota.", req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> rotaInexistente(NoResourceFoundException e, HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, "Rota não encontrada.", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> inesperado(Exception e, HttpServletRequest req) {
        log.error("Erro inesperado em {}", req.getRequestURI(), e);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno. Tente novamente mais tarde.", req);
    }

    private ResponseEntity<ErroResponse> responder(HttpStatus status, String mensagem, HttpServletRequest req) {
        return ResponseEntity.status(status).body(ErroResponse.de(status.value(), mensagem, req.getRequestURI()));
    }
}
