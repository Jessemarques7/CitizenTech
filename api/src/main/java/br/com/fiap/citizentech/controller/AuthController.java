package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.AuthDtos.LoginRequest;
import br.com.fiap.citizentech.dto.AuthDtos.RegistroRequest;
import br.com.fiap.citizentech.dto.AuthDtos.TokenResponse;
import br.com.fiap.citizentech.dto.AuthDtos.UsuarioResponse;
import br.com.fiap.citizentech.security.UsuarioLogado;
import br.com.fiap.citizentech.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Cadastro, login (JWT) e dados do usuário logado")
public class AuthController {

    private final AuthService authService;
    private final UsuarioLogado usuarioLogado;

    public AuthController(AuthService authService, UsuarioLogado usuarioLogado) {
        this.authService = authService;
        this.usuarioLogado = usuarioLogado;
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    @Operation(summary = "Cria conta de cidadão", description = "201 com token; 400 dados inválidos; 409 e-mail já usado.")
    public TokenResponse registrar(@Valid @RequestBody RegistroRequest request) {
        return authService.registrar(request);
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Login", description = "200 com token JWT; 401 credenciais inválidas; 429 após 5 falhas seguidas.")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Dados do usuário logado")
    public UsuarioResponse me() {
        return authService.perfil(usuarioLogado.atual().id());
    }
}
