const { test, expect } = require('@playwright/test');
const nock = require('nock');

let playerApp;
let lib;
let envOriginal;

test.beforeEach(() => {
  nock.cleanAll();
  envOriginal = {
    PLAYBR_API_KEY: process.env.PLAYBR_API_KEY,
    PLAYERAPP_INTEGRATION_API_URL: process.env.PLAYERAPP_INTEGRATION_API_URL,
  };
  process.env.PLAYBR_API_KEY = 'api-key-test';
  process.env.PLAYERAPP_INTEGRATION_API_URL = 'https://playbr-integration.test/api/playlists/update';
  delete require.cache[require.resolve('../src/biblioteca')];
  delete require.cache[require.resolve('../src/sites/playerApp')];
  lib = require('../src/biblioteca');
  playerApp = require('../src/sites/playerApp');
  lib.atualizarLinksDaApi([
    {
      nomeServidor: 'Servidor Teste',
      urlAtual: 'http://host-xtream.test',
      m3u: 'http://host-m3u.test/get.php?',
    },
  ]);
});

test.afterEach(() => {
  nock.cleanAll();
  if (envOriginal.PLAYBR_API_KEY === undefined) delete process.env.PLAYBR_API_KEY;
  else process.env.PLAYBR_API_KEY = envOriginal.PLAYBR_API_KEY;
  if (envOriginal.PLAYERAPP_INTEGRATION_API_URL === undefined) delete process.env.PLAYERAPP_INTEGRATION_API_URL;
  else process.env.PLAYERAPP_INTEGRATION_API_URL = envOriginal.PLAYERAPP_INTEGRATION_API_URL;
});

test('PlayerApp API recebe urlNova existente e a base m3u', async () => {
  let corpoRecebido;
  const requisicao = nock('https://playbr-integration.test')
    .matchHeader('x-api-key', 'api-key-test')
    .post('/api/playlists/update', (body) => {
      corpoRecebido = body;
      return true;
    })
    .reply(200, { resultado: 'atualizado', tipoPlaylist: 'xtream' });

  const sucesso = await playerApp.processarCliente('AA:BB:CC:DD:EE:FF', 'DEVICE-KEY', 'Servidor Teste');

  expect(sucesso).toBe(true);
  expect(corpoRecebido).toEqual({
    macAddress: 'AA:BB:CC:DD:EE:FF',
    deviceKey: 'DEVICE-KEY',
    urlNova: 'http://host-xtream.test',
    m3u: 'http://host-m3u.test/get.php?',
  });
  requisicao.done();
});

test('PlayerApp API não confirma sucesso sem devolver o tipo da playlist', async () => {
  const requisicao = nock('https://playbr-integration.test')
    .post('/api/playlists/update')
    .reply(200, { resultado: 'atualizado' });

  const sucesso = await playerApp.processarCliente('AA:BB:CC:DD:EE:FF', 'DEVICE-KEY', 'Servidor Teste');

  expect(sucesso).toBe(false);
  requisicao.done();
});
