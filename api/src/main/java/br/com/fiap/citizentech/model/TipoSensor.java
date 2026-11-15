package br.com.fiap.citizentech.model;

import br.com.fiap.citizentech.exception.RequisicaoInvalidaException;

public enum TipoSensor {
    OCUPACAO("ocupacao", "OCUPACAO", "Ocupação"),
    CHUVA("chuva", "CLIMA", "Clima");

    private final String codigoApi;
    private final String tipoAlerta;
    private final String rotuloAlerta;

    TipoSensor(String codigoApi, String tipoAlerta, String rotuloAlerta) {
        this.codigoApi = codigoApi;
        this.tipoAlerta = tipoAlerta;
        this.rotuloAlerta = rotuloAlerta;
    }

    public String getCodigoApi() {
        return codigoApi;
    }

    public String getTipoAlerta() {
        return tipoAlerta;
    }

    public static TipoSensor de(String valor) {
        for (TipoSensor t : values()) {
            if (Textos.equivalentes(t.name(), valor) || Textos.equivalentes(t.codigoApi, valor)) {
                return t;
            }
        }
        throw new RequisicaoInvalidaException("Tipo de sensor inválido: use 'ocupacao' ou 'chuva'.");
    }

    /** "CLIMA" -> "Clima", "OCUPACAO" -> "Ocupação" (rótulo exibido no app). */
    public static String rotuloDoAlerta(String tipoAlerta) {
        for (TipoSensor t : values()) {
            if (t.tipoAlerta.equalsIgnoreCase(tipoAlerta)) return t.rotuloAlerta;
        }
        return tipoAlerta;
    }
}
