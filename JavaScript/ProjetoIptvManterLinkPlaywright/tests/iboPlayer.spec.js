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
//  M3U URL : #playlist-url
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
            <div><input id="playlist-url" type="text" value="http://old.test/get.php?username=user1&password=pass1&type=m3u&output=ts" /></div>
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

test.beforeEach(async ({ page }) => {
  page.waitForTimeout = async () => {};
  nock.cleanAll();
  delete require.cache[require.resolve('../src/biblioteca')];
  delete require.cache[require.resolve('../src/sites/iboPlayer')];
  lib = require('../src/biblioteca');
  iboPlayer = require('../src/sites/iboPlayer');
  lib.captchaApiKey = 'test-captcha-key';
  lib.solveCaptchaWith2Captcha = async () => 'CAPTCHA-TESTE';

  lib.atualizarLinksDaApi([
    { nomeServidor: 'TVS', urlAtual: 'http://tvs.test.com', m3u: 'http://tvs-m3u.test/get.php?' },
    { nomeServidor: 'UNIPLAY', urlAtual: 'http://uniplay.test.com', m3u: 'http://uniplay-m3u.test/get.php?' },
    { nomeServidor: 'PLAYON', urlAtual: 'http://playon.test.com', m3u: 'http://playon-m3u.test/get.php?' },
    { nomeServidor: 'FAST', urlAtual: 'http://fast.test.com', m3u: 'http://fast-m3u.test/get.php?' },
  ]);
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
  nock('https://2captcha.com')
    .post('/in.php')
    .reply(200, { status: 1, request: 'captcha-id-001' });

  nock('https://2captcha.com')
    .get('/res.php')
    .query({ key: 'test-captcha-key', action: 'get', id: 'captcha-id-001', json: '1' })
    .reply(200, { status: 1, request: 'ABC123' });


  const sucesso = await iboPlayer.processarCliente(
    '00:11:22:33:44:55', 'DEVICE_KEY', 'TVS', page
  );

  expect(sucesso).toBe(true);
});

test('IboPlayer - preenche MAC e DevKey corretamente', async ({ page }) => {
  await page.route('https://iboplayer.com/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: IBO_PLAYER_HTML })
  );

  nock('https://2captcha.com').post('/in.php').reply(200, { status: 1, request: 'id-002' });
  nock('https://2captcha.com')
    .get('/res.php')
    .query({ key: 'test-captcha-key', action: 'get', id: 'id-002', json: '1' })
    .reply(200, { status: 1, request: 'XYZ999' });


  await iboPlayer.processarCliente('AA:BB:CC:DD:EE:FF', 'KEY-TEST', 'PLAYON', page);

  // Verifica que os campos foram preenchidos com os valores corretos
  const mac = await page.locator('#max-address').inputValue();
  const key = await page.locator('#device-key').inputValue();
  expect(mac).toBe('AA:BB:CC:DD:EE:FF');
  expect(key).toBe('KEY-TEST');
});

test('IboPlayer - atualiza apenas a base da URL M3U e preserva os parâmetros', async ({ page }) => {
  await page.route('https://iboplayer.com/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: IBO_PLAYER_HTML })
  );
  nock('https://2captcha.com').post('/in.php').reply(200, { status: 1, request: 'id-m3u' });
  nock('https://2captcha.com').get('/res.php')
    .query({ key: 'test-captcha-key', action: 'get', id: 'id-m3u', json: '1' })
    .reply(200, { status: 1, request: 'M3U_OK' });

  await iboPlayer.processarCliente('AA:BB:CC:DD:EE:FF', 'KEY', 'TVS', page);

  await expect(page.locator('#playlist-url')).toHaveValue(
    'http://tvs-m3u.test/get.php?username=user1&password=pass1&type=m3u&output=ts'
  );
});

test('IboPlayer - Xtream atualiza o host sem alterar usuário e senha', async ({ page }) => {
  const xtreamHtml = IBO_PLAYER_HTML.replace(
    '<input id="playlist-url" type="text" value="http://old.test/get.php?username=user1&password=pass1&type=m3u&output=ts" />',
    '<input id="playlist-url" type="text" value="http://old.test/get.php?username=user1&password=pass1&type=m3u&output=ts" /><input id="host" name="host" type="text" value="http://old.test" /><input id="username" name="username" value="user1" /><input id="password" name="password" type="password" value="pass1" />'
  );
  await page.route('https://iboplayer.com/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: xtreamHtml })
  );
  nock('https://2captcha.com').post('/in.php').reply(200, { status: 1, request: 'id-xtream' });
  nock('https://2captcha.com').get('/res.php')
    .query({ key: 'test-captcha-key', action: 'get', id: 'id-xtream', json: '1' })
    .reply(200, { status: 1, request: 'XTREAM_OK' });

  await iboPlayer.processarCliente('AA:BB:CC:DD:EE:FF', 'KEY', 'TVS', page);

  await expect(page.locator('#host')).toHaveValue('http://tvs.test.com');
  await expect(page.locator('#username')).toHaveValue('user1');
  await expect(page.locator('#password')).toHaveValue('pass1');
  await expect(page.locator('#playlist-url')).toHaveValue(
    'http://old.test/get.php?username=user1&password=pass1&type=m3u&output=ts'
  );
});

test('IboPlayer - não salva M3U se a API não enviar a nova base', async ({ page }) => {
  lib.atualizarLinksDaApi([{ nomeServidor: 'TVS', urlAtual: 'http://tvs.test.com' }]);
  await page.route('https://iboplayer.com/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: IBO_PLAYER_HTML })
  );

  const sucesso = await iboPlayer.processarCliente('AA:BB:CC:DD:EE:FF', 'KEY', 'TVS', page);

  expect(sucesso).toBe(false);
  await expect(page.locator('#playlist-url')).toHaveValue(
    'http://old.test/get.php?username=user1&password=pass1&type=m3u&output=ts'
  );
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
    if (tentativas <= 1) {
      route.fulfill({ status: 200, contentType: 'text/html', body: '<html><body></body></html>' });
    } else {
      route.fulfill({ status: 200, contentType: 'text/html', body: IBO_PLAYER_HTML });
    }
  });

  nock('https://2captcha.com').post('/in.php').reply(200, { status: 1, request: 'id-003' });
  nock('https://2captcha.com')
    .get('/res.php')
    .query({ key: 'test-captcha-key', action: 'get', id: 'id-003', json: '1' })
    .reply(200, { status: 1, request: 'RETRY_OK' });


  const sucesso = await iboPlayer.processarCliente(
    '00:11:22:33:44:55', 'KEY', 'TVS', page, 3 // permite até 3 tentativas
  );

  expect(sucesso).toBe(true);
  expect(tentativas).toBeGreaterThan(1); // confirma que houve retry
});
