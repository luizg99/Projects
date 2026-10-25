const { chromium } = require('playwright');
(async () => {
  const browser = await chromium.launch({ headless: false });
  try {
    const page = await browser.newPage();
    await page.goto('https://quickplayer.app', { waitUntil: 'domcontentloaded', timeout: 60000 });
    console.log('QUICKPLAYER_ABERTO: faça login e deixe aberta a edição da playlist. A inspeção só lê rótulos e atributos, nunca valores.');
    await page.waitForFunction(() => {
      const modal = document.querySelector('.ant-modal');
      const form = modal?.querySelector('form');
      const visible = (element) => element.getClientRects().length > 0;
      return !!form && [...form.querySelectorAll('input')].filter(visible).length >= 2
        && [...form.querySelectorAll('label')].some((label) => /url|playlist/i.test(label.textContent || ''));
    }, null, { timeout: 300000 });
    const fields = await page.evaluate(() => {
      const modal = document.querySelector('.ant-modal');
      const form = modal?.querySelector('form');
      const visible = (element) => element.getClientRects().length > 0;
      return {
        title: modal?.querySelector('.ant-modal-title')?.textContent?.trim() || null,
        labels: [...(form?.querySelectorAll('label') || [])].map((label) => label.textContent.trim()).filter(Boolean),
        inputs: [...(form?.querySelectorAll('input') || [])].filter(visible).map((input) => ({
          type: input.type,
          id: input.id || null,
          name: input.name || null,
          ariaLabel: input.getAttribute('aria-label'),
        })),
      };
    });
    console.log('QUICKPLAYER_FORMULARIO', JSON.stringify(fields));
    console.log('Inspeção concluída sem salvar; janela fica aberta por 2 minutos.');
    await page.waitForTimeout(120000);
  } finally { await browser.close(); }
})().catch(() => { console.error('Inspeção do QuickPlayer não concluída.'); process.exitCode = 1; });
