const { test, expect } = require('@playwright/test');
const nock = require('nock');

// ─── Mock HTML ────────────────────────────────────────────────────────────────
// Seletores usados em vuPlayer.js:
//
//  MAC        : #macAddress
//  DevKey     : #key
//  Login btn  : xpath=/html/body/div[2]/section[2]/div/div/div[2]/form/div/div[4]/button
//  Edit btn   : xpath=/html/body/div[2]/section[2]/div/div[2]/div[1]/table/tbody/tr/td[3]/button[1]
//  URL field  : #playlist-url
//  Save btn   : xpath=/html/body/div[2]/section[2]/div/div[2]/div[2]/div/div/div[3]/button[2]
//  Logout     : page.goto('https://vuplayer.pro/logout')  ← URL direta
//
// Estrutura body:
//  div[1] ← placeholder
//  div[2] ← app principal
//    section[1] ← header/nav
//    section[2] ← conteúdo
//      div > div
//        div[1] > form (login)
//        div[2] > (tabela + edição)

const VU_LOGIN_HTML = `<!DOCTYPE html>
<html>
<head><title>VuPlayer Mock - Login</title></head>
<body>
  <div></div>

  <div id="main-app">
    <section id="header"></section>

    <section id="content">
      <div>
        <div>
          <div>
            <div></div>
            <div>
              <form id="login-form">
                <div>
                  <div>
                    <div><input id="macAddress" type="text" /></div>
                    <div><input id="key"        type="text" /></div>
                    <div></div>
                    <div>
                      <button type="button" id="login-btn">Entrar</button>
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>

          <div id="dashboard-area">
            <div id="table-container">
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

            <div id="edit-panel">
              <div>
                <div>
                  <div></div>
                  <div></div>
                  <div>
                    <button type="button">Cancelar</button>
                    <button type="button" id="save-btn">Salvar</button>
                  </div>
                </div>
              </div>
              <input id="playlist-url" type="text" value="" />
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</body>
</html>`;

const VU_LOGOUT_HTML = `<!DOCTYPE html>
<html><body><p>Você saiu com sucesso.</p></body></html>`;

// ─── Setup e teardown ─────────────────────────────────────────────────────────

let lib;
let vuPlayer;

test.beforeEach(async () => {
  nock.cleanAll();
  delete require.cache[require.resolve('../src/biblioteca')];
  delete require.cache[require.resolve('../src/sites/vuPlayer')];
  lib = require('../src/biblioteca');
  vuPlayer = require('../src/sites/vuPlayer');

  nock('https://docs.google.com')
    .get('/spreadsheets/d/1lMIq91MwJcxuNDZsJaTLdYmj_M2bmLO5_R9Y5EWDEb8/export')
    .query({ format: 'csv' })
    .reply(200, 'Nome painel,Link\nlink_tvs,http://tvs.test.com\nlink_uniplay,http://uniplay.test.com\nlink_bit,http://bit.test.com\nlink_fast,http://fast.test.com');

  await lib.atualizarLinks();
});

test.afterEach(() => {
  nock.cleanAll();
});

// ─── Testes ───────────────────────────────────────────────────────────────────

test('VuPlayer - processa cliente com sucesso', async ({ page }) => {
  await page.route('https://vuplayer.pro/login', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: VU_LOGIN_HTML })
  );
  await page.route('https://vuplayer.pro/logout', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: VU_LOGOUT_HTML })
  );

  const sucesso = await vuPlayer.processarCliente(
    '00:11:22:33:44:55', 'DEVICE_KEY', 'TVS', page
  );

  expect(sucesso).toBe(true);
});

test('VuPlayer - preenche MAC e device key corretamente', async ({ page }) => {
  await page.route('https://vuplayer.pro/login', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: VU_LOGIN_HTML })
  );
  await page.route('https://vuplayer.pro/logout', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: VU_LOGOUT_HTML })
  );

  await vuPlayer.processarCliente('CC:DD:EE:FF:00:11', 'KEY-VU-TEST', 'FAST', page);

  const mac = await page.locator('#macAddress').inputValue();
  const key = await page.locator('#key').inputValue();
  expect(mac).toBe('CC:DD:EE:FF:00:11');
  expect(key).toBe('KEY-VU-TEST');
});

test('VuPlayer - preenche playlist-url com link do servidor', async ({ page }) => {
  await page.route('https://vuplayer.pro/login', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: VU_LOGIN_HTML })
  );
  await page.route('https://vuplayer.pro/logout', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: VU_LOGOUT_HTML })
  );

  await vuPlayer.processarCliente('AA:BB:CC:DD:EE:FF', 'KEY', 'UNIPLAY', page);

  const urlValue = await page.locator('#playlist-url').inputValue();
  expect(urlValue).toBe('http://uniplay.test.com');
});

test('VuPlayer - navega para URL de logout ao finalizar', async ({ page }) => {
  await page.route('https://vuplayer.pro/login', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: VU_LOGIN_HTML })
  );
  await page.route('https://vuplayer.pro/logout', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: VU_LOGOUT_HTML })
  );

  await vuPlayer.processarCliente('00:11:22:33:44:55', 'KEY', 'TVS', page);

  // Verifica que a última URL acessada foi a de logout
  expect(page.url()).toBe('https://vuplayer.pro/logout');
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
