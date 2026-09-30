const { test, expect } = require('@playwright/test');
const playlistService = require('../src/services/playlistService');
const iboPlayer = require('../src/sites/iboPlayer');
const iboPlayerPro = require('../src/sites/iboPlayerPro');
const quickPlayer = require('../src/sites/quickPlayer');
const vuPlayer = require('../src/sites/vuPlayer');

// Testa uma playlist real por aplicativo em cada servidor. Roda separadamente:
//   npx playwright test --config=e2e.config.js

const SITES = {
  'iboplayer.com': { modulo: iboPlayer, nome: 'IboPlayer' },
  'iboplayer.pro': { modulo: iboPlayerPro, nome: 'IboPlayerPro' },
  'quickplayer.app': { modulo: quickPlayer, nome: 'QuickPlayer' },
  'vuplayer.pro': { modulo: vuPlayer, nome: 'VuPlayer' },
};

test('atualiza uma playlist por aplicativo em cada servidor', async ({ page }) => {
  const playlists = await playlistService.listar();
  const amostras = playlistService.selecionarAmostras(playlists);
  expect(amostras.length).toBeGreaterThan(0);
  const falhas = [];

  for (const playlist of amostras) {
    const automacao = SITES[playlist.siteAtivacao];
    if (!automacao) continue;
    try {
      const sucesso = await automacao.modulo.processarCliente(
        playlist.macAddress,
        playlist.deviceKey,
        playlist.servidor,
        page
      );
      if (!sucesso) falhas.push(`${automacao.nome} / ${playlist.servidor}`);
    } catch {
      falhas.push(`${automacao.nome} / ${playlist.servidor}`);
    }
  }

  expect(falhas).toEqual([]);
});
