package br.com.fiap.citizentech.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Datas no formato curto exibido no app: "Hoje, 14:32", "Ontem, 16:10", "12/11, 09:00". */
public final class Datas {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM");

    private Datas() {
    }

    public static String hora(LocalDateTime momento) {
        return momento == null ? "" : momento.format(HORA);
    }

    public static String relativa(LocalDateTime momento) {
        if (momento == null) return "Sem leitura";
        LocalDate dia = momento.toLocalDate();
        LocalDate hoje = LocalDate.now();
        String prefixo;
        if (dia.equals(hoje)) prefixo = "Hoje";
        else if (dia.equals(hoje.minusDays(1))) prefixo = "Ontem";
        else prefixo = momento.format(DIA);
        return prefixo + ", " + momento.format(HORA);
    }
}
