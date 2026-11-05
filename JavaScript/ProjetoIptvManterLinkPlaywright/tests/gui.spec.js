const { test, expect } = require('@playwright/test');
const { criarInterface, iniciarServidor } = require('../src/gui/server');

const playlists = [
  { clienteId: '1', clienteNome: 'Ana', servidor: 'Servidor A', siteAtivacao: 'playbr.app.br', macAddress: 'AA', ativo: 'S', atualizar: 'S' },
  { clienteId: '2', clienteNome: 'Bruno', servidor: 'Servidor B', siteAtivacao: 'quickplayer.app', macAddress: 'BB', ativo: 'S', atualizar: 'S' },
];

test('interface local carrega dados, valida seleção e mostra resultado', async ({ request }) => {
  const chamadas = [];
  const interfaceApp = criarInterface({
    listar: async () => playlists,
    listarElegiveis: (items) => items,
    agrupar: (items, opcoes) => {
      const selecionadas = items.filter((p) => opcoes.modo === 'cliente'
        ? opcoes.clientes.includes(p.clienteId)
        : opcoes.servidores.includes(p.servidor) && opcoes[p.siteAtivacao === 'playbr.app.br' ? 'PLAYER_APP_API' : 'QUICK_PLAYER']);
      return new Map(selecionadas.map((p) => [p.siteAtivacao, [p]]));
    },
    executar: async (items, opcoes) => {
      chamadas.push(opcoes);
      await new Promise((resolve) => setTimeout(resolve, 100));
      return { sucessos: 1, falhas: [], playlistsBloqueadas: [], sitesBloqueados: [] };
    },
  });
  const { servidor, url } = await iniciarServidor({ interfaceApp });
  try {
    const headers = { Origin: url, 'Content-Type': 'application/json' };
    const dados = await request.post(`${url}/api/carregar`, { headers, data: {} });
    expect(dados.ok()).toBeTruthy();
    expect((await dados.json()).clientes).toEqual([{ id: '1', nome: 'Ana' }, { id: '2', nome: 'Bruno' }]);

    const invalida = await request.post(`${url}/api/iniciar`, { headers, data: { modo: 'cliente', clientes: ['999'] } });
    expect(invalida.status()).toBe(400);
    const inicio = await request.post(`${url}/api/iniciar`, { headers, data: { modo: 'cliente', clientes: ['1'] } });
    expect(inicio.ok()).toBeTruthy();
    const duplicada = await request.post(`${url}/api/iniciar`, { headers, data: { modo: 'cliente', clientes: ['1'] } });
    expect(duplicada.status()).toBe(400);
    await expect.poll(async () => (await (await request.get(`${url}/api/status`)).json()).fase).toBe('concluido');
    expect(chamadas).toEqual([{ modo: 'cliente', clientes: ['1'] }]);
    const status = await (await request.get(`${url}/api/status`)).json();
    expect(status.resultado.sucessos).toBe(1);
    expect(JSON.stringify(status)).not.toContain('deviceKey');
  } finally {
    await new Promise((resolve) => servidor.close(resolve));
  }
});

test('tela permite escolher clientes e iniciar a atualização', async ({ page }) => {
  let opcoesRecebidas;
  const interfaceApp = criarInterface({
    listar: async () => playlists,
    listarElegiveis: (items) => items,
    agrupar: (items, opcoes) => new Map(items
      .filter((p) => opcoes.modo === 'cliente' && opcoes.clientes.includes(p.clienteId))
      .map((p) => [p.siteAtivacao, [p]])),
    executar: async (_items, opcoes) => {
      opcoesRecebidas = opcoes;
      return { sucessos: 1, falhas: [], playlistsBloqueadas: [], sitesBloqueados: [] };
    },
  });
  const { servidor, url } = await iniciarServidor({ interfaceApp });
  try {
    await page.goto(url);
    await expect(page.locator('#elegiveis')).toHaveText('2');
    await page.getByRole('button', { name: 'Por cliente' }).click();
    await page.getByPlaceholder('Digite para encontrar um cliente...').fill('Ana');
    await page.locator('#clientes input').check();
    await page.getByRole('button', { name: 'Iniciar atualização' }).click();
    await expect(page.locator('#resultado')).toContainText('1 sucesso(s)');
    expect(opcoesRecebidas).toEqual({ modo: 'cliente', clientes: ['1'] });
  } finally {
    await new Promise((resolve) => servidor.close(resolve));
  }
});
