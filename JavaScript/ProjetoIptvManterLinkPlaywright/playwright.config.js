const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './tests',
  timeout: 90000,       // 90s por teste (automações têm waitForTimeout)
  workers: 1,           // executa um teste por vez (estado global compartilhado)
  use: {
    headless: true,
    actionTimeout: 15000,
  },
  reporter: [['list']],
});
