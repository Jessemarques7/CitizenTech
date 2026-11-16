package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.dto.Datas;
import br.com.fiap.citizentech.dto.OcorrenciaDtos.OcorrenciaRequest;
import br.com.fiap.citizentech.dto.OcorrenciaDtos.OcorrenciaResponse;
import br.com.fiap.citizentech.exception.AcessoNegadoException;
import br.com.fiap.citizentech.exception.ConflitoException;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.exception.RequisicaoInvalidaException;
import br.com.fiap.citizentech.model.Local;
import br.com.fiap.citizentech.model.Ocorrencia;
import br.com.fiap.citizentech.model.StatusOcorrencia;
import br.com.fiap.citizentech.model.Usuario;
import br.com.fiap.citizentech.repository.LocalRepository;
import br.com.fiap.citizentech.repository.OcorrenciaRepository;
import br.com.fiap.citizentech.repository.UsuarioRepository;
import br.com.fiap.citizentech.security.UsuarioAutenticado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.fiap.citizentech.service.Identificadores.PREFIXO_OCORRENCIA;

@Service
public class OcorrenciaService {

    /** Mesmas categorias oferecidas no app. */
    public static final List<String> CATEGORIAS = List.of(
            "Calçada irregular", "Rampa bloqueada", "Ausência de rampa", "Escada sem alternativa",
            "Elevador fora de serviço", "Travessia difícil", "Piso tátil danificado",
            "Sinalização inadequada", "Outro");

    private final OcorrenciaRepository ocorrencias;
    private final LocalRepository locais;
    private final UsuarioRepository usuarios;

    public OcorrenciaService(OcorrenciaRepository ocorrencias, LocalRepository locais, UsuarioRepository usuarios) {
        this.ocorrencias = ocorrencias;
        this.locais = locais;
        this.usuarios = usuarios;
    }

    /**
     * Cidadão vê apenas os próprios relatos (pedir os de outra pessoa = 403).
     * Admin vê todos, podendo filtrar por usuário.
     */
    @Transactional(readOnly = true)
    public List<OcorrenciaResponse> listar(UsuarioAutenticado quem, String usuarioId, String status) {
        Long filtroUsuario = usuarioId == null || usuarioId.isBlank() ? null : idUsuario(usuarioId);
        if (!quem.isAdmin()) {
            if (filtroUsuario != null && !filtroUsuario.equals(quem.id())) {
                throw new AcessoNegadoException("Você só pode consultar os seus próprios relatos.");
            }
            filtroUsuario = quem.id();
        }
        StatusOcorrencia filtroStatus = status == null || status.isBlank() || status.equalsIgnoreCase("Todos")
                ? null : StatusOcorrencia.de(status);
        return ocorrencias.buscar(filtroUsuario, filtroStatus).stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public OcorrenciaResponse buscar(UsuarioAutenticado quem, String id) {
        Ocorrencia o = carregar(id);
        if (!quem.isAdmin() && !o.pertenceA(quem.id())) {
            throw new AcessoNegadoException("Você só pode consultar os seus próprios relatos.");
        }
        return paraResponse(o);
    }

    @Transactional
    public OcorrenciaResponse criar(UsuarioAutenticado quem, OcorrenciaRequest req) {
        String categoria = CATEGORIAS.stream()
                .filter(c -> c.equalsIgnoreCase(req.categoria().trim()))
                .findFirst()
                .orElseThrow(() -> new RequisicaoInvalidaException(
                        "Categoria inválida. Use uma de: " + String.join(", ", CATEGORIAS) + "."));
        Local local = locais.findById(req.localId().trim())
                .orElseThrow(() -> new RequisicaoInvalidaException("Selecione um local válido: '" + req.localId() + "' não existe."));
        Usuario autor = usuarios.findById(quem.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        Ocorrencia nova = ocorrencias.save(new Ocorrencia(autor, local, categoria, req.descricao().trim()));
        return paraResponse(nova);
    }

    @Transactional
    public OcorrenciaResponse atualizarStatus(String id, String status) {
        Ocorrencia o = carregar(id);
        StatusOcorrencia novo = StatusOcorrencia.de(status);
        if (o.getStatus() == StatusOcorrencia.RESOLVIDA) {
            throw new ConflitoException("O relato já foi resolvido e não pode mudar de status.");
        }
        o.alterarStatus(novo);
        return paraResponse(o);
    }

    /** Admin exclui qualquer relato; o autor só cancela enquanto o relato ainda está "Recebida". */
    @Transactional
    public void excluir(UsuarioAutenticado quem, String id) {
        Ocorrencia o = carregar(id);
        if (!quem.isAdmin()) {
            if (!o.pertenceA(quem.id())) {
                throw new AcessoNegadoException("Você só pode cancelar os seus próprios relatos.");
            }
            if (o.getStatus() != StatusOcorrencia.RECEBIDA) {
                throw new ConflitoException("Só é possível cancelar relatos que ainda não entraram em análise.");
            }
        }
        ocorrencias.delete(o);
    }

    private Ocorrencia carregar(String id) {
        return ocorrencias.findById(Identificadores.numero(id, PREFIXO_OCORRENCIA))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relato '" + id + "' não encontrado."));
    }

    private Long idUsuario(String valor) {
        try {
            return Long.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            throw new RequisicaoInvalidaException("usuarioId inválido: '" + valor + "'.");
        }
    }

    private OcorrenciaResponse paraResponse(Ocorrencia o) {
        return new OcorrenciaResponse(
                Identificadores.ocorrencia(o.getId()),
                o.getLocal().getId(),
                o.getLocal().getNome(),
                o.getCategoria(),
                o.getDescricao(),
                o.getStatus().getRotulo(),
                Datas.relativa(o.getDataCriacao()),
                o.getStatus().getProgresso(),
                String.valueOf(o.getUsuario().getId()),
                o.getUsuario().getNome(),
                o.getDataCriacao(),
                o.getDataAtualizacao());
    }
}
