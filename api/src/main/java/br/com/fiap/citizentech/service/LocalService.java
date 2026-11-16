package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.config.CitizenTechProperties;
import br.com.fiap.citizentech.dto.Datas;
import br.com.fiap.citizentech.dto.LocalDtos.LocalRequest;
import br.com.fiap.citizentech.dto.LocalDtos.LocalResponse;
import br.com.fiap.citizentech.exception.ConflitoException;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.exception.RequisicaoInvalidaException;
import br.com.fiap.citizentech.model.Local;
import br.com.fiap.citizentech.model.Sensor;
import br.com.fiap.citizentech.model.StatusAcessibilidade;
import br.com.fiap.citizentech.model.TipoItem;
import br.com.fiap.citizentech.model.TipoSensor;
import br.com.fiap.citizentech.repository.LocalRepository;
import br.com.fiap.citizentech.repository.OcorrenciaRepository;
import br.com.fiap.citizentech.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocalService {

    /** Usado quando o local não tem sensor de ocupação cadastrado. */
    private static final BigDecimal LIMITE_OCUPACAO_PADRAO = BigDecimal.valueOf(35);

    private final LocalRepository locais;
    private final SensorRepository sensores;
    private final OcorrenciaRepository ocorrencias;
    private final long minutosDesatualizada;

    public LocalService(LocalRepository locais, SensorRepository sensores, OcorrenciaRepository ocorrencias,
                        CitizenTechProperties props) {
        this.locais = locais;
        this.sensores = sensores;
        this.ocorrencias = ocorrencias;
        this.minutosDesatualizada = props.iot().minutosDesatualizada();
    }

    @Transactional(readOnly = true)
    public List<LocalResponse> listar(String status, String busca) {
        StatusAcessibilidade filtroStatus = (status == null || status.isBlank() || status.equalsIgnoreCase("Todos"))
                ? null : StatusAcessibilidade.de(status);
        String termo = busca == null ? "" : busca.trim().toLowerCase();
        Map<String, BigDecimal> limites = limitesDeOcupacao();
        return locais.listarComItens().stream()
                .filter(l -> filtroStatus == null || l.getStatusAcessibilidade() == filtroStatus)
                .filter(l -> termo.isEmpty()
                        || (l.getNome() + " " + l.getEndereco() + " " + l.getCategoria()).toLowerCase().contains(termo))
                .map(l -> paraResponse(l, limites.getOrDefault(l.getId(), LIMITE_OCUPACAO_PADRAO)))
                .toList();
    }

    @Transactional(readOnly = true)
    public LocalResponse buscar(String id) {
        Local local = carregar(id);
        return paraResponse(local, limiteDe(id));
    }

    @Transactional
    public LocalResponse criar(LocalRequest req) {
        if (req.id() == null || req.id().isBlank()) {
            throw new RequisicaoInvalidaException("Informe o id do local (ex.: centro-cultural).");
        }
        if (locais.existsById(req.id())) {
            throw new ConflitoException("Já existe um local com o id '" + req.id() + "'.");
        }
        Local local = new Local(req.id());
        aplicar(local, req);
        locais.save(local);
        return paraResponse(local, LIMITE_OCUPACAO_PADRAO);
    }

    @Transactional
    public LocalResponse atualizar(String id, LocalRequest req) {
        if (req.id() != null && !req.id().isBlank() && !req.id().equals(id)) {
            throw new RequisicaoInvalidaException("O id do corpo não confere com o id da URL.");
        }
        Local local = carregar(id);
        aplicar(local, req);
        return paraResponse(local, limiteDe(id));
    }

    @Transactional
    public void excluir(String id) {
        Local local = carregar(id);
        if (ocorrencias.existsByLocalId(id) || sensores.existsByLocalId(id)) {
            throw new ConflitoException("O local possui relatos ou sensores vinculados e não pode ser excluído.");
        }
        locais.delete(local);
    }

    Local carregar(String id) {
        return locais.buscarComItens(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Local '" + id + "' não encontrado."));
    }

    private void aplicar(Local local, LocalRequest req) {
        local.atualizarCadastro(req.nome().trim(), req.categoria().trim(), req.endereco().trim(),
                StatusAcessibilidade.de(req.statusAcessibilidade()), req.resumo(), req.confianca(),
                req.recursos(), req.obstaculos());
    }

    private BigDecimal limiteDe(String localId) {
        return sensores.findByTipoAndLocalId(TipoSensor.OCUPACAO, localId)
                .map(Sensor::getLimiteAlerta)
                .orElse(LIMITE_OCUPACAO_PADRAO);
    }

    private Map<String, BigDecimal> limitesDeOcupacao() {
        return sensores.listarComLocal().stream()
                .filter(s -> s.getTipo() == TipoSensor.OCUPACAO)
                .collect(Collectors.toMap(s -> s.getLocal().getId(), Sensor::getLimiteAlerta, (a, b) -> a));
    }

    /** Nível derivado (não armazenado): Alta >= limite do sensor; Moderada >= metade; senão Baixa. */
    static String nivelOcupacao(int ocupacao, BigDecimal limite) {
        BigDecimal valor = BigDecimal.valueOf(ocupacao);
        if (valor.compareTo(limite) >= 0) return "Alta";
        if (valor.multiply(BigDecimal.valueOf(2)).compareTo(limite) >= 0) return "Moderada";
        return "Baixa";
    }

    private LocalResponse paraResponse(Local l, BigDecimal limite) {
        LocalDateTime ultima = l.getUltimaLeitura();
        boolean desatualizada = ultima == null || ultima.isBefore(LocalDateTime.now().minusMinutes(minutosDesatualizada));
        return new LocalResponse(
                l.getId(),
                l.getNome(),
                l.getCategoria(),
                l.getEndereco(),
                l.getStatusAcessibilidade().getRotulo(),
                l.getResumo() == null ? "" : l.getResumo(),
                l.descricoesDe(TipoItem.RECURSO),
                l.descricoesDe(TipoItem.OBSTACULO),
                l.getOcupacao(),
                nivelOcupacao(l.getOcupacao(), limite),
                Datas.hora(ultima),
                Datas.relativa(ultima),
                desatualizada,
                l.getConfianca());
    }
}
