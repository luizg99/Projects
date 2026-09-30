const { chromium } = require('playwright');
const painelIbo = require('../sites/iboPlayer');
const painelIboPro = require('../sites/iboPlayerPro');
const painelQuick = require('../sites/quickPlayer');
const painelVu = require('../sites/vuPlayer');
const { isSiteRateLimitError } = require('../errors/siteRateLimitError');
const { INTERVALO_MS } = require('../utils/retryDelay');

const SITE_BLOQUEADO = Symbol('site bloqueado por limite de acesso');
const PAINEIS = {
  'iboplayer.com': { modulo: painelIbo, opcao: 'IBO_PLAYER' },
  'iboplayer.pro': { modulo: painelIboPro, opcao: 'IBO_PRO_PLAYER' },
  'quickplayer.app': { modulo: painelQuick, opcao: 'QUICK_PLAYER' },
  'vuplayer.pro': { modulo: painelVu, opcao: 'VU_PLAYER_PRO' },
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
    const haProximaRodada = [...filas].some(([site, fila]) => indices.get(site) < fila.length);
    if (haProximaRodada) await aguardarIntervalo();
  }
}

async function processarPlaylist(playlist, opcoes, pagina) {
  const painel = PAINEIS[playlist.siteAtivacao];
  if (!painel || !opcoes.servidores.includes(playlist.servidor) || !opcoes[painel.opcao]) return null;
  return painel.modulo.processarCliente(playlist.macAddress, playlist.deviceKey, playlist.servidor, pagina);
}

async function executar(playlists, opcoes) {
  const filas = agruparPorAplicativo(playlists, opcoes);
  if (!filas.size) return { sucessos: 0, falhas: [], playlistsBloqueadas: [], sitesBloqueados: [] };

  const browser = await chromium.launch({ headless: false });
  try {
    const paginas = new Map();
    await Promise.all([...filas.keys()].map(async (site) => {
      const contexto = await browser.newContext();
      const pagina = await contexto.newPage();
      await pagina.setViewportSize({ width: 1280, height: 800 });
      paginas.set(site, pagina);
    }));

    const falhas = [];
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
        if (!isSiteRateLimitError(erro)) throw erro;
        sitesBloqueados.set(site, erro);
        playlistsBloqueadas.push(playlist);
        return SITE_BLOQUEADO;
      }
    }

    await executarRodadas(filas, paginas, async (playlist, pagina) => {
      const sucesso = await processarComProtecao(playlist, pagina);
      if (sucesso === SITE_BLOQUEADO) return true;
      if (sucesso) sucessos++;
      else falhas.push(playlist);
      return false;
    });

    let pendentes = falhas;
    for (let tentativa = 1; tentativa < 3 && pendentes.length; tentativa++) {
      const novasFalhas = [];
      await aguardarIntervalo();
      const filasDeRetry = agruparPorAplicativo(pendentes, opcoes);
      await executarRodadas(filasDeRetry, paginas, async (playlist, pagina) => {
        if (sitesBloqueados.has(playlist.siteAtivacao)) {
          await processarComProtecao(playlist, pagina);
          return true;
        }
        if (playlist.siteAtivacao === 'iboplayer.com') {
          novasFalhas.push(playlist);
          return false;
        }
        const sucesso = await processarComProtecao(playlist, pagina);
        if (sucesso === SITE_BLOQUEADO) return true;
        if (!sucesso) novasFalhas.push(playlist);
        else sucessos++;
        return false;
      });
      pendentes = novasFalhas;
    }

    return {
      sucessos,
      falhas: pendentes,
      playlistsBloqueadas,
      sitesBloqueados: [...sitesBloqueados.entries()].map(([site, erro]) => ({
        site,
        rayId: erro.rayId,
        retryAfterSeconds: erro.retryAfterSeconds,
      })),
    };
  } finally {
    await browser.close();
  }
}

module.exports = { executar, processarPlaylist, agruparPorAplicativo, executarRodadas };
