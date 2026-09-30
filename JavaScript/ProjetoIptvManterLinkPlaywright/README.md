# Automação Lembretinho Playwright

Automação de renovação de playlists nos painéis IboPlayer, IboPlayerPro,
QuickPlayer e VuPlayer. Os dados vêm da API de leitura do Lembretinho.

## Configuração

Requer Node.js 20 ou superior.

```bash
npm install
npm run install-browsers
```

Copie `.env.example` para `.env` e configure `LEMBRETINHO_API_KEY` e
`CAPTCHA_API_KEY`. `AUTOMACAO_INTERVALO_MS` define a pausa entre rodadas
(30 segundos por padrão). A chave da API é gerada no Lembretinho em Playlists →
Gerenciar Playlists → Integração da automação. O arquivo `.env` é local e não
deve ser compartilhado ou commitado.

## Execução

```bash
npm start
```

O menu permite escolher os servidores retornados pela API e os aplicativos.
São processadas playlists com `ativo=S` e `atualizar=S`. O sistema consulta
todas as páginas da API e usa `urlAtual` do servidor selecionado. Os clientes
são processados em rodadas: até um cliente por aplicativo em paralelo, com a
pausa configurada antes da próxima rodada. Ao identificar HTTP 429 ou Cloudflare
1015, o painel afetado é pausado pelo restante da execução e não recebe novas tentativas.

## Testes

```bash
npm test
```

O teste E2E consulta dados reais e grava alterações nas contas dos painéis:

```bash
npm run test:e2e
```

O E2E escolhe uma playlist por combinação de servidor e aplicativo e processa
as combinações em sequência. Execute-o somente quando estiver preparado para
alterar essas playlists.

## Organização

- `src/models`: dados da playlist, normalização, elegibilidade e seleção.
- `src/views`: entrada e saída do terminal.
- `src/controllers`: recebe as escolhas da view e coordena a aplicação.
- `src/services`: casos de uso e execução das automações.
- `src/repositories`: acesso HTTP de leitura à API do Lembretinho.
- `src/sites`: automação específica de cada painel.
- `src/app.js`: inicialização e tratamento de erros da aplicação.
- `index.js`: ponto de entrada mínimo.
