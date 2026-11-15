package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.dto.AlertaResponse;
import br.com.fiap.citizentech.dto.Datas;
import br.com.fiap.citizentech.exception.ConflitoException;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.model.Alerta;
import br.com.fiap.citizentech.model.LeituraSensor;
import br.com.fiap.citizentech.model.Sensor;
import br.com.fiap.citizentech.model.StatusAlerta;
import br.com.fiap.citizentech.model.TipoSensor;
import br.com.fiap.citizentech.repository.AlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.fiap.citizentech.service.Identificadores.PREFIXO_ALERTA;

@Service
public class AlertaService {

    private final AlertaRepository alertas;

    public AlertaService(AlertaRepository alertas) {
        this.alertas = alertas;
    }

    /** Por padrão só alertas ativos; {@code incluirEncerrados=true} traz o histórico. */
    @Transactional(readOnly = true)
    public List<AlertaResponse> listar(boolean incluirEncerrados) {
        return alertas.buscar(incluirEncerrados ? null : StatusAlerta.ATIVO).stream()
                .map(AlertaService::paraResponse)
                .toList();
    }

    @Transactional
    public AlertaResponse encerrar(String id) {
        Alerta alerta = alertas.findById(Identificadores.numero(id, PREFIXO_ALERTA))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta '" + id + "' não encontrado."));
        if (!alerta.isAtivo()) {
            throw new ConflitoException("O alerta já está encerrado.");
        }
        alerta.encerrar();
        return paraResponse(alerta);
    }

    static AlertaResponse paraResponse(Alerta a) {
        LeituraSensor leitura = a.getLeitura();
        Sensor sensor = leitura.getSensor();
        return new AlertaResponse(
                Identificadores.alerta(a.getId()),
                TipoSensor.rotuloDoAlerta(a.getTipo()),
                a.getTitulo(),
                sensor.getLocal().getNome(),
                sensor.getLocal().getId(),
                leitura.getValor().doubleValue(),
                sensor.getUnidade(),
                Datas.relativa(a.getDataCriacao()),
                a.getDescricao(),
                a.getStatus().name(),
                sensor.getCodigo(),
                leitura.isSimulada(),
                a.getDataCriacao());
    }
}
