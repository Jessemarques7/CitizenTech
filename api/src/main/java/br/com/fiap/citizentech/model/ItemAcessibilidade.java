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
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_CT_ITEM_ACESSIBILIDADE")
public class ItemAcessibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_local")
    private Local local;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_item", nullable = false, length = 10)
    private TipoItem tipo;

    @Column(name = "ds_item", nullable = false, length = 150)
    private String descricao;

    protected ItemAcessibilidade() {
    }

    public ItemAcessibilidade(Local local, TipoItem tipo, String descricao) {
        this.local = local;
        this.tipo = tipo;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public TipoItem getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
}
