package br.com.fiap.citizentech.model;

import br.com.fiap.citizentech.exception.RequisicaoInvalidaException;

public enum StatusOcorrencia {
    RECEBIDA("Recebida", 15),
    EM_ANALISE("Em análise", 45),
    ENCAMINHADA("Encaminhada", 75),
    RESOLVIDA("Resolvida", 100);

    private final String rotulo;
    private final int progresso;

    StatusOcorrencia(String rotulo, int progresso) {
        this.rotulo = rotulo;
        this.progresso = progresso;
    }

    public String getRotulo() {
        return rotulo;
    }

    public int getProgresso() {
        return progresso;
    }

    /** Aceita o código ("EM_ANALISE") ou o rótulo exibido no app ("Em análise"). */
    public static StatusOcorrencia de(String valor) {
        for (StatusOcorrencia s : values()) {
            if (Textos.equivalentes(s.name(), valor) || Textos.equivalentes(s.rotulo, valor)) {
                return s;
            }
        }
        throw new RequisicaoInvalidaException("Status inválido: use Recebida, Em análise, Encaminhada ou Resolvida.");
    }
}
