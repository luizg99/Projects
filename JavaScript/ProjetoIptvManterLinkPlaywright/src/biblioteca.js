const axios = require('axios');

const SITE_POR_HOST = new Map([
  ['iboplayer.com', 'iboplayer.com'],
  ['iboplayer.pro', 'iboplayer.pro'],
  ['quickplayer.app', 'quickplayer.app'],
  ['vuplayer.pro', 'vuplayer.pro'],
  ['playbr.app.br', 'playbr.app.br'],
]);
let linksPorServidor = new Map();
let linksM3UPorServidor = new Map();
let captchaApiKey = process.env.CAPTCHA_API_KEY || '';

function obterSiteAtivacao(urlSite) {
  if (!urlSite) return '';
  try {
    const host = new URL(urlSite).hostname.toLowerCase().replace(/^www\./, '');
    return SITE_POR_HOST.get(host) || '';
  } catch {
    return '';
  }
}

function atualizarLinksDaApi(playlists) {
  linksPorServidor = new Map();
  linksM3UPorServidor = new Map();
  for (const playlist of playlists) {
    const servidor = playlist.nomeServidor || playlist.servidor;
    if (servidor && playlist.urlAtual) linksPorServidor.set(servidor, playlist.urlAtual);
    if (servidor && playlist.m3u) linksM3UPorServidor.set(servidor, playlist.m3u);
  }
}

function obterLinkAtualizado(servidor) {
  return linksPorServidor.get(servidor) || '';
}

function obterLinkM3UAtualizado(servidor) {
  return linksM3UPorServidor.get(servidor) || '';
}

function substituirBaseM3U(urlOriginal, novaBase) {
  if (!urlOriginal || !novaBase) return '';
  const marcadorOriginal = urlOriginal.toLowerCase().indexOf('.php?');
  const marcadorBase = novaBase.toLowerCase().indexOf('.php?');
  if (marcadorOriginal < 0 || marcadorBase < 0) return '';

  const parametros = urlOriginal.slice(marcadorOriginal + '.php?'.length);
  return `${novaBase.slice(0, marcadorBase + '.php?'.length)}${parametros}`;
}

async function solveCaptchaWith2Captcha(imageBuffer, apiKey = captchaApiKey) {
  if (!apiKey) throw new Error('Configure CAPTCHA_API_KEY no arquivo .env.');
  const params = new URLSearchParams({
    key: apiKey,
    method: 'base64',
    body: imageBuffer.toString('base64'),
    json: '1',
  });
  const respostaEnvio = await axios.post('https://2captcha.com/in.php', params);
  if (respostaEnvio.data.status !== 1) {
    throw new Error(`Erro ao enviar CAPTCHA para 2Captcha: ${respostaEnvio.data.request}`);
  }

  const captchaId = respostaEnvio.data.request;
  const resultadoUrl = `https://2captcha.com/res.php?key=${apiKey}&action=get&id=${captchaId}&json=1`;
  while (true) {
    await new Promise((resolve) => setTimeout(resolve, 5000));
    const resposta = await axios.get(resultadoUrl);
    if (resposta.data.status === 1) return resposta.data.request;
    if (resposta.data.request !== 'CAPCHA_NOT_READY') {
      throw new Error(`Erro ao resolver CAPTCHA: ${resposta.data.request}`);
    }
  }
}

module.exports = {
  obterSiteAtivacao,
  atualizarLinksDaApi,
  obterLinkAtualizado,
  obterLinkM3UAtualizado,
  substituirBaseM3U,
  solveCaptchaWith2Captcha,
  get captchaApiKey() { return captchaApiKey; },
  set captchaApiKey(valor) { captchaApiKey = valor; },
};
