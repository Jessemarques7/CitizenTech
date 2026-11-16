package br.com.fiap.citizentech.model;

import br.com.fiap.citizentech.exception.RequisicaoInvalidaException;

public enum StatusAcessibilidade {
    ACESSIVEL("Acessível"),
    PARCIAL("Parcial"),
    INACESSIVEL("Inacessível");

    private final String rotulo;

    StatusAcessibilidade(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    /** Aceita o código ("PARCIAL") ou o rótulo exibido no app ("Parcial", "Acessível"). */
    public static StatusAcessibilidade de(String valor) {
        for (StatusAcessibilidade s : values()) {
            if (Textos.equivalentes(s.name(), valor) || Textos.equivalentes(s.rotulo, valor)) {
                return s;
            }
        }
        throw new RequisicaoInvalidaException("Status de acessibilidade inválido: use Acessível, Parcial ou Inacessível.");
    }
}
