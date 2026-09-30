const { test, expect } = require('@playwright/test');
const nock = require('nock');

// ─── Mock HTML ────────────────────────────────────────────────────────────────
// Seletores usados em vuPlayer.js (site foi redesenhado em 2026, era baseado em
// xpaths/ids fixos, agora usa componentes MUI com atributos `name` estáveis):
//
//  MAC        : input[name="macAddress"]
//  DevKey     : input[name="deviceKey"]
//  Login btn  : button[type="submit"]
//  Lista      : goto https://vuplayer.pro/playlists?type=list
//  Edit btn   : role=button "Editar"
//  URL field  : input[name="playlistHost"]
//  Save btn   : role=button "Enviar"
//  Logout     : role=button "Sair"

const VU_LOGIN_HTML = `<!DOCTYPE html>
<html>
<head><title>VuPlayer Mock - Login</title></head>
<body>
  <form id="login-form">
    <input name="macAddress" type="text" />
    <input name="deviceKey" type="text" />
    <button type="submit">Entrar</button>
  </form>
</body>
</html>`;

const VU_LIST_HTML = `<!DOCTYPE html>
<html>
<head><title>VuPlayer Mock - Lista</title></head>
<body>
  <table>
    <tbody>
      <tr>
        <td>Playlist</td>
        <td>Status</td>
        <td>
          <button type="button">Editar</button>
          <button type="button">Deletar</button>
        </td>
      </tr>
    </tbody>
  </table>

  <div id="edit-dialog">
    <input name="playlistHost" type="text" value="" />
    <button type="button">Cancelar</button>
    <button type="submit">Enviar</button>
  </div>

  <button type="button">Sair</button>
</body>
</html>`;

// ─── Setup e teardown ─────────────────────────────────────────────────────────

let lib;
let vuPlayer;

test.beforeEach(async () => {
  nock.cleanAll();
  delete require.cache[require.resolve('../src/biblioteca')];
  delete require.cache[require.resolve('../src/sites/vuPlayer')];
  lib = require('../src/biblioteca');
  vuPlayer = require('../src/sites/vuPlayer');

  lib.atualizarLinksDaApi([
    { nomeServidor: 'TVS', urlAtual: 'http://tvs.test.com' },
    { nomeServidor: 'UNIPLAY', urlAtual: 'http://uniplay.test.com' },
    { nomeServidor: 'PLAYON', urlAtual: 'http://playon.test.com' },
    { nomeServidor: 'FAST', urlAtual: 'http://fast.test.com' },
  ]);
});

test.afterEach(() => {
  nock.cleanAll();
});

// ─── Testes ───────────────────────────────────────────────────────────────────

function mockRoutes(page) {
  return Promise.all([
    page.route('https://vuplayer.pro/login', (route) =>
      route.fulfill({ status: 200, contentType: 'text/html', body: VU_LOGIN_HTML })
    ),
    page.route('https://vuplayer.pro/playlists?type=list', (route) =>
      route.fulfill({ status: 200, contentType: 'text/html', body: VU_LIST_HTML })
    ),
  ]);
}

test('VuPlayer - processa cliente com sucesso', async ({ page }) => {
  await mockRoutes(page);

  const sucesso = await vuPlayer.processarCliente(
    '00:11:22:33:44:55', 'DEVICE_KEY', 'TVS', page
  );

  expect(sucesso).toBe(true);
});

test('VuPlayer - preenche MAC e device key corretamente', async ({ page }) => {
  await mockRoutes(page);

  // O código navega para a lista de playlists logo após o login, então o
  // formulário de login some do DOM — captura os valores no momento do preenchimento.
  let macCapturado = '';
  let keyCapturado = '';
  await page.exposeFunction('__capturarLogin', (mac, key) => {
    macCapturado = mac;
    keyCapturado = key;
  });
  await page.addInitScript(() => {
    document.addEventListener('input', () => {
      const mac = document.querySelector('input[name="macAddress"]');
      const key = document.querySelector('input[name="deviceKey"]');
      if (mac && key) window.__capturarLogin(mac.value, key.value);
    });
  });

  await vuPlayer.processarCliente('CC:DD:EE:FF:00:11', 'KEY-VU-TEST', 'FAST', page);

  expect(macCapturado).toBe('CC:DD:EE:FF:00:11');
  expect(keyCapturado).toBe('KEY-VU-TEST');
});

test('VuPlayer - preenche playlist-url com link do servidor', async ({ page }) => {
  await mockRoutes(page);

  let urlCapturada = '';
  await page.exposeFunction('__capturarUrl', (valor) => { urlCapturada = valor; });
  await page.addInitScript(() => {
    document.addEventListener('input', () => {
      const el = document.querySelector('input[name="playlistHost"]');
      if (el) window.__capturarUrl(el.value);
    });
  });

  await vuPlayer.processarCliente('AA:BB:CC:DD:EE:FF', 'KEY', 'UNIPLAY', page);

  expect(urlCapturada).toBe('http://uniplay.test.com');
});

test('VuPlayer - clica em Sair ao finalizar', async ({ page }) => {
  await mockRoutes(page);

  let cliqueSair = false;
  await page.exposeFunction('__marcarSair', () => { cliqueSair = true; });
  await page.addInitScript(() => {
    document.addEventListener('click', (e) => {
      const btn = e.target.closest('button');
      if (btn && btn.textContent.trim() === 'Sair') window.__marcarSair();
    });
  });

  await vuPlayer.processarCliente('00:11:22:33:44:55', 'KEY', 'TVS', page);

  expect(cliqueSair).toBe(true);
});

test('VuPlayer - retorna false quando elemento não existe', async ({ page }) => {
  await page.route('https://vuplayer.pro/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: '<html><body></body></html>' })
  );

  const sucesso = await vuPlayer.processarCliente(
    '00:11:22:33:44:55', 'KEY', 'TVS', page, 2
  );

  expect(sucesso).toBe(false);
});
