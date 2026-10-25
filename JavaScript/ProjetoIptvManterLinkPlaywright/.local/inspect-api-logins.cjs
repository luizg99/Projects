const fs = require('fs');
const { chromium } = require('playwright');

const key = fs.readFileSync('.env', 'utf8').match(/^LEMBRETINHO_API_KEY=(.*)$/m)?.[1]?.trim();
const apps = [
  ['IBO PLAYER APP', 'https://iboplayer.com/device/login'],
  ['IBO PRO PLAYER APP', 'https://iboplayer.pro/manage-playlists/list/'],
  ['PLAY BR', 'https://playbr.app.br/conta/login'],
  ['QUICK PLAYER', 'https://quickplayer.app/#/login'],
  ['VU PLAYER PRO', 'https://vuplayer.pro/playlists?type=list'],
];

(async () => {
  const response = await fetch('https://lembretinho.com.br/api/integracoes/v1/playlists', {headers: {'X-API-Key': key, Accept: 'application/json'}});
  if (!response.ok) throw new Error(`API retornou HTTP ${response.status}`);
  const {dados} = await response.json();
  const browser = await chromium.launch({headless:true});
  try {
    for (const [app, url] of apps) {
      const page = await browser.newPage();
      try {
        await page.goto(url, {waitUntil:'domcontentloaded', timeout:45000});
        await page.waitForTimeout(2200);
        const row = dados.find(x => x.aplicativo?.nome === app && x.ativo && x.macAddress && x.deviceKey);
        const form = await page.locator('input,select,button[type=submit]').evaluateAll(elements => elements.map(el => ({tag:el.tagName,type:el.type||null,id:el.id||null,name:el.name||null,placeholder:el.placeholder||null,text:el.tagName==='BUTTON'?el.innerText.trim().slice(0,80):null})).filter(x => x.tag==='BUTTON' || x.type!=='hidden'));
        console.log(JSON.stringify({app,recordAvailable:Boolean(row),url:page.url(),title:await page.title(),fields:form,hasCaptcha:/captcha/i.test(await page.locator('body').innerText().catch(()=>''))}));
      } catch (error) { console.log(JSON.stringify({app,error:error.message})); }
      finally { await page.close(); }
    }
  } finally { await browser.close(); }
})().catch(error=>{console.error(error.message);process.exitCode=1;});
