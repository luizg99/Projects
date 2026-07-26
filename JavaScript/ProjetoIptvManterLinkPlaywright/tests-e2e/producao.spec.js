const { test, expect } = require('@playwright/test');
const lib = require('../src/biblioteca');
const iboPlayer = require('../src/sites/iboPlayer');
const iboPlayerPro = require('../src/sites/iboPlayerPro');
const quickPlayer = require('../src/sites/quickPlayer');
const vuPlayer = require('../src/sites/vuPlayer');

// Testa a atualização de playlist real (sem mocks) em 1 cliente por site,
// usando a planilha de clientes de produção. Roda separado da suíte padrão:
//   npx playwright test --config=e2e.config.js
const SHEET_URL_CLIENTES =
  'https://docs.google.com/spreadsheets/d/1ifSYQKY2W-DA0D0wYY00tKag90Tp5FJP3mdZ0lConUs/export?format=csv&gid=0';

lib.captchaApiKey = '0439b8069b74afca88f8062c8eb51716';

const SITES = {
  'iboplayer.com': { modulo: iboPlayer, nome: 'IboPlayer' },
  'iboplayer.pro': { modulo: iboPlayerPro, nome: 'IboPlayerPro' },
  'quickplayer.app': { modulo: quickPlayer, nome: 'QuickPlayer' },
  'vuplayer.pro': { modulo: vuPlayer, nome: 'VuPlayer' },
};

const clientesPorSite = {};

test.beforeAll(async () => {
  await lib.atualizarLinks();

  const clientes = await lib.getGoogleSheetData(SHEET_URL_CLIENTES);

  for (const row of clientes) {
    const siteAtivacao = (row['Site ativação'] || '').trim();
    const ativo = (row['Ativo'] || '').trim();
    const atualizar = (row['Atualizar'] || '').trim();

    if (ativo !== 'S' || atualizar !== 'S') continue;
    if (!SITES[siteAtivacao]) continue;
    if (clientesPorSite[siteAtivacao]) continue; // já achou um cliente pra esse site

    const macAddress = (row['MAC Address'] || '').trim();
    const deviceKey = (row['Device Key'] || '').trim();
    if (!macAddress || !deviceKey) continue; // pula linhas com dados incompletos

    clientesPorSite[siteAtivacao] = {
      macAddress,
      deviceKey,
      servidor: (row['Servidor'] || '').trim(),
    };
  }
});

for (const siteAtivacao of Object.keys(SITES)) {
  test(`${SITES[siteAtivacao].nome} - atualiza playlist real com sucesso`, async ({ page }) => {
    const cliente = clientesPorSite[siteAtivacao];
    test.skip(!cliente, `Nenhum cliente elegível (Ativo=S, Atualizar=S) encontrado para ${siteAtivacao} na planilha real`);

    console.log(`Testando ${siteAtivacao} com MAC real: ${cliente.macAddress}`);

    const sucesso = await SITES[siteAtivacao].modulo.processarCliente(
      cliente.macAddress,
      cliente.deviceKey,
      cliente.servidor,
      page
    );

    expect(sucesso).toBe(true);
  });
}
