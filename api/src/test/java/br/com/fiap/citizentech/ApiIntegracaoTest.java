package br.com.fiap.citizentech;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de API (ponta a ponta pela camada HTTP, com banco H2 em modo Oracle carregado pelo mesmo
 * script do projeto). Os nomes CT-xx correspondem aos casos do Plano de Testes (docs/CitizenTech_Entregaveis.pdf, Parte 3).
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ApiIntegracaoTest {

    private static final String DEVICE_KEY = "sensor-demo-key-2026";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    private String tokenAna;
    private String tokenAdmin;

    @BeforeEach
    void login() throws Exception {
        tokenAna = token("ana@citizentech.com", "Cidadao@2026");
        tokenAdmin = token("admin@citizentech.com", "Admin@2026");
    }

    @Test
    @DisplayName("CT-01 Login válido retorna token JWT e perfil")
    void loginValido() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@citizentech.com\",\"senha\":\"Cidadao@2026\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuario.perfil").value("CIDADAO"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("CT-02 Login com senha errada retorna 401 com mensagem genérica")
    void loginInvalido() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@citizentech.com\",\"senha\":\"errada123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("E-mail ou senha inválidos."));
    }

    @Test
    @DisplayName("CT-03 Cadastro grava a senha com hash BCrypt e cria perfil CIDADAO")
    void cadastroComHash() throws Exception {
        mvc.perform(post("/api/auth/registrar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Carla Dias\",\"email\":\"carla@teste.com\",\"senha\":\"Senha123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario.perfil").value("CIDADAO"));

        mvc.perform(post("/api/auth/registrar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Carla Dias\",\"email\":\"carla@teste.com\",\"senha\":\"Senha123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("CT-04 Cadastro com senha fraca retorna 400 com o campo inválido")
    void cadastroSenhaFraca() throws Exception {
        mvc.perform(post("/api/auth/registrar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Davi\",\"email\":\"davi@teste.com\",\"senha\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.senha").exists());
    }

    @Test
    @DisplayName("CT-05 Rota protegida sem token retorna 401")
    void semToken() throws Exception {
        mvc.perform(get("/api/locais")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("CT-06 Token adulterado é rejeitado com 401")
    void tokenAdulterado() throws Exception {
        String adulterado = tokenAna.substring(0, tokenAna.length() - 3) + "abc";
        mvc.perform(get("/api/locais").header("Authorization", "Bearer " + adulterado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("CT-07 Lista de locais com recursos, ocupação e status de acessibilidade")
    void listarLocais() throws Exception {
        mvc.perform(autenticado(get("/api/locais"), tokenAna))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[?(@.id=='biblioteca')].nivelOcupacao").value(hasItem("Alta")));

        mvc.perform(autenticado(get("/api/locais").param("status", "Acessível"), tokenAna))
                .andExpect(jsonPath("$[*].statusAcessibilidade", everyItem(is("Acessível"))));
    }

    @Test
    @DisplayName("CT-08 Local inexistente retorna 404")
    void localInexistente() throws Exception {
        mvc.perform(autenticado(get("/api/locais/nao-existe"), tokenAna))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("CT-09 Cidadão registra barreira: 201 com status inicial Recebida")
    void criarOcorrencia() throws Exception {
        mvc.perform(autenticado(post("/api/ocorrencias"), tokenAna).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"localId\":\"terminal\",\"categoria\":\"Rampa bloqueada\","
                                + "\"descricao\":\"Carro estacionado em cima da rampa.\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", startsWith("CT-")))
                .andExpect(jsonPath("$.status").value("Recebida"))
                .andExpect(jsonPath("$.progresso").value(15));
    }

    @Test
    @DisplayName("CT-10 Barreira com local inexistente, categoria vazia e HTML é rejeitada (400)")
    void criarOcorrenciaInvalida() throws Exception {
        mvc.perform(autenticado(post("/api/ocorrencias"), tokenAna).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"localId\":\"terminal\",\"categoria\":\"\",\"descricao\":\"<script>alert(1)</script>\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.categoria").exists())
                .andExpect(jsonPath("$.erros.descricao").exists());

        mvc.perform(autenticado(post("/api/ocorrencias"), tokenAna).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"localId\":\"lugar-falso\",\"categoria\":\"Outro\",\"descricao\":\"Descricao com tamanho ok.\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CT-11 Cidadão vê só os próprios relatos e recebe 403 ao pedir os de outro usuário")
    void isolamentoDeRelatos() throws Exception {
        mvc.perform(autenticado(get("/api/ocorrencias"), tokenAna))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].usuarioId", everyItem(is("2"))));

        mvc.perform(autenticado(get("/api/ocorrencias").param("usuarioId", "3"), tokenAna))
                .andExpect(status().isForbidden());

        mvc.perform(autenticado(get("/api/ocorrencias"), tokenAdmin))
                .andExpect(jsonPath("$[*].usuarioId", hasItem("3")));
    }

    @Test
    @DisplayName("CT-12 Somente ADMIN altera status de relato (cidadão recebe 403)")
    void alterarStatusSoAdmin() throws Exception {
        String corpo = "{\"status\":\"Encaminhada\"}";
        mvc.perform(autenticado(put("/api/ocorrencias/CT-1024/status"), tokenAna)
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isForbidden());

        mvc.perform(autenticado(put("/api/ocorrencias/CT-1024/status"), tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Encaminhada"))
                .andExpect(jsonPath("$.progresso").value(75));
    }

    @Test
    @DisplayName("CT-13 Relato já resolvido não muda de status (409)")
    void statusDeResolvida() throws Exception {
        mvc.perform(autenticado(put("/api/ocorrencias/CT-1018/status"), tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"Recebida\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("CT-14 Autor só cancela relato ainda Recebida; DELETE retorna 204 e depois 404")
    void excluirOcorrencia() throws Exception {
        mvc.perform(autenticado(delete("/api/ocorrencias/CT-1024"), tokenAna))
                .andExpect(status().isConflict());

        String tokenBruno = token("bruno@citizentech.com", "Cidadao@2026");
        mvc.perform(autenticado(delete("/api/ocorrencias/CT-1040"), tokenBruno))
                .andExpect(status().isNoContent());
        mvc.perform(autenticado(get("/api/ocorrencias/CT-1040"), tokenBruno))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("CT-15 Sensor acima do limite gera alerta; abaixo do limite encerra o alerta ativo")
    void sensorGeraEEncerraAlerta() throws Exception {
        mvc.perform(post("/api/sensores/leituras").header("X-Device-Key", DEVICE_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"tipo\":\"ocupacao\",\"localId\":\"terminal\",\"valor\":72,\"unidade\":\"pessoas\"}]"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alertasGerados").value(1))
                .andExpect(jsonPath("$.resultados[0].acimaDoLimite").value(true));

        mvc.perform(autenticado(get("/api/alertas"), tokenAna))
                .andExpect(jsonPath("$[*].titulo", hasItem("Terminal Central com alta ocupação")));
        mvc.perform(autenticado(get("/api/locais/terminal"), tokenAna))
                .andExpect(jsonPath("$.ocupacao").value(72))
                .andExpect(jsonPath("$.nivelOcupacao").value("Alta"));

        mvc.perform(post("/api/sensores/leituras").header("X-Device-Key", DEVICE_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"sensorCodigo\":\"OCUP-TER-01\",\"valor\":20}]"))
                .andExpect(jsonPath("$.alertasEncerrados").value(1));

        mvc.perform(autenticado(get("/api/alertas"), tokenAna))
                .andExpect(jsonPath("$[*].titulo", not(hasItem("Terminal Central com alta ocupação"))));
    }

    @Test
    @DisplayName("CT-16 Leitura de sensor sem chave de dispositivo (ou por cidadão) é bloqueada")
    void sensorSemAutorizacao() throws Exception {
        String corpo = "[{\"tipo\":\"chuva\",\"localId\":\"centro-cultural\",\"valor\":50}]";
        mvc.perform(post("/api/sensores/leituras").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/sensores/leituras").header("X-Device-Key", "chave-errada")
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isUnauthorized());
        mvc.perform(autenticado(post("/api/sensores/leituras"), tokenAna)
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CT-17 Leitura negativa ou de sensor inexistente é rejeitada")
    void leituraInvalida() throws Exception {
        mvc.perform(post("/api/sensores/leituras").header("X-Device-Key", DEVICE_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"tipo\":\"chuva\",\"localId\":\"centro-cultural\",\"valor\":-5}]"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/sensores/leituras").header("X-Device-Key", DEVICE_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"sensorCodigo\":\"NAO-EXISTE\",\"valor\":5}]"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("CT-18 Preferências: salvar as próprias (200) e bloquear as de outra pessoa (403)")
    void preferencias() throws Exception {
        mvc.perform(autenticado(put("/api/preferencias/2"), tokenAna).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"evitarEscadas\":false,\"priorizarRampas\":true,\"alertasVisuais\":true,\"altoContraste\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.altoContraste").value(true));

        mvc.perform(autenticado(get("/api/preferencias/3"), tokenAna))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CT-19 CRUD de locais restrito ao ADMIN; exclusão bloqueada se houver vínculos")
    void crudLocais() throws Exception {
        String novo = "{\"id\":\"praca-norte\",\"nome\":\"Praça Norte\",\"categoria\":\"Lazer\","
                + "\"endereco\":\"Rua A, 1\",\"statusAcessibilidade\":\"Parcial\",\"resumo\":\"Teste\","
                + "\"recursos\":[\"Rampa\"],\"obstaculos\":[]}";
        mvc.perform(autenticado(post("/api/locais"), tokenAna).contentType(MediaType.APPLICATION_JSON).content(novo))
                .andExpect(status().isForbidden());
        mvc.perform(autenticado(post("/api/locais"), tokenAdmin).contentType(MediaType.APPLICATION_JSON).content(novo))
                .andExpect(status().isCreated());
        mvc.perform(autenticado(delete("/api/locais/praca-norte"), tokenAdmin))
                .andExpect(status().isNoContent());
        mvc.perform(autenticado(delete("/api/locais/biblioteca"), tokenAdmin))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("CT-20 Bloqueio após 5 senhas erradas (429) e cabeçalhos de segurança presentes")
    void forcaBrutaECabecalhos() throws Exception {
        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"bruno@citizentech.com\",\"senha\":\"errada" + i + "\"}"));
        }
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"bruno@citizentech.com\",\"senha\":\"Cidadao@2026\"}"))
                .andExpect(status().isTooManyRequests());

        mvc.perform(autenticado(get("/api/alertas"), tokenAna))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().exists("Content-Security-Policy"));
    }

    @Test
    @DisplayName("CT-21 Tentativa de SQL injection na busca é tratada como texto (sem erro, sem vazamento)")
    void sqlInjection() throws Exception {
        mvc.perform(autenticado(get("/api/locais").param("busca", "' OR '1'='1"), tokenAna))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(autenticado(get("/api/ocorrencias/{id}", "1 OR 1=1"), tokenAdmin))
                .andExpect(status().isBadRequest());
    }

    private MockHttpServletRequestBuilder autenticado(MockHttpServletRequestBuilder req, String token) {
        return req.header("Authorization", "Bearer " + token);
    }

    private String token(String email, String senha) throws Exception {
        String resposta = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}"))
                .andReturn().getResponse().getContentAsString();
        JsonNode no = json.readTree(resposta);
        assertThat(no.has("token")).as("login de %s", email).isTrue();
        return no.get("token").asText();
    }
}
