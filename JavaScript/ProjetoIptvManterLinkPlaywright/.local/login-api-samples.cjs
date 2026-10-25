const fs = require('fs');
const { chromium } = require('playwright');

const key = fs.readFileSync('.env', 'utf8').match(/^LEMBRETINHO_API_KEY=(.*)$/m)?.[1]?.trim();
const targets = [
  {name:'PLAY BR', url:'https://playbr.app.br/conta/login', mac:'#macAddress', device:'#deviceKey', valid:()=>true},
];

const inspect = async page => ({
  url: page.url(), title: await page.title(),
  headings: await page.locator('h1,h2,h3').allTextContents(),
  labels: await page.locator('label').allTextContents(),
  buttons: await page.locator('button').evaluateAll(els=>els.map(el=>({text:el.innerText.trim().slice(0,80),disabled:el.disabled,aria:el.getAttribute('aria-label')}))),
  inputs: await page.locator('input:not([type=hidden]),select,textarea').evaluateAll(els=>els.map(el=>({id:el.id||null,name:el.name||null,type:el.type||el.tagName,placeholder:el.placeholder||null,required:el.required||false,hasValue:Boolean(el.value)}))),
  formPresent: await page.locator('form').count()>0,
});

(async()=>{
 const api=await fetch('https://lembretinho.com.br/api/integracoes/v1/playlists',{headers:{'X-API-Key':key,Accept:'application/json'}});
 if(!api.ok)throw new Error(`API retornou HTTP ${api.status}`);
 const {dados}=await api.json();
 const browser=await chromium.launch({headless:true});
 try{
  for(const target of targets){
   const record=dados.find(r=>r.aplicativo?.nome===target.name&&r.ativo&&r.macAddress&&r.deviceKey&&target.valid(r));
   if(!record){console.log(JSON.stringify({app:target.name,login:'sem registro ativo com credenciais'}));continue;}
   const page=await browser.newPage();
   try{
    await page.goto(target.url,{waitUntil:'domcontentloaded',timeout:45000});
    await page.locator(target.mac).fill(record.macAddress);
    await page.locator(target.device).fill(record.deviceKey);
    const submit=page.locator('button[type=submit],input[type=submit]').first();
    await page.waitForTimeout(1500);
    const disabled=await submit.isDisabled().catch(()=>false);
    if(!disabled) await submit.click({timeout:5000});
    await page.waitForTimeout(3000);
    let result={app:target.name,login:disabled?'client-side validation rejected selected record':'submitted',...(await inspect(page))};
    if(!disabled&&target.name==='PLAY BR'&&page.url().includes('/conta')){
      const addXtream=page.getByRole('button',{name:'Adicionar Xtream'});
      if(await addXtream.count()){await addXtream.click();await page.waitForTimeout(500);result.formXtream=await inspect(page);}
      const close=page.getByRole('button',{name:'Close'});
      if(await close.count()){await close.click();await page.waitForTimeout(300);}
      const addM3u=page.getByRole('button',{name:'Adicionar M3U'});
      if(await addM3u.count()){await addM3u.click();await page.waitForTimeout(500);result.formM3u=await inspect(page);}
    }
    console.log(JSON.stringify(result));
   }catch(e){console.log(JSON.stringify({app:target.name,error:e.message.slice(0,220),...(await inspect(page).catch(()=>({})))}));}
   finally{await page.close();}
  }
 }finally{await browser.close();}
})().catch(e=>{console.error(e.message);process.exitCode=1;});
