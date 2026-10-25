const fs = require('fs');

const env = fs.readFileSync('.env', 'utf8');
const key = env.match(/^LEMBRETINHO_API_KEY=(.*)$/m)?.[1]?.trim();
if (!key) throw new Error('LEMBRETINHO_API_KEY não está configurada no .env');

const endpoint = 'https://lembretinho.com.br/api/integracoes/v1/playlists';

(async () => {
  for (const [header, value] of [['Authorization', `Bearer ${key}`], ['X-API-Key', key]]) {
    const response = await fetch(endpoint, { headers: { [header]: value, Accept: 'application/json' } });
    const body = await response.text();
    console.log('HTTP', response.status, 'via', header);
    if (!response.ok) {
      console.log(body.slice(0, 500).replace(/\b(?:https?:\/\/)?[^\s"']+\.(?:m3u8?|php)[^\s"']*/gi, '[URL REDIGIDA]'));
      if (response.status === 401 || response.status === 403) continue;
      break;
    }
    let data;
    try { data = JSON.parse(body); } catch { console.log('Resposta não JSON; tamanho:', body.length); break; }
    const summarize = (value) => {
      if (Array.isArray(value)) return { count: value.length, sampleKeys: value[0] && typeof value[0] === 'object' ? Object.keys(value[0]) : [] };
      if (value && typeof value === 'object') return Object.fromEntries(Object.entries(value).map(([k, v]) => [k, Array.isArray(v) ? summarize(v) : v && typeof v === 'object' ? summarize(v) : typeof v]));
      return typeof value;
    };
    const rows = data?.dados;
    if (!Array.isArray(rows)) { console.log(JSON.stringify(summarize(data), null, 2)); break; }
    const by = (field, nested = null) => Object.fromEntries([...rows.reduce((counts, row) => {
      const raw = row?.[field];
      const value = String(nested ? raw?.[nested] ?? '(vazio)' : raw ?? '(vazio)');
      counts.set(value, (counts.get(value) ?? 0) + 1);
      return counts;
    }, new Map())].sort((a, b) => a[0].localeCompare(b[0])));
    const linkPattern = (value) => {
      if (typeof value !== 'string' || !value.trim()) return null;
      try {
        const url = new URL(value);
        return { host: url.host, path: url.pathname, queryParameters: [...new Set(url.searchParams.keys())].sort() };
      } catch { return { format: 'não é URL absoluta' }; }
    };
    const apps = [...new Set(rows.map(row => row.aplicativo?.nome ?? '(vazio)'))].sort();
    console.log(JSON.stringify({
      total: rows.length,
      aplicativoCampos: Object.keys(rows[0]?.aplicativo ?? {}),
      porAplicativo: by('aplicativo', 'nome'),
      amostraAplicativoSemIds: Object.fromEntries(Object.entries(rows[0]?.aplicativo ?? {}).filter(([key]) => !/id|key|token|senha|password/i.test(key))),
      servidorCampos: Object.keys(rows[0]?.servidor ?? {}),
      amostraServidorSemIds: Object.fromEntries(Object.entries(rows[0]?.servidor ?? {}).filter(([key]) => !/id|key|token|senha|password|url|host/i.test(key))),
      porServidor: by('nomeServidor'),
      m3uPreenchido: rows.filter(row => typeof row.m3u === 'string' && row.m3u.trim()).length,
      m3uPorAplicativo: Object.fromEntries(apps.map(app => [app, rows.filter(row => row.aplicativo?.nome === app && typeof row.m3u === 'string' && row.m3u.trim()).length])),
      padroesUrlAtual: Object.fromEntries(apps.map(app => [app, [...new Set(rows.filter(row => row.aplicativo?.nome === app).map(row => JSON.stringify(linkPattern(row.urlAtual))))].map(pattern => JSON.parse(pattern))])),
      campos: Object.keys(rows[0] ?? {}),
      cursorProximaPagina: data.proximoCursor && typeof data.proximoCursor === 'object' ? Object.keys(data.proximoCursor) : typeof data.proximoCursor,
      temMaisPaginas: Boolean(data.proximoCursor),
    }, null, 2));
    break;
  }
})().catch(error => { console.error(error.message); process.exitCode = 1; });
