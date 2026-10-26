const inquirer = require('inquirer');
const ExclusiveCheckboxPrompt = require('./exclusiveCheckboxPrompt');

inquirer.registerPrompt('exclusive-checkbox', ExclusiveCheckboxPrompt);

function textoParaBusca(valor) {
  return String(valor).normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR');
}

function listarClientes(playlists) {
  const clientes = new Map();
  for (const playlist of playlists) {
    const id = playlist.clienteId == null ? '' : String(playlist.clienteId);
    if (!id) continue;
    const nomeInformado = String(playlist.clienteNome || '').trim();
    if (!clientes.has(id) || (clientes.get(id).semNome && nomeInformado)) {
      clientes.set(id, { id, nome: nomeInformado || `Cliente ${id}`, semNome: !nomeInformado });
    }
  }
  return [...clientes.values()]
    .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR', { sensitivity: 'base', numeric: true }) || a.id.localeCompare(b.id))
    .map(({ id, nome }) => ({ id, nome }));
}

function interpretarNumeros(valor, quantidade) {
  const partes = String(valor).trim().split(/[\s,;]+/).filter(Boolean);
  if (partes.some((parte) => !/^\d+$/.test(parte) || Number(parte) < 1 || Number(parte) > quantidade)) {
    return null;
  }
  return [...new Set(partes.map(Number))];
}

async function selecionarClientes(clientes, prompt, escrever) {
  const selecionados = new Set();
  const nomesRepetidos = new Map();
  for (const cliente of clientes) {
    const nome = textoParaBusca(cliente.nome);
    nomesRepetidos.set(nome, (nomesRepetidos.get(nome) || 0) + 1);
  }

  while (true) {
    const { busca } = await prompt([{
      type: 'input',
      name: 'busca',
      message: 'Pesquisar cliente pelo nome ou ID (Enter para listar todos):',
    }]);
    const filtro = textoParaBusca(busca.trim());
    const encontrados = clientes.filter((cliente) =>
      textoParaBusca(cliente.nome).includes(filtro) || cliente.id.includes(filtro));
    if (!encontrados.length) {
      escrever('Nenhum cliente encontrado. Tente outra busca.');
      continue;
    }

    escrever(`Clientes encontrados: ${encontrados.length}`);
    encontrados.forEach((cliente, indice) => {
      const nome = nomesRepetidos.get(textoParaBusca(cliente.nome)) > 1
        ? `${cliente.nome} (ID ${cliente.id})` : cliente.nome;
      escrever(`${String(indice + 1).padStart(3)}. ${selecionados.has(cliente.id) ? '[x]' : '[ ]'} ${nome}`);
    });

    const { numeros } = await prompt([{
      type: 'input',
      name: 'numeros',
      message: 'Digite os números para marcar ou desmarcar (ex.: 1, 3, 5):',
      validate: (valor) => {
        const indices = interpretarNumeros(valor, encontrados.length);
        if (indices === null) return `Use números de 1 a ${encontrados.length}, separados por vírgula.`;
        return indices.length > 0 || selecionados.size > 0 || 'Selecione ao menos um cliente!';
      },
    }]);
    const indices = interpretarNumeros(numeros, encontrados.length);
    if (!indices.length) return [...selecionados];
    for (const indice of indices) {
      const id = encontrados[indice - 1].id;
      if (selecionados.has(id)) selecionados.delete(id);
      else selecionados.add(id);
    }

    const { buscarMais } = await prompt([{
      type: 'confirm',
      name: 'buscarMais',
      message: `${selecionados.size} cliente(s) marcado(s). Buscar mais ou ajustar a seleção?`,
      default: false,
    }]);
    if (!buscarMais && selecionados.size) return [...selecionados];
    if (!selecionados.size) escrever('Selecione ao menos um cliente para continuar.');
  }
}

async function mostrarInterface(nomesServidores = [], playlists = [], prompt = inquirer.prompt, escrever = console.log) {
  const servidores = [...new Set(nomesServidores.filter(Boolean))];
  const { modos } = await prompt([{
    type: 'exclusive-checkbox',
    name: 'modos',
    message: 'Selecione os servidores a atualizar:',
    hint: 'Use ↑/↓ para navegar, Espaço para escolher um modo e Enter para continuar.',
    choices: [
      { name: 'Tradicional', value: 'tradicional', checked: true },
      { name: 'Por Cliente', value: 'cliente' },
    ],
    validate: (selecionados) => selecionados.length === 1 || 'Selecione um modo!',
  }]);
  const modo = modos[0];

  if (modo === 'cliente') {
    const clientes = listarClientes(playlists);
    if (!clientes.length) {
      console.log('Nenhum cliente com playlist elegível para atualização.');
      return { modo, clientes: [], selecionado: false };
    }
    const selecionados = await selecionarClientes(clientes, prompt, escrever);
    return { modo, clientes: selecionados, selecionado: selecionados.length > 0 };
  }

  const respostas = await prompt([
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
    modo,
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

function mostrarPlaylistsSelecionadas(filas) {
  const total = [...filas.values()].reduce((soma, fila) => soma + fila.length, 0);
  console.log(`Playlists a processar conforme a seleção: ${total}`);
  for (const [site, fila] of filas) console.log(`  - ${site}: ${fila.length}`);
}

function mostrarResultado({ sucessos, falhas, playlistsBloqueadas = [], sitesBloqueados = [] }) {
  console.log(`Processamento concluído. Sucessos: ${sucessos}; falhas: ${falhas.length}.`);
  const falhasPorGrupo = new Map();
  for (const playlist of falhas) {
    const grupo = `${playlist.siteAtivacao} (servidor ${playlist.servidor})`;
    if (!falhasPorGrupo.has(grupo)) falhasPorGrupo.set(grupo, []);
    falhasPorGrupo.get(grupo).push(playlist.macAddress);
  }
  for (const [grupo, macs] of falhasPorGrupo) {
    console.error(`Falhas em ${grupo}: ${macs.length}`);
    for (const mac of macs) console.error(`  - MAC ${mac}`);
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

module.exports = { mostrarInterface, listarClientes, mostrarQuantidadePlaylists, mostrarPlaylistsSelecionadas, mostrarResultado, mostrarErroFatal };
