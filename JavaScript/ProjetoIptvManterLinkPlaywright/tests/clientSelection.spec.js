const { test, expect } = require('@playwright/test');
const { normalizarPlaylist } = require('../src/models/playlist');
const { agruparPorAplicativo } = require('../src/services/automacaoService');
const { listarClientes, mostrarInterface } = require('../src/views/interfaceView');
const ExclusiveCheckboxPrompt = require('../src/views/exclusiveCheckboxPrompt');

test('marcar o outro modo desmarca o anterior', () => {
  const escolhas = [{ checked: true }, { checked: false }];
  const choices = {
    getChoice: (index) => escolhas[index],
    forEach: (callback) => escolhas.forEach(callback),
  };
  ExclusiveCheckboxPrompt.prototype.toggleChoice.call({ opt: { choices } }, 1);
  expect(escolhas.map((escolha) => escolha.checked)).toEqual([false, true]);
});

test('lista clientes por nome e mantém IDs distintos', () => {
  const playlists = [
    { clienteId: '2', clienteNome: 'Zélia' },
    { clienteId: '1', clienteNome: 'Álvaro' },
    { clienteId: '3', clienteNome: 'Álvaro' },
    { clienteId: '1', clienteNome: 'Álvaro' },
    ...['cli289', 'cli290', 'cli291', 'cli292', 'Cli293', 'cli86', 'cli96']
      .map((clienteNome, index) => ({ clienteId: `cli-${index}`, clienteNome })),
  ];
  expect(listarClientes(playlists)).toEqual([
    { id: '1', nome: 'Álvaro' },
    { id: '3', nome: 'Álvaro' },
    { id: 'cli-5', nome: 'cli86' },
    { id: 'cli-6', nome: 'cli96' },
    { id: 'cli-0', nome: 'cli289' },
    { id: 'cli-1', nome: 'cli290' },
    { id: 'cli-2', nome: 'cli291' },
    { id: 'cli-3', nome: 'cli292' },
    { id: 'cli-4', nome: 'Cli293' },
    { id: '2', nome: 'Zélia' },
  ]);
  expect(normalizarPlaylist({ clienteId: '1', clienteNome: 'Álvaro', servidorId: '4' }).clienteNome)
    .toBe('Álvaro');
});

test('modo por cliente seleciona vários nomes por busca, sem pedir servidores ou aplicativos', async () => {
  const perguntas = [];
  const respostas = [
    { modos: ['cliente'] },
    { busca: 'alvaro' },
    { numeros: '1' },
    { buscarMais: true },
    { busca: 'zé' },
    { numeros: '1' },
    { buscarMais: false },
  ];
  const linhas = [];
  const prompt = async (questoes) => {
    perguntas.push(questoes);
    return respostas.shift();
  };
  const opcoes = await mostrarInterface(['TVS'], [
    { clienteId: '2', clienteNome: 'Zélia' },
    { clienteId: '1', clienteNome: 'Álvaro' },
  ], prompt, (linha) => linhas.push(linha));

  expect(opcoes).toEqual({ modo: 'cliente', clientes: ['1', '2'], selecionado: true });
  expect(perguntas[0][0].type).toBe('exclusive-checkbox');
  expect(perguntas[0][0].hint).toMatch(/Espaço para escolher/);
  expect(perguntas[0][0].choices[0].checked).toBe(true);
  expect(perguntas[2][0].type).toBe('input');
  expect(perguntas[2][0].validate('2')).toMatch(/Use números/);
  expect(linhas.filter((linha) => linha.includes('Álvaro'))).toHaveLength(1);
  expect(linhas.filter((linha) => linha.includes('Zélia'))).toHaveLength(1);
  expect(perguntas.every((grupo) => grupo.every((questao) => !['servidores', 'aplicativos'].includes(questao.name)))).toBe(true);
});

test('aceita vários clientes na mesma lista sem redesenhar o menu', async () => {
  const respostas = [{ modos: ['cliente'] }, { busca: '' }, { numeros: '1, 2' }, { buscarMais: false }];
  const linhas = [];
  const prompt = async () => respostas.shift();

  const opcoes = await mostrarInterface([], [
    { clienteId: '2', clienteNome: 'Bruna' },
    { clienteId: '1', clienteNome: 'Amigo' },
  ], prompt, (linha) => linhas.push(linha));

  expect(opcoes.clientes).toEqual(['1', '2']);
  expect(linhas).toEqual(['Clientes encontrados: 2', '  1. [ ] Amigo', '  2. [ ] Bruna']);
});

test('modo tradicional mantém seleção de servidores e aplicativos', async () => {
  const perguntas = [];
  const respostas = [
    { modos: ['tradicional'] },
    { servidores: ['TVS'], aplicativos: ['IBO_PLAYER', 'PLAYER_APP_API'] },
  ];
  const prompt = async (questoes) => {
    perguntas.push(questoes);
    return respostas.shift();
  };

  const opcoes = await mostrarInterface(['TVS', 'UNIPLAY'], [], prompt);

  expect(opcoes).toMatchObject({
    modo: 'tradicional', servidores: ['TVS'], IBO_PLAYER: true,
    PLAYER_APP_API: true, QUICK_PLAYER: false, selecionado: true,
  });
  expect(perguntas[1].map((questao) => questao.name)).toEqual(['servidores', 'aplicativos']);
  expect(perguntas[1][0].choices.map((choice) => choice.value)).toEqual(['TVS', 'UNIPLAY']);
});

test('filtra pelo ID do cliente e inclui os aplicativos dele em servidores diferentes', () => {
  const playlists = [
    { clienteId: '1', servidor: 'TVS', siteAtivacao: 'iboplayer.pro', macAddress: 'A' },
    { clienteId: '1', servidor: 'UNIPLAY', siteAtivacao: 'quickplayer.app', macAddress: 'B' },
    { clienteId: '2', servidor: 'TVS', siteAtivacao: 'iboplayer.pro', macAddress: 'C' },
  ];
  const porCliente = agruparPorAplicativo(playlists, { modo: 'cliente', clientes: ['1'] });
  expect([...porCliente.values()].flat().map((playlist) => playlist.macAddress)).toEqual(['A', 'B']);

  const tradicional = agruparPorAplicativo(playlists, {
    modo: 'tradicional', servidores: ['TVS'], IBO_PRO_PLAYER: true, QUICK_PLAYER: false,
  });
  expect([...tradicional.values()].flat().map((playlist) => playlist.macAddress)).toEqual(['A', 'C']);
});
