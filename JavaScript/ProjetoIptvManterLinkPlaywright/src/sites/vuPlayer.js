const lib = require('../biblioteca');
const { verificarRateLimit, isSiteRateLimitError } = require('../errors/siteRateLimitError');
const { aguardarAntesDeRetry } = require('../utils/retryDelay');

// Encerra a sessão quando a edição é abandonada, para não afetar o próximo MAC.
async function sairSemSalvar(page) {
  try {
    await page.getByRole('button', { name: 'Sair' }).click({ timeout: 3000 });
    await page.waitForTimeout(1000);
  } catch {}
}

// Automação para vuplayer.pro
async function processarCliente(macAddress, deviceKey, servidor, page, tentativas = 1) {
  let tentativaAtual = 1;

  while (tentativaAtual <= tentativas) {
    try {
      let response = await page.goto('https://vuplayer.pro/login');
      await verificarRateLimit(page, response, 'vuplayer.pro');

      // Preencher MAC Address
      await page.locator('input[name="macAddress"]').waitFor({ timeout: 15000 });
      await page.waitForTimeout(5000);
      await page.fill('input[name="macAddress"]', macAddress);

      // Preencher Device Key
      await page.fill('input[name="deviceKey"]', deviceKey);

      // Login
      await page.locator('button[type="submit"]').click();
      await page.waitForTimeout(2000);

      // Ir para a lista de playlists gerenciadas
      response = await page.goto('https://vuplayer.pro/playlists?type=list');
      await verificarRateLimit(page, response, 'vuplayer.pro');

      // Esperar o botão editar
      const editButton = page.getByRole('button', { name: 'Editar' }).first();
      await editButton.waitFor({ timeout: 15000 });
      await page.waitForTimeout(2000);
      await editButton.click();
      await page.waitForTimeout(1000);

      // O VU Player Pro renderiza formulários diferentes para XC e M3U.
      // XC usa host/usuário/senha; M3U usa uma URL completa.
      const campoHost = page.locator('input[name="playlistHost"]');
      const campoUsuario = page.locator('input[name="userName"]');
      const campoSenha = page.locator('input[name="password"]');
      const formularioXtream = await campoHost.isVisible()
        && await campoUsuario.isVisible()
        && await campoSenha.isVisible();

      if (formularioXtream) {
        const hostAtualizado = lib.obterLinkAtualizado(servidor);
        if (!hostAtualizado) {
          console.log(`VUPLAYER: nenhum link de destino configurado para o servidor ${servidor} (MAC ${macAddress}).`);
          await sairSemSalvar(page);
          return false;
        }
        await campoHost.fill(hostAtualizado);
      } else {
        const campoM3U = page.locator('input[name="playlistUrl"]');
        if (!await campoM3U.isVisible()) {
          console.log(`VUPLAYER: formulário de edição não reconhecido (nem Xtream nem M3U) para MAC ${macAddress}.`);
          await sairSemSalvar(page);
          return false;
        }

        const urlOriginal = await campoM3U.inputValue();
        const baseM3U = lib.obterLinkM3UAtualizado(servidor);
        const urlAtualizada = lib.substituirBaseM3U(urlOriginal, baseM3U);
        if (!urlAtualizada) {
          console.log(`VUPLAYER: não foi possível gerar o novo link M3U para MAC ${macAddress}. Servidor: ${servidor}; base de destino ${baseM3U ? 'configurada' : 'NÃO configurada'}; link atual ${urlOriginal ? 'preenchido' : 'vazio'}.`);
          await sairSemSalvar(page);
          return false;
        }
        await campoM3U.fill(urlAtualizada);
      }

      // Salvar
      const saveButton = page.getByRole('button', { name: 'Enviar' });
      await saveButton.waitFor({ timeout: 10000 });
      await saveButton.click();
      await page.waitForTimeout(2000);

      // Logout
      await page.getByRole('button', { name: 'Sair' }).click();
      await page.waitForTimeout(2000);

      console.log(`Processamento concluído para MAC: ${macAddress} com ${tentativaAtual} tentativa${tentativaAtual === 1 ? '' : 's'} - Servidor: ${servidor} - Aplicativo: VU_PLAYER_PRO`);
      return true;

    } catch (e) {
      if (isSiteRateLimitError(e)) throw e;
      console.log(`VUPLAYER: Ocorreu um erro com MAC ${macAddress}`);
      console.log(e.message);
      await aguardarAntesDeRetry(page, tentativaAtual, tentativas);
      tentativaAtual++;
    }
  }

  console.log(`Todas as tentativas falharam para MAC: ${macAddress}`);
  return false;
}

module.exports = { processarCliente };
