const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true });
  try {
    const page = await browser.newPage();
    await page.goto('https://iboplayer.com/dashboard/edit/6ac81761a661be40d7c33654', { waitUntil: 'domcontentloaded', timeout: 60000 });
    await page.waitForTimeout(2000);
    console.log(JSON.stringify({url: page.url(), title: await page.title(), form: await page.locator('input,select,textarea,button,label,a').evaluateAll(els => els.map(el => ({tag:el.tagName, type:el.getAttribute('type'), name:el.getAttribute('name'), id:el.id, text:el.tagName === 'INPUT' ? undefined : el.innerText?.slice(0,160), placeholder:el.getAttribute('placeholder'), href:el.getAttribute('href')}))), scripts: await page.locator('script[src]').evaluateAll(els => els.map(el => el.src))}, null, 2));
  } finally { await browser.close(); }
})().catch(error => { console.error(error.message); process.exitCode = 1; });
