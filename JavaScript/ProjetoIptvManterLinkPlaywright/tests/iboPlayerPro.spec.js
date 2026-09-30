const { test, expect } = require('@playwright/test');
const nock = require('nock');

// ─── Mock HTML ────────────────────────────────────────────────────────────────
// Seletores usados em iboPlayerPro.js:
//
//  MAC     : #mac_address
//  Password: #password
//  Login   : xpath=/html/body/div[1]/div[3]/div/form/button
//  Edit    : xpath=/html/body/div[1]/div[3]/div/div[3]/div[2]/section/div/div[2]/table/tbody/tr/td[3]/button[1]
//  Host    : [name="host"]
//  Salvar  : xpath=/html/body/div[3]/div/div/div[2]/form/button
//  Logout  : xpath=/html/body/div[1]/div[3]/div/div[3]/div[1]/div[3]
//
// Estrutura:
//  body
//  ├── div[1]                     ← app container
//  │   ├── div[1]                 ← placeholder
//  │   ├── div[2]                 ← placeholder
//  │   └── div[3]                 ← content area
//  │       └── div                ← inner wrapper
//  │           ├── form           ← login (div[1]/div[3]/div/form)
//  │           ├── div[1]         ← nav (div[3]/div[1]/div[3] = logout)
//  │           │   ├── div[1]
//  │           │   ├── div[2]
//  │           │   └── div[3]     ← LOGOUT
//  │           ├── div[2]
//  │           └── div[3]         ← dashboard
//  │               ├── div[1]
//  │               └── div[2]
//  │                   └── section > div > div[2] > table...
//  ├── div[2]
//  └── div[3]                     ← modal (body/div[3])
//      └── div > div
//          ├── div[1]
//          └── div[2] > form      ← save form

const IBO_PRO_HTML = `<!DOCTYPE html>
<html>
<head><title>IboPlayerPro Mock</title></head>
<body>
  <div id="main-app">
    <div></div>
    <div></div>
    <div id="content-area">
      <div id="inner-wrapper">
        <form id="login-form">
          <input id="mac_address" type="text" />
          <input id="password"    type="text" />
          <button type="button" id="login-btn">Login</button>
        </form>

        <div id="nav-bar">
          <div></div>
          <div></div>
          <div id="logout-div" style="cursor:pointer">Logout</div>
        </div>

        <div></div>

        <div id="dashboard">
          <div></div>
          <div id="table-area">
            <section>
              <div>
                <div></div>
                <div>
                  <table>
                    <tbody>
                      <tr>
                        <td>Playlist</td>
                        <td>Status</td>
                        <td>
                          <button type="button" id="edit-btn">Editar</button>
                          <button type="button">Deletar</button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </section>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div></div>

  <div id="modal">
    <div>
      <div>
        <div></div>
        <div>
          <form id="modal-form">
            <input name="host" type="text" value="" />
            <button type="button" id="modal-save-btn">Salvar</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</body>
</html>`;

// ─── Setup e teardown ─────────────────────────────────────────────────────────

let lib;
let iboPlayerPro;

test.beforeEach(async () => {
  nock.cleanAll();
  delete require.cache[require.resolve('../src/biblioteca')];
  delete require.cache[require.resolve('../src/sites/iboPlayerPro')];
  lib = require('../src/biblioteca');
  iboPlayerPro = require('../src/sites/iboPlayerPro');

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

test('IboPlayerPro - processa cliente com sucesso', async ({ page }) => {
  await page.route('https://iboplayer.pro/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: IBO_PRO_HTML })
  );

  const sucesso = await iboPlayerPro.processarCliente(
    '00:11:22:33:44:55', 'DEVICE_KEY', 'UNIPLAY', page
  );

  expect(sucesso).toBe(true);
});

test('IboPlayerPro - preenche MAC e password corretamente', async ({ page }) => {
  await page.route('https://iboplayer.pro/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: IBO_PRO_HTML })
  );

  // O finally() do código sempre dá reload() (mesmo em caso de sucesso), o que
  // limpa os campos — captura os valores no momento em que são preenchidos.
  let macCapturado = '';
  let pwdCapturado = '';
  await page.exposeFunction('__capturarLogin', (mac, pwd) => {
    macCapturado = mac;
    pwdCapturado = pwd;
  });
  await page.addInitScript(() => {
    document.addEventListener('input', () => {
      const mac = document.querySelector('#mac_address');
      const pwd = document.querySelector('#password');
      if (mac && pwd) window.__capturarLogin(mac.value, pwd.value);
    });
  });

  await iboPlayerPro.processarCliente('BB:CC:DD:EE:FF:00', 'MY_KEY_456', 'PLAYON', page);

  expect(macCapturado).toBe('BB:CC:DD:EE:FF:00');
  expect(pwdCapturado).toBe('MY_KEY_456');
});

test('IboPlayerPro - preenche o campo host com link do servidor', async ({ page }) => {
  await page.route('https://iboplayer.pro/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: IBO_PRO_HTML })
  );

  let hostCapturado = '';
  await page.exposeFunction('__capturarHost', (valor) => { hostCapturado = valor; });
  await page.addInitScript(() => {
    document.addEventListener('input', () => {
      const el = document.querySelector('[name="host"]');
      if (el) window.__capturarHost(el.value);
    });
  });

  await iboPlayerPro.processarCliente('AA:BB:CC:DD:EE:FF', 'KEY', 'TVS', page);

  expect(hostCapturado).toBe('http://tvs.test.com');
});

test('IboPlayerPro - retorna false quando elemento não existe', async ({ page }) => {
  await page.route('https://iboplayer.pro/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: '<html><body></body></html>' })
  );

  const sucesso = await iboPlayerPro.processarCliente(
    '00:11:22:33:44:55', 'KEY', 'TVS', page, 2
  );

  expect(sucesso).toBe(false);
});
