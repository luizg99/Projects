const { test, expect } = require('@playwright/test');
const nock = require('nock');

// ─── Mock HTML ────────────────────────────────────────────────────────────────
// HTML construído para satisfazer todos os seletores usados em iboPlayer.js:
//
//  Termos  : xpath=/html/body/div[1]/div/div/div/button
//  MAC     : #max-address
//  DevKey  : #device-key
//  Captcha : xpath=//*[@id="root"]/main/div/form/div[5]/div
//  CaptTxt : xpath=//label[text()='Captcha']/following-sibling::input
//  Login   : xpath=/html/body/div/main/div/form/button
//  SVG     : svg.text-blue-500.cursor-pointer
//  Host    : #host
//  Salvar  : xpath=//*[@id="root"]/main/div/section/form/div[2]/button
//  Logout  : xpath=//*[@id="root"]/main/aside/a[6]
//  Err-out : xpath=//*[@id="root"]/main/aside/a[7]

const IBO_PLAYER_HTML = `<!DOCTYPE html>
<html>
<head><title>IboPlayer Mock</title></head>
<body>
  <div id="root">
    <div><div><div>
      <button id="terms-btn">Aceitar Termos</button>
    </div></div></div>

    <main>
      <div>
        <form id="login-form">
          <div><input id="max-address" type="text" /></div>
          <div><input id="device-key"  type="text" /></div>
          <div></div>
          <div></div>
          <div>
            <div id="captcha-area"
                 style="width:200px;height:80px;background:#ddd;display:flex;
                        align-items:center;justify-content:center;">
              CAPTCHA
            </div>
          </div>
          <div>
            <label>Captcha</label>
            <input id="captcha-input" type="text" />
          </div>
          <button type="button" id="login-btn">Entrar</button>
        </form>

        <section>
          <form id="edit-form">
            <div><input id="host" type="text" value="" /></div>
            <div><button type="button" id="save-btn">Salvar</button></div>
          </form>
        </section>

        <svg class="text-blue-500 cursor-pointer"
             xmlns="http://www.w3.org/2000/svg"
             viewBox="0 0 24 24" width="24" height="24">
          <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z"/>
        </svg>
      </div>

      <aside>
        <a href="#">Link 1</a>
        <a href="#">Link 2</a>
        <a href="#">Link 3</a>
        <a href="#">Link 4</a>
        <a href="#">Link 5</a>
        <a href="#" id="logout-link">Logout</a>
        <a href="#" id="error-logout-link">Logout (erro)</a>
      </aside>
    </main>
  </div>
</body>
</html>`;

// ─── Setup e teardown ─────────────────────────────────────────────────────────

let lib;
let iboPlayer;

test.beforeEach(() => {
  nock.cleanAll();
  delete require.cache[require.resolve('../src/biblioteca')];
  delete require.cache[require.resolve('../src/sites/iboPlayer')];
  lib = require('../src/biblioteca');
  iboPlayer = require('../src/sites/iboPlayer');
  lib.captchaApiKey = 'test-captcha-key';

  // Define os links diretamente (sem chamar atualizarLinks)
  nock('https://docs.google.com')
    .get('/spreadsheets/d/1lMIq91MwJcxuNDZsJaTLdYmj_M2bmLO5_R9Y5EWDEb8/export')
    .query({ format: 'csv' })
    .reply(200, 'Nome painel,Link\nlink_tvs,http://tvs.test.com\nlink_uniplay,http://uniplay.test.com\nlink_bit,http://bit.test.com\nlink_fast,http://fast.test.com');
});

test.afterEach(() => {
  nock.cleanAll();
});

// ─── Testes ───────────────────────────────────────────────────────────────────

test('IboPlayer - processa cliente com sucesso', async ({ page }) => {
  // Serve o mock HTML para qualquer URL do iboplayer.com
  await page.route('https://iboplayer.com/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: IBO_PLAYER_HTML })
  );

  // Mock da API 2Captcha
  nock('http://2captcha.com')
    .post('/in.php')
    .reply(200, { status: 1, request: 'captcha-id-001' });

  nock('http://2captcha.com')
    .get('/res.php')
    .query({ key: 'test-captcha-key', action: 'get', id: 'captcha-id-001', json: '1' })
    .reply(200, { status: 1, request: 'ABC123' });

  await lib.atualizarLinks();

  const sucesso = await iboPlayer.processarCliente(
    'd6:f5:72:a3:0a:55', 'DEVICE_KEY', 'TVS', page
  );

  expect(sucesso).toBe(true);
});

test('IboPlayer - preenche MAC e DevKey corretamente', async ({ page }) => {
  await page.route('https://iboplayer.com/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: IBO_PLAYER_HTML })
  );

  nock('http://2captcha.com').post('/in.php').reply(200, { status: 1, request: 'id-002' });
  nock('http://2captcha.com')
    .get('/res.php')
    .query({ key: 'test-captcha-key', action: 'get', id: 'id-002', json: '1' })
    .reply(200, { status: 1, request: 'XYZ999' });

  await lib.atualizarLinks();

  await iboPlayer.processarCliente('AA:BB:CC:DD:EE:FF', 'KEY-TEST', 'BIT', page);

  // Verifica que os campos foram preenchidos com os valores corretos
  const mac = await page.locator('#max-address').inputValue();
  const key = await page.locator('#device-key').inputValue();
  expect(mac).toBe('AA:BB:CC:DD:EE:FF');
  expect(key).toBe('KEY-TEST');
});

test('IboPlayer - retorna false quando elemento essencial não existe', async ({ page }) => {
  // Serve HTML vazio - nenhum elemento será encontrado
  await page.route('https://iboplayer.com/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: '<html><body></body></html>' })
  );

  const sucesso = await iboPlayer.processarCliente(
    '00:11:22:33:44:55', 'KEY', 'TVS', page, 2 // máximo 2 tentativas
  );

  expect(sucesso).toBe(false);
});

test('IboPlayer - tenta novamente após falha e retorna true', async ({ page }) => {
  let tentativas = 0;

  // Primeira chamada: retorna HTML quebrado. Segunda: retorna HTML correto.
  await page.route('https://iboplayer.com/**', (route) => {
    tentativas++;
    if (tentativas <= 2) {
      route.fulfill({ status: 200, contentType: 'text/html', body: '<html><body></body></html>' });
    } else {
      route.fulfill({ status: 200, contentType: 'text/html', body: IBO_PLAYER_HTML });
    }
  });

  nock('http://2captcha.com').post('/in.php').reply(200, { status: 1, request: 'id-003' });
  nock('http://2captcha.com')
    .get('/res.php')
    .query({ key: 'test-captcha-key', action: 'get', id: 'id-003', json: '1' })
    .reply(200, { status: 1, request: 'RETRY_OK' });

  await lib.atualizarLinks();

  const sucesso = await iboPlayer.processarCliente(
    'd6:f5:72:a3:0a:55', 'KEY', 'TVS', page, 3 // permite até 3 tentativas
  );

  expect(sucesso).toBe(true);
  expect(tentativas).toBeGreaterThan(1); // confirma que houve retry
});
