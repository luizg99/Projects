const { test, expect } = require('@playwright/test');
const nock = require('nock');

// Recarrega o módulo a cada describe para garantir estado limpo
let lib;

const LINKS_CSV = `Nome painel,Link
link_tvs,http://tvs.test.com
link_uniplay,http://uniplay.test.com
link_bit,http://bit.test.com
link_fast,http://fast.test.com`;

const CLIENTES_CSV = `MAC Address,Device Key,Servidor,Site ativação,data validade APP,Ativo,Atualizar
d6:f5:72:a3:0a:55,KEYABC,TVS,iboplayer.com,2026-12-31,S,S
66:77:88:99:AA:BB,KEYDEF,UNIPLAY,iboplayer.pro,2026-06-01,S,N`;

const SHEET_ID_LINKS = '1lMIq91MwJcxuNDZsJaTLdYmj_M2bmLO5_R9Y5EWDEb8';
const SHEET_ID_CLIENTES = '1ifSYQKY2W-DA0D0wYY00tKag90Tp5FJP3mdZ0lConUs';

test.beforeEach(() => {
  // Limpa cache do módulo para reiniciar estado global entre testes
  delete require.cache[require.resolve('../src/biblioteca')];
  lib = require('../src/biblioteca');
  nock.cleanAll();
});

test.afterEach(() => {
  nock.cleanAll();
});

// ─── getGoogleSheetData ───────────────────────────────────────────────────────

test('getGoogleSheetData - retorna array de objetos do CSV', async () => {
  nock('https://docs.google.com')
    .get(`/spreadsheets/d/${SHEET_ID_CLIENTES}/export`)
    .query({ format: 'csv' })
    .reply(200, CLIENTES_CSV);

  const url = `https://docs.google.com/spreadsheets/d/${SHEET_ID_CLIENTES}/export?format=csv`;
  const data = await lib.getGoogleSheetData(url);

  expect(data).toHaveLength(2);
  expect(data[0]['MAC Address']).toBe('00:11:22:33:44:55');
  expect(data[0]['Device Key']).toBe('KEYABC');
  expect(data[0]['Servidor']).toBe('TVS');
  expect(data[1]['Atualizar']).toBe('N');
});

test('getGoogleSheetData - lança erro em falha HTTP', async () => {
  nock('https://docs.google.com')
    .get(`/spreadsheets/d/${SHEET_ID_CLIENTES}/export`)
    .query({ format: 'csv' })
    .reply(500, 'Erro interno');

  const url = `https://docs.google.com/spreadsheets/d/${SHEET_ID_CLIENTES}/export?format=csv`;
  await expect(lib.getGoogleSheetData(url)).rejects.toThrow();
});

// ─── atualizarLinks ───────────────────────────────────────────────────────────

test('atualizarLinks - preenche variáveis globais com links da planilha', async () => {
  nock('https://docs.google.com')
    .get(`/spreadsheets/d/${SHEET_ID_LINKS}/export`)
    .query({ format: 'csv' })
    .reply(200, LINKS_CSV);

  await lib.atualizarLinks();

  expect(lib.obterLinkAtualizado('TVS')).toBe('http://tvs.test.com');
  expect(lib.obterLinkAtualizado('UNIPLAY')).toBe('http://uniplay.test.com');
  expect(lib.obterLinkAtualizado('BIT')).toBe('http://bit.test.com');
  expect(lib.obterLinkAtualizado('FAST')).toBe('http://fast.test.com');
});

test('atualizarLinks - ignora linhas sem nome de painel conhecido', async () => {
  const csvComLinhaNaoReconhecida = `Nome painel,Link
link_tvs,http://tvs.test.com
desconhecido,http://outro.com`;

  nock('https://docs.google.com')
    .get(`/spreadsheets/d/${SHEET_ID_LINKS}/export`)
    .query({ format: 'csv' })
    .reply(200, csvComLinhaNaoReconhecida);

  await lib.atualizarLinks();

  expect(lib.obterLinkAtualizado('TVS')).toBe('http://tvs.test.com');
  expect(lib.obterLinkAtualizado('UNIPLAY')).toBe('');  // não foi setado
});

// ─── obterLinkAtualizado ──────────────────────────────────────────────────────

test('obterLinkAtualizado - retorna string vazia antes de atualizarLinks', () => {
  expect(lib.obterLinkAtualizado('TVS')).toBe('');
  expect(lib.obterLinkAtualizado('UNIPLAY')).toBe('');
  expect(lib.obterLinkAtualizado('BIT')).toBe('');
  expect(lib.obterLinkAtualizado('FAST')).toBe('');
});

test('obterLinkAtualizado - retorna string vazia para servidor desconhecido', async () => {
  nock('https://docs.google.com')
    .get(`/spreadsheets/d/${SHEET_ID_LINKS}/export`)
    .query({ format: 'csv' })
    .reply(200, LINKS_CSV);

  await lib.atualizarLinks();

  expect(lib.obterLinkAtualizado('DESCONHECIDO')).toBe('');
  expect(lib.obterLinkAtualizado('')).toBe('');
});

// ─── solveCaptchaWith2Captcha ─────────────────────────────────────────────────

test('solveCaptchaWith2Captcha - resolve e retorna texto do CAPTCHA', async () => {
  lib.captchaApiKey = 'test-api-key';
  const fakeBuffer = Buffer.from('fake-image-data');

  // Mock do envio da imagem
  nock('http://2captcha.com')
    .post('/in.php')
    .reply(200, { status: 1, request: 'captcha-id-999' });

  // Mock da consulta do resultado (resolve na primeira chamada)
  nock('http://2captcha.com')
    .get('/res.php')
    .query({ key: 'test-api-key', action: 'get', id: 'captcha-id-999', json: '1' })
    .reply(200, { status: 1, request: 'TEXTO_RESOLVIDO' });

  const resultado = await lib.solveCaptchaWith2Captcha(fakeBuffer, 'test-api-key');

  expect(resultado).toBe('TEXTO_RESOLVIDO');
});

test('solveCaptchaWith2Captcha - lança erro se 2Captcha recusar envio', async () => {
  const fakeBuffer = Buffer.from('fake-image-data');

  nock('http://2captcha.com')
    .post('/in.php')
    .reply(200, { status: 0, request: 'ERROR_WRONG_USER_KEY' });

  await expect(
    lib.solveCaptchaWith2Captcha(fakeBuffer, 'chave-invalida')
  ).rejects.toThrow('Erro ao enviar CAPTCHA');
});

test('solveCaptchaWith2Captcha - lança erro em resposta inesperada do polling', async () => {
  const fakeBuffer = Buffer.from('fake-image-data');

  nock('http://2captcha.com')
    .post('/in.php')
    .reply(200, { status: 1, request: 'captcha-id-888' });

  nock('http://2captcha.com')
    .get('/res.php')
    .query({ key: 'test-api-key', action: 'get', id: 'captcha-id-888', json: '1' })
    .reply(200, { status: 0, request: 'ERROR_CAPTCHA_UNSOLVABLE' });

  await expect(
    lib.solveCaptchaWith2Captcha(fakeBuffer, 'test-api-key')
  ).rejects.toThrow('Erro ao resolver CAPTCHA');
});
