# CitizenTech · Acessibilidade Urbana Inteligente

**Mais informação. Mais autonomia.** Global Solution FIAP 2026/2 (3ESOA).

Pessoas com deficiência ou mobilidade reduzida consultam a acessibilidade e a ocupação de locais públicos, relatam barreiras (calçada quebrada, rampa bloqueada…) e recebem alertas gerados por sensores. A prefeitura/zeladoria (perfil administrador) acompanha e atualiza os relatos.

- **Vídeo pitch:** [LINK PÚBLICO DO VÍDEO]
- **PDF dos entregáveis** (DER, plano de testes, evidências, segurança, IoT): [`docs/CitizenTech_Entregaveis.pdf`](docs/CitizenTech_Entregaveis.pdf)
- **Integrantes:** ver [`integrantes.txt`](integrantes.txt)

## Estrutura

| Parte | Pasta | Tecnologia |
|---|---|---|
| 1. Banco de dados | [`database/`](database) | Oracle: `citizentech.sql` (DDL + carga), `consultas.sql` (8 consultas) |
| 2. API REST | [`api/`](api) | Java 17, Spring Boot 3.4, JPA, Spring Security + JWT, Swagger |
| 3. Testes | [`api/src/test`](api/src/test), [`docs/evidencias`](docs/evidencias) | JUnit 5 + MockMvc (24 testes), script de evidências, Postman |
| 4. App mobile | [`app/`](app) | Android, Kotlin, Jetpack Compose, Retrofit |
| 5. Segurança | [`api/.../security`](api/src/main/java/br/com/fiap/citizentech/security), [`SecurityConfig`](api/src/main/java/br/com/fiap/citizentech/config/SecurityConfig.java) | BCrypt, JWT, perfis ADMIN/CIDADAO/DEVICE |
| 6. IoT | [`iot/`](iot) | Python (sem dependências) + `sensores.json` |
| Documentação da API | [`docs/api`](docs/api) | `openapi.json` (Swagger) e `CitizenTech.postman_collection.json` |

## Pré-requisitos

- **Java 17+** e **Maven 3.9+** (API)
- **Python 3.10+** (simulador IoT e scripts de documentação)
- **Android Studio** (app), com um emulador Android
- Opcional: conta no **Oracle da FIAP** e SQL Developer

## 1. Banco de dados (Oracle)

1. Abra o SQL Developer conectado ao Oracle da FIAP (`oracle.fiap.com.br:1521/ORCL`) com seu RM.
2. Abra [`database/citizentech.sql`](database/citizentech.sql) e execute como script (**F5**). Ele apaga e recria as tabelas `TB_CT_*` e insere a carga inicial.
3. Execute [`database/consultas.sql`](database/consultas.sql) para ver as consultas de uso.

> A API também roda **sem Oracle**: por padrão ela sobe um H2 em memória **em modo Oracle**, carregado com o mesmo modelo e a mesma carga (arquivos gerados a partir do script Oracle por `python database/gerar_h2.py`).

## 2. API REST

```bash
cd api
mvn spring-boot:run
```

- API: `http://localhost:8080/api`
- **Swagger:** http://localhost:8080/swagger-ui.html (faça `POST /api/auth/login`, copie o `token` e clique em **Authorize**)
- Se a porta 8080 estiver ocupada: `PORT=8081 mvn spring-boot:run` (no Windows/cmd: `set PORT=8081` antes)

**Usando o Oracle da FIAP** (depois de rodar o script do passo 1):

```bash
# Windows (cmd)
set ORACLE_USER=rm12345
set ORACLE_PASSWORD=suasenha
mvn spring-boot:run -Dspring-boot.run.profiles=oracle
```

**Usuários de teste** (senhas guardadas com hash BCrypt no banco):

| Perfil | E-mail | Senha |
|---|---|---|
| ADMIN | `admin@citizentech.com` | `Admin@2026` |
| CIDADAO | `ana@citizentech.com` | `Cidadao@2026` |
| CIDADAO | `bruno@citizentech.com` | `Cidadao@2026` |

