const lib = require('../biblioteca');
const { verificarRateLimit, isSiteRateLimitError } = require('../errors/siteRateLimitError');
const { aguardarAntesDeRetry } = require('../utils/retryDelay');

function classificarPlaylist(url) {
  try {
    const parsed = new URL(url);
    const caminho = parsed.pathname.toLowerCase();
    const parametros = new Map(
      [...parsed.searchParams.entries()].map(([chave, valor]) => [chave.toLowerCase(), valor.toLowerCase()]),
    );
    const temUsuarioSenha = parametros.has('username') && parametros.has('password');
    const assinaturaGetPhp = caminho.endsWith('/get.php')
      && temUsuarioSenha
      && parametros.get('type') === 'm3u_plus';

    if (assinaturaGetPhp && parametros.get('output') === 'ts') return 'xtream';
    if (assinaturaGetPhp && parametros.get('output') === 'hls') return 'm3u';
    if (/\.m3u8?$/.test(caminho)) return 'm3u';
    if (caminho.endsWith('/get.php') && temUsuarioSenha
      && !parametros.has('type') && !parametros.has('output')) return 'm3u';
  } catch {
    return 'desconhecido';
  }
  return 'desconhecido';
}

function substituirHostXtream(urlOriginal, hostAtualizado) {
  try {
    const original = new URL(urlOriginal);
    const novoHost = new URL(hostAtualizado);
    if (!['http:', 'https:'].includes(original.protocol)
      || !['http:', 'https:'].includes(novoHost.protocol)) return '';
    return `${novoHost.origin}${original.pathname}${original.search}${original.hash}`;
  } catch {
    return '';
  }
}

async function encerrarSessao(page) {
  const botaoSair = page.getByRole('button', { name: /^(sair|logout)$/i }).last();
  if (!await botaoSair.isVisible().catch(() => false)) return;
  await botaoSair.click();
  const confirmacao = page.locator('.ant-modal:visible').last().getByRole('button', { name: /^(sair|logout)$/i });
  if (await confirmacao.isVisible().catch(() => false)) await confirmacao.click();
}

async function processarCliente(macAddress, deviceKey, servidor, page, tentativas = 1) {
  let tentativaAtual = 1;

  while (tentativaAtual <= tentativas) {
    try {
      const response = await page.goto('https://quickplayer.app/login');
      await verificarRateLimit(page, response, 'quickplayer.app');

      const campoMac = page.locator(
        'input[aria-label*="MAC" i], input[aria-label="Mac address"], #mac',
      ).first();
      await campoMac.waitFor({ timeout: 15000 });
      await campoMac.fill(macAddress);

      const entrarComMac = page.getByRole('button', { name: /entrar com o mac|login by mac/i });
      await entrarComMac.click();

      const modalLogin = page.locator('.ant-modal:visible').last();
      const campoDeviceKey = modalLogin.locator('input.modal-input').first();
      await campoDeviceKey.waitFor({ timeout: 15000 });
      await campoDeviceKey.fill(deviceKey);
      await modalLogin.locator('button[type="submit"]').click();

      const botaoEditar = page.getByRole('button', { name: /editar playlist|edit playlist/i }).first();
      await botaoEditar.waitFor({ timeout: 20000 });
      await botaoEditar.click();

      const modalEdicao = page.locator('.ant-modal:visible').last();
      const campoUrl = modalEdicao.locator('form input.modal-input').nth(1);
      await campoUrl.waitFor({ timeout: 10000 });
      const urlOriginal = await campoUrl.inputValue();
      const tipoPlaylist = classificarPlaylist(urlOriginal);

      let urlAtualizada;
      if (tipoPlaylist === 'xtream') {
        urlAtualizada = substituirHostXtream(urlOriginal, lib.obterLinkAtualizado(servidor));
      } else if (tipoPlaylist === 'm3u') {
        urlAtualizada = lib.substituirBaseM3U(urlOriginal, lib.obterLinkM3UAtualizado(servidor));
      } else {
        console.log('QUICK PLAYER: formato da playlist ambíguo; alteração ignorada sem salvar.');
        await encerrarSessao(page);
        return false;
      }

      if (!urlAtualizada) {
        console.log(`QUICK PLAYER: link de atualização ausente ou inválido para ${tipoPlaylist}; alteração ignorada.`);
        await encerrarSessao(page);
        return false;
      }

      await campoUrl.fill(urlAtualizada);
      await modalEdicao.getByRole('button', { name: /^(salvar|save)$/i }).click();
      await modalEdicao.waitFor({ state: 'hidden', timeout: 15000 });
      await encerrarSessao(page);

      console.log(`Processamento QuickPlayer concluído para MAC ${macAddress} - Servidor: ${servidor}.`);
      return true;
    } catch (e) {
      if (isSiteRateLimitError(e)) throw e;
      console.log(`QUICK PLAYER: falha ao processar MAC ${macAddress}.`);
      console.log(e.message);
      await aguardarAntesDeRetry(page, tentativaAtual, tentativas);
      tentativaAtual++;
    }
  }

  console.log(`QUICK PLAYER: todas as tentativas falharam para MAC ${macAddress}.`);
  return false;
}

module.exports = { processarCliente, classificarPlaylist, substituirHostXtream };
