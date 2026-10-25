const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: false });
  try {
    const page = await browser.newPage();
    await page.goto('https://iboplayer.com/device/login', { waitUntil: 'domcontentloaded', timeout: 60000 });
    console.log('JANELA_PLAYWRIGHT_PRONTA. Faça login nesta janela; vou detectar quando o dashboard carregar.');
    await page.waitForFunction(() => location.pathname.startsWith('/dashboard') && !!document.querySelector('table') && document.body.innerText.includes('Add Playlist') && !document.querySelector('#max-address'), null, { timeout: 300000 });
    await page.waitForTimeout(1500);
    const rows = page.locator('table tbody tr');
    let editOpened = false;
    if (await rows.count()) {
      const icons = rows.first().locator('td:last-child svg');
      if (await icons.count()) {
        await icons.first().click({ force: true });
        editOpened = true;
      }
    }
    if (editOpened) {
      try {
        await page.waitForFunction(() => location.pathname.includes('/dashboard/edit/') || !!document.querySelector('.swal2-popup input'), null, { timeout: 12000 });
      } catch {}
      if (await page.locator('.swal2-popup input').count()) {
        console.log('PIN_DA_PLAYLIST_NECESSARIO. Digite o PIN na janela para continuar; não o envie pelo chat.');
        await page.waitForURL(/\/dashboard\/edit\//, { timeout: 300000 });
      }
      await page.waitForTimeout(1200);
    }
    const result = await page.locator('input:not([type=hidden]),select,textarea').evaluateAll(elements => elements.map(el => ({
      tag: el.tagName, type: el.getAttribute('type'), id: el.id || null,
      name: el.getAttribute('name'), placeholder: el.getAttribute('placeholder'),
      required: el.required || false, hasValue: Boolean(el.value),
      options: el.tagName === 'SELECT' ? [...el.options].map(option => option.text) : undefined,
    })));
    const labels = await page.locator('label').allTextContents();
    const headings = await page.locator('h1,h2,h3').allTextContents();
    const buttons = await page.locator('button').evaluateAll(elements => elements.map(el => ({ text: el.innerText.trim().slice(0, 80), disabled: el.disabled })));
    const playlistType = result.some(field => field.id === 'host' || field.id === 'username') ? 'Xtream (Host/usuário/senha)' : result.some(field => field.id === 'playlist-url') ? 'M3U (URL completa)' : 'não identificado';
    console.log('EDICAO_INSPECIONADA', JSON.stringify({ url: page.url(), title: await page.title(), editOpened, playlistType, rowCount: await rows.count(), headings, labels, fields: result, buttons }));
    console.log('A janela permanecerá aberta por 5 minutos.');
    await page.waitForTimeout(300000);
  } finally { await browser.close(); }
})().catch(error => { console.error(error.message); process.exitCode = 1; });
