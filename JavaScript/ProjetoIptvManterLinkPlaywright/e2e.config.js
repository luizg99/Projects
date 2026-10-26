const { defineConfig } = require('@playwright/test');

// Config separada para os testes reais (tests-e2e/), que tocam sites e
// planilhas de produção de verdade. Não roda junto com `npx playwright test`
// (que usa playwright.config.js, testDir: './tests').
module.exports = defineConfig({
  testDir: './tests-e2e',
  timeout: 900000,      // percorre uma amostra real por aplicativo e servidor
  workers: 1,
  use: {
    headless: true,
    actionTimeout: 20000,
  },
  reporter: [['list']],
});
