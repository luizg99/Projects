const { test, expect } = require('@playwright/test');
const nock = require('nock');

const QUICK_HTML = (urlOriginal) => `<!DOCTYPE html>
<html lang="pt-BR">
<body>
  <main id="login">
    <input class="input" aria-label="Mac address" />
    <button type="button" id="by-mac">ENTRAR COM O MAC</button>
  </main>
  <div class="ant-modal" id="key-modal" style="display:none">
    <form id="key-form">
      <input class="modal-input" aria-label="Chave do dispositivo" />
      <button type="submit">Entrar</button>
    </form>
  </div>
  <main id="playlists" style="display:none">
    <button type="button" aria-label="Editar playlist">Editar</button>
    <button type="button" id="logout">SAIR</button>
  </main>
  <div class="ant-modal" id="edit-modal" style="display:none">
    <form id="edit-form">
      <label>Nome da playlist</label><input class="modal-input" value="Teste" />
      <label>Url da playlist</label><input class="modal-input" value="${urlOriginal}" />
      <label>Url do EPG (guia)</label><input class="modal-input" value="" />
      <button type="submit">Salvar</button>
    </form>
  </div>
  <div class="ant-modal" id="logout-modal" style="display:none">
    <button type="button" id="confirm-logout">SAIR</button>
  </div>
  <script>
    const keyModal = document.querySelector('#key-modal');
    const playlists = document.querySelector('#playlists');
    const editModal = document.querySelector('#edit-modal');
    const logoutModal = document.querySelector('#logout-modal');
    document.querySelector('#by-mac').addEventListener('click', () => { keyModal.style.display = 'block'; });
    document.querySelector('#key-form').addEventListener('submit', (event) => {
      event.preventDefault();
      window.__loginCaptured = {
        mac: document.querySelector('#login input').value,
        key: keyModal.querySelector('input').value,
      };
      keyModal.style.display = 'none';
      document.querySelector('#login').style.display = 'none';
      playlists.style.display = 'block';
    });
    playlists.querySelector('[aria-label="Editar playlist"]').addEventListener('click', () => { editModal.style.display = 'block'; });
    document.querySelector('#edit-form').addEventListener('submit', (event) => {
      event.preventDefault();
      window.__savedUrl = document.querySelectorAll('#edit-form input')[1].value;
      editModal.style.display = 'none';
    });
    document.querySelector('#logout').addEventListener('click', () => { logoutModal.style.display = 'block'; });
    document.querySelector('#confirm-logout').addEventListener('click', () => {
      logoutModal.style.display = 'none';
      playlists.style.display = 'none';
      document.querySelector('#login').style.display = 'block';
    });
  </script>
</body>
</html>`;

let lib;
let quickPlayer;

test.beforeEach(async () => {
  nock.cleanAll();
  delete require.cache[require.resolve('../src/biblioteca')];
  delete require.cache[require.resolve('../src/sites/quickPlayer')];
  lib = require('../src/biblioteca');
  quickPlayer = require('../src/sites/quickPlayer');

  lib.atualizarLinksDaApi([
    { nomeServidor: 'TVS', urlAtual: 'http://tvs.test.com' },
    { nomeServidor: 'UNIPLAY', urlAtual: 'http://uniplay.test.com' },
    { nomeServidor: 'M3U-TEST', urlAtual: 'http://m3u-host.test.com', m3u: 'http://new.example/get.php?token=unused' },
    { nomeServidor: 'FAST', urlAtual: 'http://fast.test.com' },
  ]);
});

test.afterEach(() => nock.cleanAll());

async function mockQuickPlayer(page, url) {
  await page.route('https://quickplayer.app/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: QUICK_HTML(url) })
  );
}

