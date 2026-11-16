package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.config.CitizenTechProperties;
import br.com.fiap.citizentech.dto.SensorDtos.LeituraRequest;
import br.com.fiap.citizentech.dto.SensorDtos.LeituraResponse;
import br.com.fiap.citizentech.dto.SensorDtos.LeiturasResponse;
import br.com.fiap.citizentech.dto.SensorDtos.ResultadoLeitura;
import br.com.fiap.citizentech.dto.SensorDtos.SensorResponse;
import br.com.fiap.citizentech.exception.ConflitoException;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.exception.RequisicaoInvalidaException;
import br.com.fiap.citizentech.model.Alerta;
import br.com.fiap.citizentech.model.LeituraSensor;
import br.com.fiap.citizentech.model.Sensor;
import br.com.fiap.citizentech.model.TipoSensor;
import br.com.fiap.citizentech.repository.AlertaRepository;
import br.com.fiap.citizentech.repository.LeituraSensorRepository;
import br.com.fiap.citizentech.repository.SensorRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Regras da simulação de IoT:
 * <ol>
 *   <li>cada leitura é gravada em TB_CT_LEITURA_SENSOR, marcada como simulada;</li>
 *   <li>sensor de ocupação atualiza a ocupação e a hora da última leitura do local;</li>
 *   <li>valor &gt;= limite do sensor gera um alerta ATIVO (o anterior do mesmo sensor é substituído);</li>
 *   <li>valor abaixo do limite encerra os alertas ativos daquele sensor (situação normalizada).</li>
 * </ol>
 */
@Service
public class SensorService {

    public static final int MAX_LEITURAS_POR_LOTE = 50;

    private final SensorRepository sensores;
    private final LeituraSensorRepository leituras;
    private final AlertaRepository alertas;
    private final long minutosDesatualizada;

    public SensorService(SensorRepository sensores, LeituraSensorRepository leituras, AlertaRepository alertas,
                         CitizenTechProperties props) {
        this.sensores = sensores;
        this.leituras = leituras;
        this.alertas = alertas;
        this.minutosDesatualizada = props.iot().minutosDesatualizada();
    }

    @Transactional
    public LeiturasResponse registrar(List<LeituraRequest> lote) {
        if (lote == null || lote.isEmpty()) {
            throw new RequisicaoInvalidaException("Envie ao menos uma leitura.");
        }
        if (lote.size() > MAX_LEITURAS_POR_LOTE) {
            throw new RequisicaoInvalidaException("Envie no máximo " + MAX_LEITURAS_POR_LOTE + " leituras por requisição.");
        }
        List<ResultadoLeitura> resultados = new ArrayList<>();
        int gerados = 0;
        int encerrados = 0;
        for (LeituraRequest req : lote) {
            ResultadoLeitura r = processar(req);
            resultados.add(r);
            if (r.alertaGerado() != null) gerados++;
            encerrados += r.alertasEncerrados().size();
        }
        String mensagem = gerados > 0
                ? gerados + " alerta(s) gerado(s) a partir das leituras simuladas."
                : "Leituras registradas. Nenhum limite ultrapassado.";
        return new LeiturasResponse(mensagem, resultados.size(), gerados, encerrados, resultados);
    }

    private ResultadoLeitura processar(LeituraRequest req) {
        Sensor sensor = localizarSensor(req);
        if (!sensor.isAtivo()) {
            throw new ConflitoException("O sensor " + sensor.getCodigo() + " está desativado.");
        }
        if (req.unidade() != null && !req.unidade().isBlank() && !req.unidade().equalsIgnoreCase(sensor.getUnidade())) {
            throw new RequisicaoInvalidaException("Unidade '" + req.unidade() + "' não confere com a do sensor "
                    + sensor.getCodigo() + " (" + sensor.getUnidade() + ").");
        }
        BigDecimal valor = req.valor().setScale(2, RoundingMode.HALF_UP);
        LocalDateTime momento = Optional.ofNullable(req.lidoEm()).orElse(LocalDateTime.now());
        if (momento.isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new RequisicaoInvalidaException("A data da leitura não pode estar no futuro.");
        }

        LeituraSensor leitura = leituras.save(new LeituraSensor(sensor, valor, momento));

        if (sensor.getTipo() == TipoSensor.OCUPACAO) {
            sensor.getLocal().registrarOcupacao(valor.setScale(0, RoundingMode.HALF_UP).intValue(), momento);
        }

        List<String> encerrados = new ArrayList<>();
        for (Alerta ativo : alertas.ativosDoSensor(sensor.getId())) {
            ativo.encerrar();
            encerrados.add(Identificadores.alerta(ativo.getId()));
        }

        boolean acima = sensor.ultrapassouLimite(valor);
        String gerado = null;
        if (acima) {
            Alerta alerta = alertas.save(criarAlerta(sensor, leitura));
            gerado = Identificadores.alerta(alerta.getId());
        }
        return new ResultadoLeitura(sensor.getCodigo(), sensor.getTipo().getCodigoApi(), sensor.getLocal().getId(),
                valor, sensor.getLimiteAlerta(), acima, gerado, encerrados);
    }

