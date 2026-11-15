package br.com.fiap.citizentech.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class SensorDtos {

    private SensorDtos() {
    }

    /**
     * Leitura enviada por um sensor simulado. Identifique o sensor por {@code sensorCodigo}
     * ou pelo par {@code tipo} + {@code localId} (formato do app).
     */
    public record LeituraRequest(
            @Size(max = 30) String sensorCodigo,
            String tipo,
            String localId,
            String localNome,
            @NotNull(message = "é obrigatório")
            @PositiveOrZero(message = "não pode ser negativo")
            @DecimalMax(value = "100000", message = "valor fora da faixa aceita")
            BigDecimal valor,
            String unidade,
            LocalDateTime lidoEm
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ResultadoLeitura(
            String sensor,
            String tipo,
            String localId,
            BigDecimal valor,
            BigDecimal limite,
            boolean acimaDoLimite,
            String alertaGerado,
            List<String> alertasEncerrados
    ) {
    }

    public record LeiturasResponse(
            String message,
            int leituras,
            int alertasGerados,
            int alertasEncerrados,
            List<ResultadoLeitura> resultados
    ) {
    }

    public record SensorResponse(
            String codigo,
            String tipo,
            String localId,
            String localNome,
            String unidade,
            BigDecimal limiteAlerta,
            boolean ativo,
            BigDecimal ultimoValor,
            LocalDateTime ultimaLeitura,
            boolean leituraDesatualizada,
            boolean simulado
    ) {
    }

    public record LeituraResponse(Long id, BigDecimal valor, LocalDateTime lidoEm, boolean simulada) {
    }
}
