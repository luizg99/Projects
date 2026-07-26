const { defineConfig } = require('@playwright/test');

// Config separada para os testes reais (tests-e2e/), que tocam sites e
// planilhas de produção de verdade. Não roda junto com `npx playwright test`
// (que usa playwright.config.js, testDir: './tests').
module.exports = defineConfig({
  testDir: './tests-e2e',
  timeout: 180000,      // captcha via 2Captcha pode demorar
  workers: 1,
  use: {
    headless: true,
    actionTimeout: 20000,
  },
  reporter: [['list']],
});
