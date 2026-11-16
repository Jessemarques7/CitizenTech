package br.com.fiap.citizentech.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.type.NumericBooleanConverter;

@Entity
@Table(name = "TB_CT_PREFERENCIA")
public class Preferencia {

    @Id
    @Column(name = "id_usuario")
    private Long usuarioId;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "fl_evitar_escadas", nullable = false)
    private boolean evitarEscadas = true;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "fl_priorizar_rampas", nullable = false)
    private boolean priorizarRampas = true;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "fl_alertas_visuais", nullable = false)
    private boolean alertasVisuais = true;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "fl_alto_contraste", nullable = false)
    private boolean altoContraste = false;

    protected Preferencia() {
    }

    public Preferencia(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void atualizar(boolean evitarEscadas, boolean priorizarRampas, boolean alertasVisuais, boolean altoContraste) {
        this.evitarEscadas = evitarEscadas;
        this.priorizarRampas = priorizarRampas;
        this.alertasVisuais = alertasVisuais;
        this.altoContraste = altoContraste;
    }

    public Long getUsuarioId() { return usuarioId; }
    public boolean isEvitarEscadas() { return evitarEscadas; }
    public boolean isPriorizarRampas() { return priorizarRampas; }
    public boolean isAlertasVisuais() { return alertasVisuais; }
    public boolean isAltoContraste() { return altoContraste; }
}
