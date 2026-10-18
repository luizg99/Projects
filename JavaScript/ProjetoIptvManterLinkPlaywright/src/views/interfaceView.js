const inquirer = require('inquirer');

async function mostrarInterface(nomesServidores = []) {
  const servidores = [...new Set(nomesServidores.filter(Boolean))];
  const respostas = await inquirer.prompt([
    {
      type: 'checkbox',
      name: 'servidores',
      message: 'Selecione os servidores a atualizar:',
      choices: servidores.map((servidor) => ({ name: servidor, value: servidor, checked: true })),
      validate: (escolhas) => escolhas.length > 0 || 'Selecione ao menos um servidor!',
    },
    {
      type: 'checkbox',
      name: 'aplicativos',
      message: 'Selecione os aplicativos a atualizar:',
      choices: [
        { name: 'IBO_PLAYER (iboplayer.com)', value: 'IBO_PLAYER', checked: true },
        { name: 'IBO_PRO_PLAYER (iboplayer.pro)', value: 'IBO_PRO_PLAYER', checked: true },
        { name: 'QUICK_PLAYER (quickplayer.app)', value: 'QUICK_PLAYER', checked: true },
        { name: 'VU_PLAYER_PRO (vuplayer.pro)', value: 'VU_PLAYER_PRO', checked: true },
        { name: 'PLAYER APP (PlayBR API)', value: 'PLAYER_APP_API', checked: true },
      ],
      validate: (escolhas) => escolhas.length > 0 || 'Selecione ao menos um aplicativo!',
    },
  ]);

  return {
    servidores: respostas.servidores,
    IBO_PLAYER: respostas.aplicativos.includes('IBO_PLAYER'),
    IBO_PRO_PLAYER: respostas.aplicativos.includes('IBO_PRO_PLAYER'),
    QUICK_PLAYER: respostas.aplicativos.includes('QUICK_PLAYER'),
    VU_PLAYER_PRO: respostas.aplicativos.includes('VU_PLAYER_PRO'),
    PLAYER_APP_API: respostas.aplicativos.includes('PLAYER_APP_API'),
    selecionado: respostas.servidores.length > 0,
  };
}

function mostrarQuantidadePlaylists(quantidade) {
  console.log(`Playlists recebidas: ${quantidade}`);
}

function mostrarResultado({ sucessos, falhas, playlistsBloqueadas = [], sitesBloqueados = [] }) {
  console.log(`Processamento concluído. Sucessos: ${sucessos}; falhas: ${falhas.length}.`);
  for (const playlist of falhas) {
    console.error(`Falha no servidor ${playlist.servidor}, aplicativo ${playlist.siteAtivacao}, MAC ${playlist.macAddress}.`);
  }
  for (const bloqueio of sitesBloqueados) {
    const espera = bloqueio.retryAfterSeconds == null
      ? 'aguarde antes de tentar novamente'
      : `aguarde pelo menos ${bloqueio.retryAfterSeconds}s antes de tentar novamente`;
    const rayId = bloqueio.rayId ? ` Ray ID: ${bloqueio.rayId}.` : '';
    console.error(`Limite de acesso detectado em ${bloqueio.site}; painel pausado nesta execução, ${espera}.${rayId}`);
  }
  if (playlistsBloqueadas.length) {
    console.error(`Playlists não processadas por bloqueio do painel: ${playlistsBloqueadas.length}.`);
  }
}

function mostrarErroFatal(erro) {
  console.error('Erro fatal na automação:', erro.message);
}

module.exports = { mostrarInterface, mostrarQuantidadePlaylists, mostrarResultado, mostrarErroFatal };
