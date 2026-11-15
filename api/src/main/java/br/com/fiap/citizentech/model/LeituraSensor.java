package br.com.fiap.citizentech.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.type.NumericBooleanConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CT_LEITURA_SENSOR")
public class LeituraSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_leitura")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sensor")
    private Sensor sensor;

    @Column(name = "vl_leitura", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "dt_leitura", nullable = false)
    private LocalDateTime dataLeitura;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "fl_simulada", nullable = false)
    private boolean simulada = true;

    protected LeituraSensor() {
    }

    public LeituraSensor(Sensor sensor, BigDecimal valor, LocalDateTime dataLeitura) {
        this.sensor = sensor;
        this.valor = valor;
        this.dataLeitura = dataLeitura;
    }

    public Long getId() { return id; }
    public Sensor getSensor() { return sensor; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getDataLeitura() { return dataLeitura; }
    public boolean isSimulada() { return simulada; }
}