Variáveis de ambiente opcionais: `CITIZENTECH_JWT_SECRET` (segredo do JWT, 32+ caracteres), `CITIZENTECH_DEVICE_KEY` (chave dos sensores, padrão `sensor-demo-key-2026`), `CITIZENTECH_CORS_ORIGINS`.

**Postman:** importe [`docs/api/CitizenTech.postman_collection.json`](docs/api/CitizenTech.postman_collection.json), rode a pasta *Auth* primeiro (ela guarda os tokens) e depois as demais.

## 3. Testes

```bash
cd api
mvn test                          # 24 testes (21 de API + 3 de unidade)
mvn surefire-report:report        # relatório HTML em api/target/reports/surefire.html
```

Evidências contra a API em execução (gera `docs/evidencias/execucao_api.txt`):

```bash
python docs/evidencias/coletar_evidencias.py --api http://localhost:8080/api
```

O plano de testes completo (cenário, entrada, saída esperada e status) está na Parte 3 do PDF.

## 4. App mobile (Android)

1. Com a API rodando, abra a **raiz** deste repositório no Android Studio e aguarde o Gradle sincronizar.
2. Rode o app em um **emulador**. A URL padrão `http://10.0.2.2:8080/api/` aponta para a API no seu computador.
3. Entre com um usuário de teste. Com o **admin**, a aba *Relatos* mostra todos os relatos e o botão para avançar o status.
4. Celular físico: em *Configurar endereço da API* informe `http://IP-DO-PC:8080/api/` e adicione esse IP em `app/src/main/res/xml/network_security_config.xml`.
5. O botão **Explorar no modo demonstração** usa dados locais (Room), sem API.

Telas: Login/Cadastro, Explorar (lista), Detalhe do local, Novo relato, Relatos, Alertas e Perfil.

## 5. Segurança

Senhas com **BCrypt**, login com **JWT** (2 h), perfis **CIDADAO**, **ADMIN** e **DEVICE** (sensores, por `X-Device-Key`), validação de entrada (Bean Validation + CHECK no banco), consultas parametrizadas (contra SQL injection), bloqueio de `<`/`>` em textos livres e cabeçalhos CSP/X-Frame-Options (contra XSS), bloqueio após 5 senhas erradas (HTTP 429), segredos por variável de ambiente e erros sem stack trace. Detalhes na Parte 5 do PDF.

## 6. Simulação de IoT

Com a API rodando, em outro terminal:

```bash
python iot/simulador_sensores.py                          # 10 ciclos, cenário normal
python iot/simulador_sensores.py --cenario tempestade     # chuva passa de 30 mm/h -> alerta de Clima
python iot/simulador_sensores.py --cenario lotacao        # ocupação passa do limite -> alerta de Ocupação
python iot/simulador_sensores.py --api http://localhost:8081/api --ciclos 0   # contínuo (Ctrl+C para parar)
```

São 5 sensores simulados (3 de ocupação, 2 de chuva) em [`iot/sensores.json`](iot/sensores.json). As leituras vão para `POST /api/sensores/leituras`. A API grava, atualiza a ocupação do local e **gera um alerta quando passa do limite** (ou encerra quando normaliza). O app mostra os alertas na aba **Alertas**.

## Regerar o PDF dos entregáveis

Preencha integrantes e link do pitch em [`docs/entregaveis.html`](docs/entregaveis.html) e rode:

```bash
python docs/gerar_pdf.py          # usa Edge ou Chrome em modo headless
```

## Declaração de uso de IA generativa

| Ferramenta | Para quê |
|---|---|
| Google AI Studio | Estrutura inicial do protótipo do app Android e do protótipo web de referência (`public/assets`) |
| Claude (Anthropic) | Apoio na escrita do backend (API, segurança, testes), do simulador IoT, dos scripts SQL e da documentação; revisão de texto |

Todo o código foi revisado, executado e testado pela equipe (veja as evidências em `docs/evidencias`). A equipe entende e consegue explicar cada parte.
