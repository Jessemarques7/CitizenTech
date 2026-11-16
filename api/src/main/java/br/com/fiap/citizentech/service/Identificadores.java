package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.exception.RequisicaoInvalidaException;

/** Converte os ids exibidos no app ("CT-1024", "AL-205") para os ids numéricos do banco e vice-versa. */
public final class Identificadores {

    public static final String PREFIXO_OCORRENCIA = "CT-";
    public static final String PREFIXO_ALERTA = "AL-";

    private Identificadores() {
    }

    public static String ocorrencia(Long id) {
        return PREFIXO_OCORRENCIA + id;
    }

    public static String alerta(Long id) {
        return PREFIXO_ALERTA + id;
    }

    /** Aceita "CT-1024" ou "1024". */
    public static Long numero(String valor, String prefixo) {
        String texto = valor == null ? "" : valor.trim();
        if (texto.regionMatches(true, 0, prefixo, 0, prefixo.length())) {
            texto = texto.substring(prefixo.length());
        }
        try {
            return Long.valueOf(texto);
        } catch (NumberFormatException e) {
            throw new RequisicaoInvalidaException("Identificador inválido: '" + valor + "'.");
        }
    }
}
