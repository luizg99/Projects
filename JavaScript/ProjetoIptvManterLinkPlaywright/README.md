# Projeto IPTV — Manter Link (Playwright)

Automação para atualizar o link/host da playlist de clientes IPTV em 4 painéis
(`iboplayer.com`, `iboplayer.pro`, `quickplayer.app`, `vuplayer.pro`), lendo os
dados de clientes e de servidores de planilhas do Google Sheets.

## Pré-requisitos

- [Node.js](https://nodejs.org/) instalado (recomendado LTS mais recente).
- Acesso à internet (o projeto lê planilhas reais do Google Sheets e acessa os
  sites dos painéis).

## Instalação

```bash
npm install
npm run install-browsers   # baixa o Chromium usado pelo Playwright (só precisa uma vez)
```

### Suíte de testes end-to-end reais (acessa sites e planilhas de produção)

```bash
npm run test:e2e
```

## Rodando a aplicação

```bash
npm start
```

O que acontece:

1. É exibido um menu interativo no terminal (setas + espaço para marcar, Enter
   para confirmar):
   - Quais **servidores** atualizar (`TVS`, `UNIPLAY`, `PLAYON`, `FAST`).
   - Quais **aplicativos/painéis** processar (IboPlayer, IboPlayerPro,
     QuickPlayer, VuPlayer).
2. O script lê a planilha real de clientes (`index.js`, constante
   `sheetUrlClientes`) e processa cada cliente elegível (`Ativo=S`,
   `Atualizar=S`) no painel indicado na coluna `Site ativação`.
3. Os navegadores abrem **visíveis** (`headless: false`) e processam clientes
   em paralelo — o número de navegadores simultâneos é controlado pela
   constante `CONCURRENCY` em `index.js`.
4. Ao final, os clientes que falharem são reprocessados (até 2 tentativas
   extras, exceto `iboplayer.com`, que não permite retry em lote).

> ⚠️ **Atenção:** isso altera de verdade a playlist salva na conta de cada
> cliente real e ativo da planilha. Não é um modo de demonstração.

## Testes

O projeto usa o [Playwright Test](https://playwright.dev/docs/test-intro)
(`@playwright/test`) como test runner, tanto para testes unitários (com
`nock` mockando as chamadas HTTP) quanto para os testes de automação de
navegador.

### Suíte de testes com mocks (não acessa a internet nem sites reais)

```bash
npm test
```

Ou, para rodar um arquivo específico:

```bash
npx playwright test tests/biblioteca.spec.js
```

Outras opções úteis:

```bash
npx playwright test --ui        # modo interativo
npx playwright test --headed    # com navegador visível
```

Essa suíte fica em `tests/` (config: `playwright.config.js`) e usa:
- `nock` para simular respostas HTTP (Google Sheets, API do 2Captcha).
- `page.route()` do Playwright para simular o HTML dos sites dos painéis.

Ela valida a **lógica de automação** (seletores, preenchimento de campos,
tratamento de erro/retry), sem depender de credenciais reais nem alterar
nada de verdade.


Essa suíte (`tests-e2e/producao.spec.js`, config: `e2e.config.js`) roda
**sem mocks**: lê a planilha real de clientes, pega o primeiro cliente
elegível de cada um dos 4 painéis e executa a atualização de playlist de
verdade.

> ⚠️ **Atenção:** isso **altera de fato** a playlist salva na conta real de
> até 4 clientes (um por painel). Use com cuidado — não é reversível
> automaticamente.

## Estrutura do projeto

```
index.js                 → ponto de entrada da aplicação (CLI interativa)
src/
  biblioteca.js           → leitura das planilhas, links atualizados, resolução de CAPTCHA
  interface.js            → menu interativo (inquirer)
  sites/
    iboPlayer.js           → automação para iboplayer.com
    iboPlayerPro.js         → automação para iboplayer.pro
    quickPlayer.js          → automação para quickplayer.app
    vuPlayer.js             → automação para vuplayer.pro
tests/                    → testes unitários/mockados (nock + page.route)
tests-e2e/                → testes reais, sem mocks, contra produção
playwright.config.js      → config da suíte de testes com mocks
e2e.config.js             → config da suíte de testes reais (e2e)
```

## Servidores e painéis suportados

- **Servidores** (fonte do link/host da playlist): `TVS`, `UNIPLAY`,
  `PLAYON`, `FAST`.
- **Painéis/sites de ativação**: `iboplayer.com`, `iboplayer.pro`,
  `quickplayer.app`, `vuplayer.pro`.
