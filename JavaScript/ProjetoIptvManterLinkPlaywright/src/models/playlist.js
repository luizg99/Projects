const { obterSiteAtivacao } = require('../biblioteca');

function normalizarPlaylist(registro) {
  if (registro.siteAtivacao !== undefined) return {
    ...registro,
    clienteId: String(registro.clienteId),
    servidorId: String(registro.servidorId),
    clienteNome: registro.clienteNome || '',
  };
  return {
    clienteId: String(registro.clienteId),
    clienteNome: registro.clienteNome || '',
    servidorId: String(registro.servidorId),
    servidor: registro.nomeServidor || '',
    macAddress: registro.macAddress || '',
    deviceKey: registro.deviceKey || '',
    aplicativoId: registro.aplicativo?.aplicativoId == null ? '' : String(registro.aplicativo.aplicativoId),
    siteAtivacao: obterSiteAtivacao(registro.aplicativo?.urlSite),
    ativo: registro.ativo,
    atualizar: registro.atualizar,
    urlAtual: registro.urlAtual || '',
    m3u: registro.m3u || '',
  };
}

function playlistElegivel(playlist) {
  return playlist.ativo === 'S'
    && playlist.atualizar === 'S'
    && Boolean(playlist.macAddress && playlist.deviceKey && playlist.servidor && playlist.siteAtivacao);
}

function selecionarAmostrasPorServidorAplicativo(playlists) {
  const selecionadas = new Map();
  for (const playlist of playlists.map(normalizarPlaylist)) {
    if (!playlistElegivel(playlist)) continue;
    const chave = `${playlist.servidorId}:${playlist.aplicativoId || playlist.siteAtivacao}`;
    if (!selecionadas.has(chave)) selecionadas.set(chave, playlist);
  }
  return [...selecionadas.values()];
}

module.exports = { normalizarPlaylist, playlistElegivel, selecionarAmostrasPorServidorAplicativo };
