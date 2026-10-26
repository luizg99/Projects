const { test, expect } = require('@playwright/test');
const nock = require('nock');
const biblioteca = require('../src/biblioteca');
const playlistRepository = require('../src/repositories/lembretinhoPlaylistRepository');
const playlistModel = require('../src/models/playlist');

test.beforeEach(() => {
  delete require.cache[require.resolve('../src/biblioteca')];
  delete require.cache[require.resolve('../src/models/playlist')];
  delete require.cache[require.resolve('../src/repositories/lembretinhoPlaylistRepository')];
  nock.cleanAll();
});

test.afterEach(() => nock.cleanAll());

test('repositório consulta todas as páginas e preserva cursor e IDs textuais', async () => {
  const cursor = 'cursor+opaco/==';
  const primeira = nock('https://lembretinho.test', { reqheaders: { 'x-api-key': 'teste' } })
    .get('/api/integracoes/v1/playlists').query({ limite: '500' })
    .reply(200, { dados: [{ clienteId: '9007199254740993' }], proximoCursor: cursor });
  const segunda = nock('https://lembretinho.test', { reqheaders: { 'x-api-key': 'teste' } })
    .get('/api/integracoes/v1/playlists').query({ limite: '500', cursor })
    .reply(200, { dados: [{ clienteId: '9007199254740994' }], proximoCursor: null });

  const registros = await playlistRepository.listarTodas({
    apiKey: 'teste', apiUrl: 'https://lembretinho.test/api/integracoes/v1/playlists',
  });

  expect(registros.map((item) => item.clienteId)).toEqual(['9007199254740993', '9007199254740994']);
  expect(primeira.isDone()).toBe(true);
  expect(segunda.isDone()).toBe(true);
});

test('modelo seleciona uma amostra elegível por servidor e aplicativo', () => {
  const base = {
    ativo: 'S', atualizar: 'S', macAddress: 'MAC', deviceKey: 'KEY',
    aplicativo: { aplicativoId: '41', urlSite: 'https://iboplayer.com' },
  };
  const amostras = playlistModel.selecionarAmostrasPorServidorAplicativo([
    { ...base, servidorId: '9007199254740993', nomeServidor: 'S1' },
    { ...base, servidorId: '9007199254740993', nomeServidor: 'S1', macAddress: 'OUTRO' },
    { ...base, servidorId: '9007199254740993', nomeServidor: 'S1', aplicativo: { aplicativoId: '42', urlSite: 'https://vuplayer.pro' } },
    { ...base, servidorId: '9007199254740994', nomeServidor: 'S2' },
    { ...base, servidorId: '9007199254740994', nomeServidor: 'S2', ativo: 'N' },
  ]);

  expect(amostras.map(({ servidorId, aplicativoId }) => `${servidorId}:${aplicativoId}`)).toEqual([
    '9007199254740993:41', '9007199254740993:42', '9007199254740994:41',
  ]);
});

test('biblioteca usa o link retornado para cada servidor', () => {
  biblioteca.atualizarLinksDaApi([{
    nomeServidor: 'Servidor A', urlAtual: 'https://host.test', m3u: 'https://m3u.test/get.php?',
  }]);
  expect(biblioteca.obterLinkAtualizado('Servidor A')).toBe('https://host.test');
  expect(biblioteca.obterLinkM3UAtualizado('Servidor A')).toBe('https://m3u.test/get.php?');
  expect(biblioteca.obterLinkAtualizado('inexistente')).toBe('');
  expect(biblioteca.obterLinkM3UAtualizado('inexistente')).toBe('');
});

test('biblioteca troca a base M3U e preserva a query completa', () => {
  expect(biblioteca.substituirBaseM3U(
    'http://antigo.test/get.php?username=cliente&password=segredo&type=m3u&output=ts',
    'http://novo.test/get.php?'
  )).toBe('http://novo.test/get.php?username=cliente&password=segredo&type=m3u&output=ts');
  expect(biblioteca.substituirBaseM3U('http://antigo.test/playlist.m3u', 'http://novo.test/get.php?')).toBe('');
});

test('biblioteca resolve CAPTCHA usando a chave fornecida', async () => {
  biblioteca.captchaApiKey = 'teste';
  nock('https://2captcha.com').post('/in.php').reply(200, { status: 1, request: 'captcha-id' });
  nock('https://2captcha.com').get('/res.php')
    .query({ key: 'teste', action: 'get', id: 'captcha-id', json: '1' })
    .reply(200, { status: 1, request: 'RESOLVIDO' });

  await expect(biblioteca.solveCaptchaWith2Captcha(Buffer.from('imagem'))).resolves.toBe('RESOLVIDO');
});
