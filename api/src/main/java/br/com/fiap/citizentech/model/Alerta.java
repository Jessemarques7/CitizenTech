package br.com.fiap.citizentech.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CT_ALERTA")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_leitura", unique = true)
    private LeituraSensor leitura;

    @Column(name = "tp_alerta", nullable = false, length = 20)
    private String tipo;

    @Column(name = "ds_titulo", nullable = false, length = 120)
    private String titulo;

    @Column(name = "ds_descricao", nullable = false, length = 400)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "st_alerta", nullable = false, length = 10)
    private StatusAlerta status = StatusAlerta.ATIVO;

    @Column(name = "dt_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "dt_encerramento")
    private LocalDateTime dataEncerramento;

    protected Alerta() {
    }

    public Alerta(LeituraSensor leitura, String tipo, String titulo, String descricao) {
        this.leitura = leitura;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataCriacao = leitura.getDataLeitura();
    }

    public void encerrar() {
        this.status = StatusAlerta.ENCERRADO;
        this.dataEncerramento = LocalDateTime.now();
    }

    public boolean isAtivo() {
        return status == StatusAlerta.ATIVO;
    }

    public Long getId() { return id; }
    public LeituraSensor getLeitura() { return leitura; }
    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public StatusAlerta getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataEncerramento() { return dataEncerramento; }
}
