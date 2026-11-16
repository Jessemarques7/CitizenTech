package br.com.fiap.citizentech.model;

import java.text.Normalizer;

final class Textos {

    private Textos() {
    }

    /** "Em análise" -> "Em_analise", para comparar rótulos do app com os códigos do banco. */
    static String normalizar(String valor) {
        if (valor == null) return "";
        return Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace(' ', '_');
    }

    static boolean equivalentes(String a, String b) {
        return normalizar(a).equalsIgnoreCase(normalizar(b));
    }
}
