const { test, expect } = require('@playwright/test');
const automacaoService = require('../src/services/automacaoService');
const painelIbo = require('../src/sites/iboPlayer');
const painelIboPro = require('../src/sites/iboPlayerPro');
const painelQuick = require('../src/sites/quickPlayer');
const painelVu = require('../src/sites/vuPlayer');
const painelPlayerApp = require('../src/sites/playerApp');

test('painéis fazem no máximo uma tentativa interna, exceto IboPlayer com duas', async () => {
  const logOriginal = console.log;
  console.log = () => {};
  try {
    for (const [painel, esperado] of [
      [painelIboPro, 1], [painelQuick, 1], [painelVu, 1], [painelIbo, 2],
    ]) {
      let acessos = 0;
      const pagina = {
        goto: async () => { acessos++; throw new Error('falha simulada'); },
        locator: () => ({ click: async () => { throw new Error('sem sessão'); } }),
        waitForTimeout: async () => {},
      };
      const sucesso = await painel.processarCliente('00:11:22:33:44:55', 'KEY', 'TVS', pagina);
      expect(sucesso).toBe(false);
      expect(acessos).toBe(esperado);
    }
  } finally {
    console.log = logOriginal;
  }
});

test('fila geral limita a duas chamadas por MAC com falha', async () => {
  const processarOriginal = painelPlayerApp.processarCliente;
  const logOriginal = console.log;
  let chamadas = 0;
  painelPlayerApp.processarCliente = async () => { chamadas++; return false; };
  console.log = () => {};
  try {
    const resultado = await automacaoService.executar([{
      siteAtivacao: 'playbr.app.br', servidor: 'TVS', macAddress: 'MAC', deviceKey: 'KEY',
    }], { servidores: ['TVS'], PLAYER_APP_API: true });
    expect(chamadas).toBe(2);
    expect(resultado.falhas).toHaveLength(1);
  } finally {
    painelPlayerApp.processarCliente = processarOriginal;
    console.log = logOriginal;
  }
});
