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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CT_OCORRENCIA")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ocorrencia")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_local")
    private Local local;

    @Column(name = "ds_categoria", nullable = false, length = 40)
    private String categoria;

    @Column(name = "ds_descricao", nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "st_ocorrencia", nullable = false, length = 12)
    private StatusOcorrencia status = StatusOcorrencia.RECEBIDA;

    @Column(name = "dt_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "dt_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    protected Ocorrencia() {
    }

    public Ocorrencia(Usuario usuario, Local local, String categoria, String descricao) {
        this.usuario = usuario;
        this.local = local;
        this.categoria = categoria;
        this.descricao = descricao;
    }

    @PrePersist
    void aoCriar() {
        LocalDateTime agora = LocalDateTime.now();
        if (dataCriacao == null) dataCriacao = agora;
        dataAtualizacao = agora;
    }

    public void alterarStatus(StatusOcorrencia novoStatus) {
        this.status = novoStatus;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public boolean pertenceA(Long usuarioId) {
        return usuario.getId().equals(usuarioId);
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Local getLocal() { return local; }
    public String getCategoria() { return categoria; }
    public String getDescricao() { return descricao; }
    public StatusOcorrencia getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}
