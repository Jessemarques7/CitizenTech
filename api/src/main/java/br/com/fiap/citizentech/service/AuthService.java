package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.dto.AuthDtos.LoginRequest;
import br.com.fiap.citizentech.dto.AuthDtos.RegistroRequest;
import br.com.fiap.citizentech.dto.AuthDtos.TokenResponse;
import br.com.fiap.citizentech.dto.AuthDtos.UsuarioResponse;
import br.com.fiap.citizentech.exception.ApiException;
import br.com.fiap.citizentech.exception.ConflitoException;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.model.Perfil;
import br.com.fiap.citizentech.model.Preferencia;
import br.com.fiap.citizentech.model.Usuario;
import br.com.fiap.citizentech.repository.PreferenciaRepository;
import br.com.fiap.citizentech.repository.UsuarioRepository;
import br.com.fiap.citizentech.security.JwtService;
import br.com.fiap.citizentech.security.TentativasLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    /** Mensagem única para e-mail inexistente ou senha errada: não revela quais e-mails existem. */
    private static final String CREDENCIAIS_INVALIDAS = "E-mail ou senha inválidos.";

    private final UsuarioRepository usuarios;
    private final PreferenciaRepository preferencias;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TentativasLoginService tentativas;

    public AuthService(UsuarioRepository usuarios, PreferenciaRepository preferencias, PasswordEncoder passwordEncoder,
                       JwtService jwtService, TentativasLoginService tentativas) {
        this.usuarios = usuarios;
        this.preferencias = preferencias;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tentativas = tentativas;
    }

    @Transactional
    public TokenResponse registrar(RegistroRequest req) {
        String email = req.email().trim().toLowerCase();
        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw new ConflitoException("Já existe uma conta com este e-mail.");
        }
        Usuario usuario = usuarios.save(
                new Usuario(req.nome().trim(), email, passwordEncoder.encode(req.senha()), Perfil.CIDADAO));
        preferencias.save(new Preferencia(usuario.getId()));
        return emitirToken(usuario);
    }

    public TokenResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        tentativas.verificarBloqueio(email);
        Usuario usuario = usuarios.findByEmailIgnoreCase(email)
                .filter(u -> passwordEncoder.matches(req.senha(), u.getSenhaHash()))
                .orElse(null);
        if (usuario == null) {
            tentativas.registrarFalha(email);
            throw new ApiException(HttpStatus.UNAUTHORIZED, CREDENCIAIS_INVALIDAS);
        }
        tentativas.registrarSucesso(email);
        return emitirToken(usuario);
    }

    public UsuarioResponse perfil(Long usuarioId) {
        return usuarios.findById(usuarioId)
                .map(UsuarioResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    private TokenResponse emitirToken(Usuario usuario) {
        JwtService.Token token = jwtService.gerar(usuario);
        return new TokenResponse(token.valor(), "Bearer", token.expiraEm(), UsuarioResponse.de(usuario));
    }
}
