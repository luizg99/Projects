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

O primeiro menu escolhe entre `Tradicional` e `Por Cliente`. No modo tradicional,
o menu permite escolher os servidores retornados pela API e os aplicativos. No
modo por cliente, a lista aparece em ordem alfabética; é possível pesquisar por
nome ou ID e digitar os números de um ou mais clientes. São processados todos os
aplicativos e servidores com playlists elegíveis dos clientes escolhidos.
São processadas playlists com `ativo=S` e `atualizar=S`. O sistema consulta
todas as páginas da API e usa `urlAtual` do servidor de cada playlist. Os clientes
são processados em rodadas: até um cliente por aplicativo em paralelo, com a
pausa configurada antes da próxima rodada. Ao identificar HTTP 429 ou Cloudflare
1015, o painel afetado é pausado pelo restante da execução e não recebe novas tentativas.
Cada passagem começa com `Nova Sessão iniciada`. Após cada MAC, o console mostra
o total original por aplicativo e quantos ainda aguardam conclusão, inclusive
os que terão nova tentativa.

## PlayerApp

For playlists whose activation site is `playbr.app.br`, the automation calls
the PlayerApp integration API and does not open a browser. Configure
`PLAYBR_API_KEY` in the local `.env`; the endpoint defaults to
`https://api.playbr.app.br/api/integracoes/v1/playlists/atualizar-link` and can
be overridden with `PLAYERAPP_INTEGRATION_API_URL`. The request keeps sending
the current server link as `urlNova` and also sends the server's `m3u` base,
alongside the MAC and Device Key, so the integration can apply the matching
link for the stored playlist type. A successful response must include both a
recognized `resultado` and `tipoPlaylist` (`m3u` or `xtream`) so the automation
can confirm which format the PlayBR API updated.

## IboPlayer, IboPlayer Pro and VU Player Pro

The IboPlayer and VU Player Pro automations detect the edit form type. Xtream
playlists update only the host from `urlAtual`, preserving the username and
password. M3U playlists use the `m3u` field returned by the Lembretinho API (a
base ending in `.php?`) and keep the existing query string. IboPlayer Pro also
detects its Xtream and M3U edit forms and applies the matching update. If the
API omits the M3U base or the playlist URL does not contain `.php?`, the
automation skips the save.

## QuickPlayer

QuickPlayer stores only one playlist URL in its edit form. Its Xtream creation
flow builds `get.php` URLs with `type=m3u_plus&output=ts`; the M3U example uses
`type=m3u_plus&output=hls`. The automation uses `output` to distinguish them:
Xtream updates the host, while M3U uses the API `m3u` base and preserves the
existing query. A URL without a recognizable signature is skipped. Because
QuickPlayer does not retain a separate playlist-type field, a direct M3U URL
identical in structure to its generated Xtream URL cannot be distinguished by
the panel.

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
