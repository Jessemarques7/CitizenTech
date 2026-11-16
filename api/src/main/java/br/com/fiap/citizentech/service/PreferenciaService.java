package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.dto.PreferenciasDto;
import br.com.fiap.citizentech.exception.AcessoNegadoException;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.exception.RequisicaoInvalidaException;
import br.com.fiap.citizentech.model.Preferencia;
import br.com.fiap.citizentech.repository.PreferenciaRepository;
import br.com.fiap.citizentech.repository.UsuarioRepository;
import br.com.fiap.citizentech.security.UsuarioAutenticado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreferenciaService {

    private final PreferenciaRepository preferencias;
    private final UsuarioRepository usuarios;

    public PreferenciaService(PreferenciaRepository preferencias, UsuarioRepository usuarios) {
        this.preferencias = preferencias;
        this.usuarios = usuarios;
    }

    @Transactional
    public PreferenciasDto buscar(UsuarioAutenticado quem, String usuarioId) {
        return PreferenciasDto.de(carregarOuCriar(quem, usuarioId));
    }

    @Transactional
    public PreferenciasDto salvar(UsuarioAutenticado quem, String usuarioId, PreferenciasDto dto) {
        Preferencia p = carregarOuCriar(quem, usuarioId);
        p.atualizar(dto.evitarEscadas(), dto.priorizarRampas(), dto.alertasVisuais(), dto.altoContraste());
        return PreferenciasDto.de(p);
    }

    /** Cada pessoa só lê/grava as próprias preferências (admin pode consultar qualquer uma). */
    private Preferencia carregarOuCriar(UsuarioAutenticado quem, String usuarioId) {
        Long id;
        try {
            id = Long.valueOf(usuarioId.trim());
        } catch (NumberFormatException e) {
            throw new RequisicaoInvalidaException("usuarioId inválido: '" + usuarioId + "'.");
        }
        if (!quem.isAdmin() && !id.equals(quem.id())) {
            throw new AcessoNegadoException("Você só pode acessar as suas próprias preferências.");
        }
        if (!usuarios.existsById(id)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado.");
        }
        return preferencias.findById(id).orElseGet(() -> preferencias.save(new Preferencia(id)));
    }
}
