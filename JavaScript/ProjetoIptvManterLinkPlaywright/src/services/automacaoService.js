const { chromium } = require('playwright');
const painelIbo = require('../sites/iboPlayer');
const painelIboPro = require('../sites/iboPlayerPro');
const painelQuick = require('../sites/quickPlayer');
const painelVu = require('../sites/vuPlayer');
const painelPlayerApp = require('../sites/playerApp');
const { isSiteRateLimitError } = require('../errors/siteRateLimitError');
const { INTERVALO_MS } = require('../utils/retryDelay');
const INTERVALO_API_MS = 2100;

// iboplayer.com exige CAPTCHA pago; falhas dele não são repetidas na reexecução.
const SITES_SEM_RETRY = new Set(['iboplayer.com']);
const RETRIES_FALHANTES = 2;
const SITE_BLOQUEADO = Symbol('site bloqueado por limite de acesso');
const FALHA_PERMANENTE = Symbol('falha permanente da API');
const PAINEIS = {
  'iboplayer.com': { modulo: painelIbo, opcao: 'IBO_PLAYER', navegador: true },
  'iboplayer.pro': { modulo: painelIboPro, opcao: 'IBO_PRO_PLAYER', navegador: true },
  'quickplayer.app': { modulo: painelQuick, opcao: 'QUICK_PLAYER', navegador: true },
  'vuplayer.pro': { modulo: painelVu, opcao: 'VU_PLAYER_PRO', navegador: true },
  'playbr.app.br': { modulo: painelPlayerApp, opcao: 'PLAYER_APP_API', navegador: false },
};

function agruparPorAplicativo(playlists, opcoes) {
  const filas = new Map();
  for (const playlist of playlists) {
    const painel = PAINEIS[playlist.siteAtivacao];
    if (!painel || !opcoes.servidores.includes(playlist.servidor) || !opcoes[painel.opcao]) continue;
    if (!filas.has(playlist.siteAtivacao)) filas.set(playlist.siteAtivacao, []);
    filas.get(playlist.siteAtivacao).push(playlist);
  }
  return filas;
}

async function aguardarIntervalo() {
  if (!INTERVALO_MS) return;
  console.log(`Aguardando ${Math.ceil(INTERVALO_MS / 1000)}s antes da próxima rodada.`);
  await new Promise((resolve) => setTimeout(resolve, INTERVALO_MS));
}

async function aguardarIntervaloApi() {
  console.log('Aguardando 2.1s antes da próxima chamada à API.');
  await new Promise((resolve) => setTimeout(resolve, INTERVALO_API_MS));
}

async function executarRodadas(filas, paginas, processar) {
  const indices = new Map([...filas.keys()].map((site) => [site, 0]));
  while (true) {
    const rodada = [];
    for (const [site, fila] of filas) {
      const indice = indices.get(site);
      if (indice >= fila.length) continue;
      indices.set(site, indice + 1);
      rodada.push(processar(fila[indice], paginas.get(site)).then((bloqueado) => ({ site, bloqueado })));
    }
    if (!rodada.length) return;
    const resultados = await Promise.all(rodada);
    for (const resultado of resultados) {
      if (!resultado.bloqueado) continue;
      const fila = filas.get(resultado.site);
      const indice = indices.get(resultado.site);
      for (const playlist of fila.slice(indice)) {
        await processar(playlist, paginas.get(resultado.site));
      }
      indices.set(resultado.site, fila.length);
    }
    const filasPendentes = [...filas].filter(([site, fila]) => indices.get(site) < fila.length);
    if (filasPendentes.length) {
      const somenteApis = filasPendentes.every(([site]) => !PAINEIS[site].navegador);
      if (somenteApis) await aguardarIntervaloApi();
      else await aguardarIntervalo();
    }
  }
}

async function processarPlaylist(playlist, opcoes, pagina) {
  const painel = PAINEIS[playlist.siteAtivacao];
  if (!painel || !opcoes.servidores.includes(playlist.servidor) || !opcoes[painel.opcao]) return null;
  return painel.navegador
    ? painel.modulo.processarCliente(playlist.macAddress, playlist.deviceKey, playlist.servidor, pagina)
    : painel.modulo.processarCliente(playlist.macAddress, playlist.deviceKey, playlist.servidor);
}

