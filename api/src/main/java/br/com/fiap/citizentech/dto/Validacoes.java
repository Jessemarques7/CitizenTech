package br.com.fiap.citizentech.dto;

/** Regras de validação de entrada reutilizadas pelos DTOs. */
public final class Validacoes {

    /** Bloqueia os caracteres usados para injetar HTML/script (proteção contra XSS armazenado). */
    public static final String SEM_HTML = "^[^<>]*$";
    public static final String MSG_SEM_HTML = "não pode conter os caracteres < ou >";

    /** Mínimo 8 caracteres, com ao menos uma letra e um número. */
    public static final String SENHA_FORTE = "^(?=.*[A-Za-z])(?=.*\\d).{8,72}$";

    /** Identificador de local no formato slug: "biblioteca", "centro-cultural". */
    public static final String SLUG = "^[a-z0-9]+(-[a-z0-9]+)*$";

    private Validacoes() {
    }
}
