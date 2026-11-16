package br.com.fiap.citizentech.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.type.NumericBooleanConverter;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_CT_SENSOR")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sensor")
    private Long id;

    @Column(name = "cd_sensor", nullable = false, unique = true, length = 30)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_sensor", nullable = false, length = 10)
    private TipoSensor tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_local")
    private Local local;

    @Column(name = "ds_unidade", nullable = false, length = 10)
    private String unidade;

    @Column(name = "vl_limite_alerta", nullable = false, precision = 8, scale = 2)
    private BigDecimal limiteAlerta;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "fl_ativo", nullable = false)
    private boolean ativo = true;

    protected Sensor() {
    }

    public boolean ultrapassouLimite(BigDecimal valor) {
        return valor.compareTo(limiteAlerta) >= 0;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public TipoSensor getTipo() { return tipo; }
    public Local getLocal() { return local; }
    public String getUnidade() { return unidade; }
    public BigDecimal getLimiteAlerta() { return limiteAlerta; }
    public boolean isAtivo() { return ativo; }
}