async function executar(playlists, opcoes) {
  const filas = agruparPorAplicativo(playlists, opcoes);
  if (!filas.size) return { sucessos: 0, falhas: [], playlistsBloqueadas: [], sitesBloqueados: [] };

  const sitesComNavegador = [...filas.keys()].filter((site) => PAINEIS[site].navegador);
  const browser = sitesComNavegador.length ? await chromium.launch({ headless: false }) : null;
  try {
    const paginas = new Map();
    await Promise.all(sitesComNavegador.map(async (site) => {
      const contexto = await browser.newContext();
      const pagina = await contexto.newPage();
      await pagina.setViewportSize({ width: 1280, height: 800 });
      paginas.set(site, pagina);
    }));

    const falhas = [];
    const falhasPermanentes = [];
    const playlistsBloqueadas = [];
    const sitesBloqueados = new Map();
    let sucessos = 0;

    async function processarComProtecao(playlist, pagina) {
      const site = playlist.siteAtivacao;
      if (sitesBloqueados.has(site)) {
        playlistsBloqueadas.push(playlist);
        return SITE_BLOQUEADO;
      }
      try {
        return await processarPlaylist(playlist, opcoes, pagina);
      } catch (erro) {
        if (erro?.code === 'PLAYERAPP_PLAYLIST_NOT_FOUND') {
          console.error(`PlayerApp API: dispositivo ou playlist não encontrado para MAC ${playlist.macAddress} (HTTP 404).`);
          falhasPermanentes.push(playlist);
          return FALHA_PERMANENTE;
        }
        if (!isSiteRateLimitError(erro)) throw erro;
        sitesBloqueados.set(site, erro);
        playlistsBloqueadas.push(playlist);
        return SITE_BLOQUEADO;
      }
    }

    await executarRodadas(filas, paginas, async (playlist, pagina) => {
      const sucesso = await processarComProtecao(playlist, pagina);
      if (sucesso === SITE_BLOQUEADO) return true;
      if (sucesso === FALHA_PERMANENTE) return false;
      if (sucesso) sucessos++;
      else falhas.push(playlist);
      return false;
    });

    const falhasSemRetry = falhas.filter((p) => SITES_SEM_RETRY.has(p.siteAtivacao));
    let pendentes = falhas.filter((p) => !SITES_SEM_RETRY.has(p.siteAtivacao));
    for (let tentativa = 1; tentativa <= RETRIES_FALHANTES && pendentes.length; tentativa++) {
      const novasFalhas = [];
      const filasDeRetry = agruparPorAplicativo(pendentes, opcoes);
      const somenteApis = [...filasDeRetry.keys()].every((site) => !PAINEIS[site].navegador);
      console.log(`Reexecutando ${pendentes.length} playlist(s) com falha (${tentativa}/${RETRIES_FALHANTES}).`);
      if (somenteApis) await aguardarIntervaloApi();
      else await aguardarIntervalo();
      await executarRodadas(filasDeRetry, paginas, async (playlist, pagina) => {
        if (sitesBloqueados.has(playlist.siteAtivacao)) {
          await processarComProtecao(playlist, pagina);
          return true;
        }
        const sucesso = await processarComProtecao(playlist, pagina);
        if (sucesso === SITE_BLOQUEADO) return true;
        if (sucesso === FALHA_PERMANENTE) return false;
        if (!sucesso) novasFalhas.push(playlist);
        else sucessos++;
        return false;
      });
      pendentes = novasFalhas;
    }

    return {
      sucessos,
      falhas: [...pendentes, ...falhasSemRetry, ...falhasPermanentes],
      playlistsBloqueadas,
      sitesBloqueados: [...sitesBloqueados.entries()].map(([site, erro]) => ({
        site,
        rayId: erro.rayId,
        retryAfterSeconds: erro.retryAfterSeconds,
      })),
    };
  } finally {
    if (browser) await browser.close();
  }
}

module.exports = { executar, processarPlaylist, agruparPorAplicativo, executarRodadas };
