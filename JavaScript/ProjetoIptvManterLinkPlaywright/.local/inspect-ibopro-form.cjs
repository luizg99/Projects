const fs = require('fs');
const { chromium } = require('playwright');

const env = fs.readFileSync('.env', 'utf8');
const apiKey = env.match(/^LEMBRETINHO_API_KEY=(.*)$/m)?.[1]?.trim();
if (!apiKey) throw new Error('LEMBRETINHO_API_KEY não configurada.');
let currentStage = 'api';

(async () => {
  const response = await fetch('https://lembretinho.com.br/api/integracoes/v1/playlists', {
    headers: { 'X-API-Key': apiKey, Accept: 'application/json' },
  });
  if (!response.ok) throw new Error(`API retornou HTTP ${response.status}.`);
  const { dados } = await response.json();
  const record = dados.find((row) => row.aplicativo?.nome === 'IBO PRO PLAYER APP'
    && row.ativo && row.atualizar === 'S' && row.macAddress && row.deviceKey);
  if (!record) throw new Error('Nenhum registro ativo elegível para inspeção.');

  const browser = await chromium.launch({ headless: true });
  try {
    currentStage = 'page';
    const page = await browser.newPage();
    await page.goto('https://iboplayer.pro/manage-playlists/list/', {
      waitUntil: 'domcontentloaded', timeout: 45000,
    });
    if (await page.locator('#mac_address').count()) {
      await page.locator('#mac_address').fill(record.macAddress);
      await page.locator('#password').fill(record.deviceKey);
      await page.locator('xpath=/html/body/div[1]/div[3]/div/form/button').click();
    }
    currentStage = 'dashboard';
    await page.waitForTimeout(4000);
    const rows = page.locator('table tbody tr');
    if (!await rows.count()) {
      const safeState = await page.evaluate(() => ({
        title: document.title,
        headings: [...document.querySelectorAll('h1,h2,h3')].map((el) => el.innerText.trim()),
        fields: [...document.querySelectorAll('input:not([type="hidden"])')].map((el) => ({
          id: el.id || null, name: el.name || null, type: el.type || null,
        })),
        buttons: [...document.querySelectorAll('button')].map((el) => el.innerText.trim()).filter(Boolean),
      }));
      console.log(JSON.stringify({ stage: currentStage, dashboardFound: false, safeState }));
      return;
    }
    currentStage = 'open-edit';
    const editButton = rows.first().locator('button').first();
    await editButton.waitFor({ timeout: 15000 });
    await editButton.click();
    currentStage = 'edit-form';
    await page.locator('form:visible').last().waitFor({ timeout: 15000 });
    const result = await page.evaluate(() => {
      const forms = [...document.querySelectorAll('form')].filter((form) => form.getClientRects().length);
      const form = forms.at(-1);
      return {
        headings: [...document.querySelectorAll('h1,h2,h3,[role="heading"]')]
          .map((el) => el.innerText.trim()).filter(Boolean),
        labels: [...(form?.querySelectorAll('label') ?? [])].map((el) => el.innerText.trim()).filter(Boolean),
        fields: [...(form?.querySelectorAll('input,select,textarea') ?? [])].map((el) => ({
          tag: el.tagName, type: el.getAttribute('type'), id: el.id || null,
          name: el.getAttribute('name'), placeholder: el.getAttribute('placeholder'),
          hasValue: Boolean(el.value),
        })),
        buttons: [...(form?.querySelectorAll('button') ?? [])].map((el) => ({
          text: el.innerText.trim(), type: el.getAttribute('type'),
        })),
      };
    });
    console.log(JSON.stringify({ inspection: 'read-only; no save performed', ...result }));
  } finally {
    await browser.close();
  }
})().catch((error) => {
  console.error(JSON.stringify({ stage: currentStage, error: error.name || 'InspectionError', message: 'Inspeção não concluída.' }));
  process.exitCode = 1;
});
