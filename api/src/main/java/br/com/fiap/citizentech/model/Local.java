package br.com.fiap.citizentech.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_CT_LOCAL")
public class Local {

    @Id
    @Column(name = "id_local", length = 40)
    private String id;

    @Column(name = "nm_local", nullable = false, length = 100)
    private String nome;

    @Column(name = "ds_categoria", nullable = false, length = 50)
    private String categoria;

    @Column(name = "ds_endereco", nullable = false, length = 150)
    private String endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "st_acessibilidade", nullable = false, length = 12)
    private StatusAcessibilidade statusAcessibilidade;

    @Column(name = "ds_resumo", length = 400)
    private String resumo;

    @Column(name = "ds_confianca", nullable = false, length = 60)
    private String confianca = "Cadastro local";

    /** Contagem agregada de pessoas (nunca identifica indivíduos). */
    @Column(name = "nr_ocupacao", nullable = false)
    private int ocupacao;

    @Column(name = "dt_ultima_leitura")
    private LocalDateTime ultimaLeitura;

    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<ItemAcessibilidade> itens = new ArrayList<>();

    protected Local() {
    }

    public Local(String id) {
        this.id = id;
    }

    public void atualizarCadastro(String nome, String categoria, String endereco, StatusAcessibilidade status,
                                  String resumo, String confianca, List<String> recursos, List<String> obstaculos) {
        this.nome = nome;
        this.categoria = categoria;
        this.endereco = endereco;
        this.statusAcessibilidade = status;
        this.resumo = resumo;
        if (confianca != null && !confianca.isBlank()) this.confianca = confianca;
        this.itens.clear();
        recursos.forEach(r -> itens.add(new ItemAcessibilidade(this, TipoItem.RECURSO, r)));
        obstaculos.forEach(o -> itens.add(new ItemAcessibilidade(this, TipoItem.OBSTACULO, o)));
    }

    public void registrarOcupacao(int pessoas, LocalDateTime momento) {
        this.ocupacao = pessoas;
        this.ultimaLeitura = momento;
    }

    public List<String> descricoesDe(TipoItem tipo) {
        return itens.stream().filter(i -> i.getTipo() == tipo).map(ItemAcessibilidade::getDescricao).toList();
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public String getEndereco() { return endereco; }
    public StatusAcessibilidade getStatusAcessibilidade() { return statusAcessibilidade; }
    public String getResumo() { return resumo; }
    public String getConfianca() { return confianca; }
    public int getOcupacao() { return ocupacao; }
    public LocalDateTime getUltimaLeitura() { return ultimaLeitura; }
    public List<ItemAcessibilidade> getItens() { return itens; }
}
