const { chromium } = require('playwright');
const fs = require('fs');
(async () => {
 if(process.argv.includes('--scan')) {
  for(const file of fs.readdirSync('.local').filter(f=>f.endsWith('.js') && (!process.argv[3] || f.startsWith(process.argv[3])))) {
   const s=fs.readFileSync('.local/'+file,'utf8');
   const hits=[...s.matchAll(/xtream|playlist_type|Playlist Host|server_url|playlistType|name:"host"|name:"url"|"XC"|get\.php|playlist\/[^"` ]+/gi)];
   if(hits.length) console.log(file, hits.slice(0,22).map(m=>s.slice(Math.max(0,m.index-170),m.index+300)).join('\n'));
  }
  return;
 }
 const browser = await chromium.launch({headless:true});
 try {
  await Promise.all(['https://iboplayer.pro/manage-playlists/list/','https://playbr.app.br/conta/login','https://quickplayer.app/#/upload-playlist','https://vuplayer.pro/playlists?type=list'].map(async url => {
   const page = await browser.newPage();
   const host = new URL(url).hostname;
   let count = 0;
   const pending = [];
   page.on('response', response => {
    if (response.request().resourceType() === 'script' && new URL(response.url()).hostname === host) {
     pending.push(response.text().then(body => fs.writeFileSync(`.local/${host}-${count++}.js`,body)).catch(()=>{}));
    }
   });
   try {
    await page.goto(url,{waitUntil:'domcontentloaded',timeout:40000});
    await page.waitForTimeout(3000);
    console.log(JSON.stringify({site:url,url:page.url(),text:(await page.locator('body').innerText()).slice(0,9000),elements:await page.locator('a,input').evaluateAll(els=>els.map(el=>({tag:el.tagName,id:el.id,name:el.name,type:el.type,text:el.innerText,href:el.getAttribute('href')})))}));
    await Promise.allSettled(pending);
   } catch(e) {console.log(host,e.message);}
   finally {await page.close();}
  }));
 } finally {await browser.close();}
})();
