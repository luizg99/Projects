const fs=require('fs');
const {chromium}=require('playwright');
const key=fs.readFileSync('.env','utf8').match(/^LEMBRETINHO_API_KEY=(.*)$/m)?.[1]?.trim();
(async()=>{
 const api=await fetch('https://lembretinho.com.br/api/integracoes/v1/playlists',{headers:{'X-API-Key':key,Accept:'application/json'}});
 if(!api.ok)throw new Error(`API HTTP ${api.status}`);
 const {dados}=await api.json();
 const record=dados.find(r=>r.aplicativo?.nome==='QUICK PLAYER'&&r.ativo&&r.macAddress);
 if(!record)throw new Error('Nenhum registro Quick Player ativo');
 const browser=await chromium.launch({headless:true});
 try{
  const page=await browser.newPage();
  await page.goto('https://quickplayer.app/#/login',{waitUntil:'domcontentloaded',timeout:45000});
  await page.waitForTimeout(2000);
  const mac=page.locator('input[placeholder="Endereço MAC"]');
  await mac.fill(record.macAddress);
  const submit=page.locator('button[type=submit],input[type=submit]').first();
  const disabled=await submit.isDisabled().catch(()=>false);
  if(!disabled)await submit.click({timeout:5000});
  await page.waitForTimeout(2500);
  console.log(JSON.stringify({login:disabled?'validation rejected':'submitted',url:page.url(),title:await page.title(),headings:await page.locator('h1,h2,h3').allTextContents(),labels:await page.locator('label').allTextContents(),buttons:await page.locator('button').evaluateAll(els=>els.map(el=>({text:el.innerText.trim().slice(0,70),disabled:el.disabled,aria:el.getAttribute('aria-label')}))),inputs:await page.locator('input:not([type=hidden])').evaluateAll(els=>els.map(el=>({type:el.type,placeholder:el.placeholder||null,name:el.name||null,hasValue:Boolean(el.value)}))),lists:await page.locator('table tbody tr').count()}));
 }finally{await browser.close();}
})().catch(e=>{console.error(e.message);process.exitCode=1;});
