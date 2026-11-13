# Contrato sugerido da API - CitizenTech

Este contrato é a referência de integração do protótipo. O grupo deve alinhar os nomes/formatos com o backend ASP.NET ou Java antes da integração final. A URL-base esperada pelo frontend termina em `/api`.

## Endpoints previstos

| Método | Rota | Uso no produto | Acesso sugerido |
|---|---|---|---|
| GET | `/locais` | Lista os locais cadastrados | Cidadão |
| GET | `/locais/{id}` | Consulta detalhes, recursos e ocupação de um local | Cidadão |
| GET | `/ocorrencias?usuarioId={id}` | Lista os relatos do cidadão | Cidadão (próprios) |
| POST | `/ocorrencias` | Registra uma barreira | Cidadão autenticado |
| PUT | `/ocorrencias/{id}/status` | Atualiza o andamento de uma ocorrência | Administrador |
| DELETE | `/ocorrencias/{id}` | Exclui/cancela uma ocorrência conforme regra de negócio | Administrador ou autor, com validação |
| GET | `/alertas` | Lista alertas ativos e histórico | Cidadão |
| GET | `/preferencias/{usuarioId}` | Consulta preferências de acessibilidade | Cidadão (próprias) |
| PUT | `/preferencias/{usuarioId}` | Salva preferências | Cidadão (próprias) |
| POST | `/sensores/leituras` | Registra leitura(s) simulada(s) dos sensores IoT | Serviço/dispositivo autorizado |

O frontend desta entrega chama GET `/locais`, GET `/locais/{id}`, GET `/ocorrencias`, POST `/ocorrencias`, GET `/alertas`, GET `/preferencias/{usuarioId}`, PUT `/preferencias/{usuarioId}` e POST `/sensores/leituras`. As rotas PUT de status e DELETE completam o CRUD da API e podem ser usadas pelo painel administrativo.

## Exemplos de payloads

### POST `/ocorrencias`

```json
{
  "usuarioId": "cidadao-demo-01",
  "localId": "biblioteca",
  "categoria": "Calçada irregular",
  "descricao": "Piso desnivelado na lateral da entrada, próximo à faixa de pedestres."
}
```

Resposta sugerida: HTTP `201 Created`, com a ocorrência criada e status inicial `Recebida`.

### PUT `/preferencias/{usuarioId}`

```json
{
  "evitarEscadas": true,
  "priorizarRampas": true,
  "alertasVisuais": true,
  "altoContraste": false
}
```

### POST `/sensores/leituras`

O frontend envia um array para registrar os dois sensores no mesmo clique de demonstração:

```json
[
  {
    "tipo": "ocupacao",
    "localId": "biblioteca",
    "valor": 42,
    "unidade": "pessoas"
  },
  {
    "tipo": "chuva",
    "localId": "centro-cultural",
    "localNome": "Praça das Artes",
    "valor": 35,
    "unidade": "mm/h"
  }
]
```

A API deve aplicar limites definidos pela equipe e, quando ultrapassados, registrar o alerta correspondente. Os valores acima são exclusivamente demonstrativos, não são limites oficiais.

## Formato mínimo de resposta

### Local

```json
{
  "id": "biblioteca",
  "nome": "Biblioteca Central",
  "categoria": "Serviço público",
  "endereco": "Rua das Palmeiras, 120 · Centro",
  "statusAcessibilidade": "Parcial",
  "resumo": "Entrada principal nivelada.",
  "recursos": ["Entrada principal com rampa", "Banheiro acessível"],
  "obstaculos": ["Calçada lateral irregular"],
  "ocupacao": 42,
  "nivelOcupacao": "Alta",
  "ultimaLeitura": "14:32",
  "atualizadoEm": "Hoje, 14:32",
  "confianca": "Cadastro + relato comunitário"
}
```

### Ocorrência

```json
{
  "id": "CT-1024",
  "localId": "biblioteca",
  "localNome": "Biblioteca Central",
  "categoria": "Calçada irregular",
  "descricao": "Piso desnivelado na lateral da entrada.",
  "status": "Em análise",
  "criadoEm": "Hoje, 10:42",
  "progresso": 45
}
```

### Alerta

```json
{
  "id": "AL-205",
  "tipo": "Clima",
  "titulo": "Chuva intensa no entorno da Praça Central",
  "local": "Praça Central",
  "valor": 35,
  "unidade": "mm/h",
  "atualizadoEm": "Hoje, 14:35",
  "descricao": "A leitura simulada ultrapassou o limite de demonstração."
}
```

## Regras que o backend precisa respeitar

- Retornar status HTTP coerentes (`200`, `201`, `204`, `400`, `401`, `403`, `404`, `409`, `500`) e mensagens de validação compreensíveis.
- Validar existência de `localId`, categoria e descrição não vazia; foto é opcional.
- Marcar leituras antigas como desatualizadas e retornar a data/hora da última leitura.
- Não tratar automaticamente um relato comunitário como fato confirmado.
- A contagem de pessoas deve ser agregada; não identificar pessoas nem armazenar rostos/imagens.
- Identificar claramente leituras e alertas simulados; não representar a simulação como alerta oficial da Defesa Civil.
- Proteger as rotas com autenticação e autorização conforme perfil. Senhas devem ser armazenadas com hash no backend. O token pode ser enviado pelo frontend no cabeçalho `Authorization: Bearer ...`.
- Habilitar CORS somente para as origens necessárias durante o desenvolvimento.

## Configurar a URL no frontend

No `index.html`, antes de carregar `api.js`, descomente/adapte:

```html
<script>
  window.CITIZENTECH_API_BASE = "http://localhost:8080/api";
</script>
<script src="api.js"></script>
```

Com a URL vazia (padrão), o app usa dados locais de demonstração no navegador. Com a URL preenchida, as chamadas vão para a API real; falhas de conexão são exibidas, sem trocar silenciosamente para dados fictícios.
