package br.com.fiap.citizentech.security;

import br.com.fiap.citizentech.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioLogado {

    public UsuarioAutenticado atual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UsuarioAutenticado usuario) {
            return usuario;
        }
        throw new ApiException(HttpStatus.UNAUTHORIZED, "Faça login para continuar.");
    }
}
