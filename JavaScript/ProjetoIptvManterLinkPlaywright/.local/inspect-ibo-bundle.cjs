const { request } = require('playwright');
(async () => {
  const context = await request.newContext();
  try {
    const response = await context.get('https://iboplayer.com/assets/index-Bf-ycfbx.js');
    const source = await response.text();
    console.log('HTTP', response.status());
    for (const [name, start, end] of [['M3U','S9=({currentPlaylist','N9=({currentPlaylist'],['Xtream','N9=({currentPlaylist','C9=({currentPlaylist']]) {
      const section = source.slice(source.indexOf(start), source.indexOf(end));
      console.log(name, JSON.stringify([...section.matchAll(/(?:htmlFor|id):"[^"]+"|children:"[^"]+"/g)].map(m => m[0])));
    }
  } finally { await context.dispose(); }
})().catch(error => { console.error(error.message); process.exitCode = 1; });