    private Sensor localizarSensor(LeituraRequest req) {
        if (req.sensorCodigo() != null && !req.sensorCodigo().isBlank()) {
            return sensores.findByCodigoIgnoreCase(req.sensorCodigo().trim())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Sensor '" + req.sensorCodigo() + "' não cadastrado."));
        }
        if (req.tipo() == null || req.localId() == null) {
            throw new RequisicaoInvalidaException("Informe sensorCodigo ou o par tipo + localId.");
        }
        TipoSensor tipo = TipoSensor.de(req.tipo());
        return sensores.findByTipoAndLocalId(tipo, req.localId().trim())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Não há sensor de " + tipo.getCodigoApi() + " no local '" + req.localId() + "'."));
    }

    private Alerta criarAlerta(Sensor sensor, LeituraSensor leitura) {
        String local = sensor.getLocal().getNome();
        String valor = leitura.getValor().stripTrailingZeros().toPlainString();
        String limite = sensor.getLimiteAlerta().stripTrailingZeros().toPlainString();
        return switch (sensor.getTipo()) {
            case CHUVA -> new Alerta(leitura, sensor.getTipo().getTipoAlerta(),
                    "Chuva intensa no entorno do " + local,
                    "Leitura SIMULADA de " + valor + " mm/h ultrapassou o limite de demonstração (" + limite
                            + " mm/h). Calçadas e rampas podem estar escorregadias ou alagadas. "
                            + "Não é um alerta oficial da Defesa Civil.");
            case OCUPACAO -> new Alerta(leitura, sensor.getTipo().getTipoAlerta(),
                    local + " com alta ocupação",
                    "Leitura SIMULADA de " + valor + " pessoas ultrapassou o limite de " + limite
                            + " pessoas. Considere outro horário. Contagem agregada, sem identificação de pessoas.");
        };
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> listar() {
        LocalDateTime limiteAtualizacao = LocalDateTime.now().minusMinutes(minutosDesatualizada);
        return sensores.listarComLocal().stream().map(s -> {
            Optional<LeituraSensor> ultima = leituras.ultimaDoSensor(s.getId());
            LocalDateTime quando = ultima.map(LeituraSensor::getDataLeitura).orElse(null);
            return new SensorResponse(s.getCodigo(), s.getTipo().getCodigoApi(), s.getLocal().getId(),
                    s.getLocal().getNome(), s.getUnidade(), s.getLimiteAlerta(), s.isAtivo(),
                    ultima.map(LeituraSensor::getValor).orElse(null), quando,
                    quando == null || quando.isBefore(limiteAtualizacao),
                    ultima.map(LeituraSensor::isSimulada).orElse(true));
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<LeituraResponse> leiturasDo(String codigo, int limite) {
        if (limite < 1 || limite > 200) {
            throw new RequisicaoInvalidaException("limite deve estar entre 1 e 200.");
        }
        Sensor sensor = sensores.findByCodigoIgnoreCase(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sensor '" + codigo + "' não cadastrado."));
        return leituras.ultimasDoSensor(sensor.getId(), Pageable.ofSize(limite)).stream()
                .map(l -> new LeituraResponse(l.getId(), l.getValor(), l.getDataLeitura(), l.isSimulada()))
                .toList();
    }
}