test('QuickPlayer - login direto e formulário Xtream atualizam somente o host', async ({ page }) => {
  await page.route('https://quickplayer.app/**', (route) => route.fulfill({
    status: 200,
    contentType: 'text/html',
    body: `<meta charset="utf-8">
      <form id="login-form">
        <input id="device-mac" aria-label="Endereço MAC" />
        <input id="device-key" />
        <button id="login" type="submit">Enter my space</button>
      </form>
      <div id="playlists" style="display:none">
        <button aria-label="Editar playlist">Editar</button>
        <button id="logout">SAIR</button>
      </div>
      <div class="ant-modal" id="edit-modal" style="display:none">
        <form>
          <input class="modal-input" value="Teste" />
          <input id="edit-host" class="modal-input" value="http://old.example" />
          <input id="edit-user" value="cliente" />
          <input id="edit-password" value="senha" />
          <button type="submit">Salvar</button>
        </form>
      </div>
      <script>
        document.querySelector('#login-form').onsubmit = (event) => {
          event.preventDefault();
          window.__loginCaptured = [document.querySelector('#device-mac').value, document.querySelector('#device-key').value];
          document.querySelector('#playlists').style.display = 'block';
        };
        document.querySelector('[aria-label="Editar playlist"]').onclick = () => {
          document.querySelector('#edit-modal').style.display = 'block';
        };
        document.querySelector('#edit-modal form').onsubmit = (event) => {
          event.preventDefault();
          window.__saved = {
            host: document.querySelector('#edit-host').value,
            user: document.querySelector('#edit-user').value,
            password: document.querySelector('#edit-password').value,
          };
          document.querySelector('#edit-modal').style.display = 'none';
        };
        document.querySelector('#logout').onclick = () => {
          document.querySelector('#playlists').style.display = 'none';
        };
      </script>`,
  }));

  const sucesso = await quickPlayer.processarCliente('00:11:22:33:44:55', '123456', 'UNIPLAY', page);

  expect(sucesso).toBe(true);
  expect(await page.evaluate(() => window.__loginCaptured)).toEqual(['00:11:22:33:44:55', '123456']);
  expect(await page.evaluate(() => window.__saved)).toEqual({
    host: 'http://uniplay.test.com', user: 'cliente', password: 'senha',
  });
});

test('QuickPlayer - atualiza host Xtream preservando caminho e credenciais', async ({ page }) => {
  await mockQuickPlayer(page, 'http://old.example:8080/get.php?username=cliente&password=senha&type=m3u_plus&output=ts');

  const sucesso = await quickPlayer.processarCliente('00:11:22:33:44:55', '123456', 'UNIPLAY', page);

  expect(sucesso).toBe(true);
  expect(await page.evaluate(() => window.__savedUrl))
    .toBe('http://uniplay.test.com/get.php?username=cliente&password=senha&type=m3u_plus&output=ts');
  expect(await page.evaluate(() => window.__loginCaptured))
    .toEqual({ mac: '00:11:22:33:44:55', key: '123456' });
});

test('QuickPlayer - identifica M3U por output=hls e preserva os parâmetros', async ({ page }) => {
  await mockQuickPlayer(page, 'http://old.example/get.php?username=cliente&password=senha&type=m3u_plus&output=hls');

  const sucesso = await quickPlayer.processarCliente('AA:BB:CC:DD:EE:FF', '654321', 'M3U-TEST', page);

  expect(sucesso).toBe(true);
  expect(await page.evaluate(() => window.__savedUrl))
    .toBe('http://new.example/get.php?username=cliente&password=senha&type=m3u_plus&output=hls');
});

test('QuickPlayer - não salva URL sem assinatura reconhecida', async ({ page }) => {
  await mockQuickPlayer(page, 'https://old.example/playlist?token=private');

  const sucesso = await quickPlayer.processarCliente('00:11:22:33:44:55', '123456', 'TVS', page);

  expect(sucesso).toBe(false);
  expect(await page.evaluate(() => window.__savedUrl)).toBeUndefined();
});

test('QuickPlayer - identifica a assinatura Xtream gerada pelo app', async () => {
  expect(quickPlayer.classificarPlaylist(
    'https://host.example/get.php?username=u&password=p&type=m3u_plus&output=ts',
  )).toBe('xtream');
  expect(quickPlayer.classificarPlaylist(
    'https://host.example/get.php?username=u&password=p&type=m3u_plus&output=hls',
  )).toBe('m3u');
});
