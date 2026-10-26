const { test, expect } = require('@playwright/test');
const { criarProgresso, executarRodadas, executar } = require('../src/services/automacaoService');
const painelPlayerApp = require('../src/sites/playerApp');

test('mantém os totais originais e os MACs pendentes entre sessões', () => {
  const v1 = { siteAtivacao: 'vuplayer.pro' };
  const v2 = { siteAtivacao: 'vuplayer.pro' };
  const i1 = { siteAtivacao: 'iboplayer.com' };
  const mensagens = [];
  const progresso = criarProgresso(new Map([
    ['vuplayer.pro', [v1, v2]],
    ['iboplayer.com', [i1]],
  ]), (mensagem) => mensagens.push(mensagem));

  progresso.iniciarSessao();
  progresso.registrar(v1, false);
  progresso.registrar(i1, true);
  progresso.iniciarSessao();
  progresso.registrar(v1, true);
  progresso.registrar(v2, true);

  expect(mensagens.filter((mensagem) => mensagem === 'Nova Sessão iniciada')).toHaveLength(2);
  expect(mensagens.filter((mensagem) => /^Falta(m)? /.test(mensagem))).toEqual([
    'Faltam 3 MACs no total',
    'Faltam 2 MACs no total',
    'Falta 1 MAC no total',
    'Faltam 0 MACs no total',
  ]);
  expect(mensagens.filter((mensagem) => mensagem.includes('vuplayer.pro:'))).toEqual([
    '  - vuplayer.pro: 2 ---- falta 2',
    '  - vuplayer.pro: 2 ---- falta 2',
    '  - vuplayer.pro: 2 ---- falta 1',
    '  - vuplayer.pro: 2 ---- falta 0',
  ]);
  expect(mensagens).toContain('  - iboplayer.com: 1 ---- falta 0');
});

test('contabiliza todos os MACs quando um painel bloqueia a fila', async () => {
  const v1 = { siteAtivacao: 'vuplayer.pro' };
  const v2 = { siteAtivacao: 'vuplayer.pro' };
  const filas = new Map([['vuplayer.pro', [v1, v2]]]);
  const mensagens = [];
  const progresso = criarProgresso(filas, (mensagem) => mensagens.push(mensagem));

  progresso.iniciarSessao();
  await executarRodadas(filas, new Map(), async (playlist) => {
    progresso.registrar(playlist, true);
    return playlist === v1;
  });

  expect(mensagens.filter((mensagem) => /^Falta(m)? /.test(mensagem))).toEqual([
    'Falta 1 MAC no total',
    'Faltam 0 MACs no total',
  ]);
});

test('execução informa sessão e restante após processar um MAC', async () => {
  const mensagens = [];
  const processarOriginal = painelPlayerApp.processarCliente;
  const logOriginal = console.log;
  painelPlayerApp.processarCliente = async () => true;
  console.log = (mensagem) => mensagens.push(mensagem);

  try {
    const resultado = await executar([{
      siteAtivacao: 'playbr.app.br', servidor: 'TVS', macAddress: 'MAC', deviceKey: 'KEY',
    }], { servidores: ['TVS'], PLAYER_APP_API: true });
    expect(resultado.sucessos).toBe(1);
  } finally {
    painelPlayerApp.processarCliente = processarOriginal;
    console.log = logOriginal;
  }

  expect(mensagens).toContain('Nova Sessão iniciada');
  expect(mensagens).toContain('Faltam 0 MACs no total');
  expect(mensagens).toContain('  - playbr.app.br: 1 ---- falta 0');
});
