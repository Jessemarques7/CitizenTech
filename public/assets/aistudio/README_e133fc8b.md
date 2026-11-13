# CitizenTech - Protótipo mobile

**Mais informação. Mais autonomia.**

Protótipo mobile-first para a Global Solution FIAP 2026/2. A interface demonstra a jornada de uma pessoa cidadã consultando locais, identificando condições de acessibilidade e ocupação, registrando barreiras, acompanhando relatos e consultando alertas.

## Como executar

1. Extraia o ZIP.
2. Abra a pasta `citizentech_prototipo`.
3. Para a demonstração local, abra `index.html` em um navegador moderno.
4. Recomendado para persistência mais consistente: no terminal, dentro da pasta, execute `python -m http.server 5500` e acesse `http://localhost:5500`.

Não há dependências externas ou instalação de pacotes. A interface foi pensada para largura mobile e também pode ser demonstrada em desktop.

## Telas e interações

- **Explorar:** mapa ilustrativo, busca, filtros de acessibilidade e lista de locais.
- **Detalhe do local:** recursos cadastrados, obstáculos reportados, ocupação estimada e hora da última leitura.
- **Novo relato:** cadastro com local, categoria e descrição obrigatórios; foto opcional (o envio de arquivo ainda não está integrado).
- **Meus relatos:** acompanhamento de status: recebida, em análise, encaminhada e resolvida.
- **Alertas:** alertas associados à leitura simulada de chuva e descrição dos dois sensores.
- **Perfil:** preferências de rota, alto contraste e ajuste do tamanho do texto.

A navegação inferior leva a Explorar, Relatos, Alertas e Perfil. Os cartões de locais abrem a tela de detalhe; o botão de relato abre o cadastro.

## Modo demonstração e integração

O protótipo abre em **modo demonstração**, com registros fictícios e persistência no `localStorage`. Os locais são um recorte demonstrativo, não representam endereços reais. A biblioteca começa com 42 pessoas estimadas e classificação de alta ocupação; a leitura de chuva começa em 35 mm/h. Esses números servem para mostrar as regras de negócio e não são limites ou alertas oficiais.

Para conectar ao backend do grupo, defina `window.CITIZENTECH_API_BASE` no `index.html`, antes do `api.js`, por exemplo:

```html
<script>window.CITIZENTECH_API_BASE = "http://localhost:8080/api";</script>
<script src="api.js"></script>
```

O frontend então usa a API e não faz fallback silencioso para dados locais em caso de erro. O backend precisa liberar CORS para a origem do frontend. O contrato sugerido, métodos, rotas e exemplos estão em [`API_CONTRATO.md`](API_CONTRATO.md).

**Atenção:** a integração real só estará concluída depois que as rotas e os formatos forem alinhados ao backend da equipe e testados com a API em execução. Esta entrega não substitui autenticação; o adapter já suporta token Bearer salvo em `localStorage` na chave `citizentech_token`, mas o fluxo de login deve ser conectado ao mecanismo seguro desenvolvido pelo grupo.

## Identidade visual

- **Nome:** CitizenTech - Acessibilidade Urbana Inteligente.
- **Assinatura:** Mais informação. Mais autonomia.
- **Conceito:** orientação clara, acolhedora e confiável para a pessoa decidir como circular.
- **Azul petróleo `#12313D`:** segurança, estrutura e legibilidade.
- **Verde teal `#087F78`:** autonomia, ação e mobilidade.
- **Verde claro `#E3F3EF`:** apoio visual e áreas de informação.
- **Âmbar `#A45D08`:** atenção e risco moderado; sempre acompanhado de texto.
- **Vermelho `#A83E39`:** situação crítica; sempre acompanhado de texto.
- **Fundo `#F4F7F5`:** reduzir a sensação de interface carregada.
- **Tipografia:** system sans-serif, com pesos fortes para hierarquia, tamanhos redimensionáveis e áreas de toque amplas.
- **Marca:** símbolo de localização com ponto central e linha de percurso.

## Acessibilidade considerada no protótipo

- Textos e controles com nomes acessíveis e navegação por teclado.
- Status descritos por palavras, não apenas por cor.
- Alternativa em lista para os pontos do mapa ilustrativo.
- Alto contraste e ajuste de escala tipográfica.
- Alertas visuais e textuais; informações relevantes não dependem exclusivamente de som.
- Dados comunitários identificados como relatos e dados de sensores identificados como simulados.

## Arquivos

- `index.html`: estrutura da aplicação.
- `styles.css`: identidade visual e layout responsivo.
- `app.js`: telas, navegação e interações.
- `api.js`: adapter HTTP + modo demonstração.
- `API_CONTRATO.md`: proposta de integração com a API REST do grupo.
- `IDENTIDADE_VISUAL.md`: guia da marca, paleta e componentes.
- `ROTEIRO_PITCH.md`: roteiro de vídeo de até 3 minutos.

## Declaração de uso de IA

O código e os textos deste protótipo tiveram apoio de IA generativa para estruturação inicial, organização de componentes, redação de documentação e revisão. A equipe deve revisar, compreender, adaptar e testar todo o material antes da entrega, conforme as regras da disciplina.
