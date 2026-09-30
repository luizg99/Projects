const playlistRepository = require('../repositories/lembretinhoPlaylistRepository');
const playlistModel = require('../models/playlist');
const lib = require('../biblioteca');

async function listar() {
  const registros = await playlistRepository.listarTodas();
  lib.atualizarLinksDaApi(registros);
  return registros.map(playlistModel.normalizarPlaylist);
}

function listarElegiveis(playlists) {
  return playlists.filter(playlistModel.playlistElegivel);
}

function selecionarAmostras(playlists) {
  return playlistModel.selecionarAmostrasPorServidorAplicativo(playlists);
}

module.exports = { listar, listarElegiveis, selecionarAmostras };
