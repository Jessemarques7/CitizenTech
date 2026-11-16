package br.com.fiap.citizentech;

import br.com.fiap.citizentech.exception.RequisicaoInvalidaException;
import br.com.fiap.citizentech.model.StatusAcessibilidade;
import br.com.fiap.citizentech.model.StatusOcorrencia;
import br.com.fiap.citizentech.service.Identificadores;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Testes de unidade (sem Spring) das regras de negócio isoladas. */
class RegrasNegocioTest {

    @Test
    @DisplayName("U-01 Status aceita o rótulo do app e o código do banco")
    void statusPorRotuloOuCodigo() {
        assertThat(StatusOcorrencia.de("Em análise")).isEqualTo(StatusOcorrencia.EM_ANALISE);
        assertThat(StatusOcorrencia.de("em_analise")).isEqualTo(StatusOcorrencia.EM_ANALISE);
        assertThat(StatusAcessibilidade.de("Acessível")).isEqualTo(StatusAcessibilidade.ACESSIVEL);
        assertThatThrownBy(() -> StatusOcorrencia.de("Arquivada")).isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    @DisplayName("U-02 Ids exibidos (CT-1024) convertem para o número do banco")
    void identificadores() {
        assertThat(Identificadores.numero("CT-1024", "CT-")).isEqualTo(1024L);
        assertThat(Identificadores.numero("1024", "CT-")).isEqualTo(1024L);
        assertThatThrownBy(() -> Identificadores.numero("CT-abc", "CT-")).isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    @DisplayName("U-03 BCrypt gera hashes diferentes para a mesma senha e valida corretamente")
    void hashDeSenha() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String h1 = encoder.encode("Cidadao@2026");
        String h2 = encoder.encode("Cidadao@2026");
        assertThat(h1).isNotEqualTo(h2).doesNotContain("Cidadao@2026").startsWith("$2a$");
        assertThat(encoder.matches("Cidadao@2026", h1)).isTrue();
        assertThat(encoder.matches("outra", h1)).isFalse();
    }
}
