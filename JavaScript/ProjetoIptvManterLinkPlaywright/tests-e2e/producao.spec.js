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
  const elegiveis = playlistService.listarElegiveis(playlists).filter((playlist) =>
    /^(?:[0-9a-f]{2}:){5}[0-9a-f]{2}$/i.test(playlist.macAddress));
  const amostras = playlistService.selecionarAmostras(elegiveis)
    .filter((playlist) => !process.env.E2E_SITE || playlist.siteAtivacao === process.env.E2E_SITE);
  expect(amostras.length).toBeGreaterThan(0);
  const falhas = [];

  for (const playlist of amostras) {
    const automacao = SITES[playlist.siteAtivacao];
    if (!automacao) continue;
    console.log(`E2E: iniciando ${automacao.nome} / ${playlist.servidor}.`);
    const candidatos = elegiveis.filter((item) => item.servidorId === playlist.servidorId
      && item.aplicativoId === playlist.aplicativoId
      && item.siteAtivacao === playlist.siteAtivacao)
      .slice(0, ['vuplayer.pro', 'iboplayer.pro', 'iboplayer.com'].includes(playlist.siteAtivacao) ? 2 : 1);
    let sucesso = false;
    for (const [indice, candidato] of candidatos.entries()) {
      const contextoAmostra = await page.context().browser().newContext();
      const paginaAmostra = await contextoAmostra.newPage();
      try {
        sucesso = await automacao.modulo.processarCliente(
          candidato.macAddress,
          candidato.deviceKey,
          candidato.servidor,
          paginaAmostra
        );
      } catch (erro) {
        console.error(`E2E: ${automacao.nome} / ${playlist.servidor}: ${erro.message}`);
      } finally {
        await contextoAmostra.close();
      }
      if (sucesso) break;
      console.log(`E2E: amostra ${indice + 1}/${candidatos.length} falhou em ${automacao.nome} / ${playlist.servidor}.`);
    }
    if (!sucesso) falhas.push(`${automacao.nome} / ${playlist.servidor}`);
  }

  expect(falhas).toEqual([]);
});
