const { test, expect } = require('@playwright/test');
const nock = require('nock');

// ─── Mock HTML ────────────────────────────────────────────────────────────────
// Seletores usados em quickPlayer.js:
//
//  MAC       : #mac
//  Loop btn  : xpath=//*[@id="login"]/div/div[2]/div/div/div/div/button
//  DevKey    : #login_code
//  Login btn : xpath=/html/body/div[2]/div/div[2]/div/div[2]/div/div[3]/div/div/div/div/div/button
//  SVG edit  : svg[width='29'][height='29']
//  URL field : #url  (deve ter valor com /get.php para split funcionar)
//  Salvar    : xpath=/html/body/div[2]/div/div[2]/div/div[2]/div/div/form/div/div[7]/button
//  Logout    : xpath=/html/body/div[1]/div/section/header/div[3]/button[2]
//  Confirmar : xpath=/html/body/div[3]/div/div[2]/div/div[2]/div/div/div[2]/button[2]
//
// Estrutura body:
//  div[1] ← shell do app  (header com logout)
//  div[2] ← conteúdo principal
//  div[3] ← diálogo de confirmação de logout

const QUICK_HTML = `<!DOCTYPE html>
<html>
<head><title>QuickPlayer Mock</title></head>
<body>
  <div id="app-shell">
    <div>
      <section>
        <header>
          <div></div>
          <div></div>
          <div>
            <button type="button">Perfil</button>
            <button type="button" id="logout-btn">Sair</button>
          </div>
        </header>
      </section>
    </div>
  </div>

  <div id="main-content">
    <div>
      <div id="second-div">
        <div>
          <div>
            <div id="login">
              <div>
                <div></div>
                <div>
                  <div>
                    <div>
                      <div>
                        <div>
                          <button type="button" id="next-btn">Próximo</button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div>
              <div>
                <input id="mac" type="text" />
                <input id="login_code" type="text" />
              </div>
            </div>
            <div>
              <div>
                <div>
                  <div>
                    <div>
                      <button type="button" id="login-btn">Entrar</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div>
              <form id="edit-form">
                <div>
                  <div></div>
                  <div></div>
                  <div></div>
                  <div></div>
                  <div></div>
                  <div></div>
                  <div>
                    <button type="button" id="save-btn">Salvar</button>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div id="confirm-dialog">
    <div>
      <div>
        <div>
          <div>
            <div>
              <div>
                <div>
                  <div>
                    <button type="button">Cancelar</button>
                    <button type="button" id="confirm-btn">Confirmar</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <svg width="29" height="29" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
       id="svg-edit" style="cursor:pointer">
    <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z"/>
  </svg>

  <input id="url" type="text"
    value="http://old-servidor.com/get.php?username=usuario1&password=senha1" />
</body>
</html>`;

// ─── Setup e teardown ─────────────────────────────────────────────────────────

let lib;
let quickPlayer;

test.beforeEach(async () => {
  nock.cleanAll();
  delete require.cache[require.resolve('../src/biblioteca')];
  delete require.cache[require.resolve('../src/sites/quickPlayer')];
  lib = require('../src/biblioteca');
  quickPlayer = require('../src/sites/quickPlayer');

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

test('QuickPlayer - processa cliente com sucesso', async ({ page }) => {
  await page.route('https://quickplayer.app/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: QUICK_HTML })
  );

  const sucesso = await quickPlayer.processarCliente(
    '00:11:22:33:44:55', 'DEVICE_KEY', 'TVS', page
  );

  expect(sucesso).toBe(true);
});

test('QuickPlayer - constrói nova URL preservando usuário e senha', async ({ page }) => {
  await page.route('https://quickplayer.app/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: QUICK_HTML })
  );

  await quickPlayer.processarCliente('AA:BB:CC:DD:EE:FF', 'KEY', 'UNIPLAY', page);

  // Após processamento, o campo #url deve conter o novo host + os params originais
  const novaUrl = await page.locator('#url').inputValue();
  expect(novaUrl).toBe('http://uniplay.test.com/get.php?username=usuario1&password=senha1');
});

test('QuickPlayer - retorna false quando elemento não existe', async ({ page }) => {
  await page.route('https://quickplayer.app/**', (route) =>
    route.fulfill({ status: 200, contentType: 'text/html', body: '<html><body></body></html>' })
  );

  const sucesso = await quickPlayer.processarCliente(
    '00:11:22:33:44:55', 'KEY', 'TVS', page, 2
  );

  expect(sucesso).toBe(false);
});
