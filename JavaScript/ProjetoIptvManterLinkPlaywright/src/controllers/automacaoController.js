const interfaceView = require('../views/interfaceView');
const playlistService = require('../services/playlistService');
const automacaoService = require('../services/automacaoService');

async function executar() {
  const playlists = await playlistService.listar();
  interfaceView.mostrarQuantidadePlaylists(playlists.length);
  const servidores = [...new Set(playlists.map((playlist) => playlist.servidor).filter(Boolean))];
  const opcoes = await interfaceView.mostrarInterface(servidores);
  if (!opcoes.selecionado) return;

  const elegiveis = playlistService.listarElegiveis(playlists);
  const resultado = await automacaoService.executar(elegiveis, opcoes);
  interfaceView.mostrarResultado(resultado);
}

module.exports = { executar };
