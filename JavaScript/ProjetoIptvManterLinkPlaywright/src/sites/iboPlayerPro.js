const lib = require('../biblioteca');
const { verificarRateLimit, isSiteRateLimitError } = require('../errors/siteRateLimitError');
const { aguardarAntesDeRetry } = require('../utils/retryDelay');

function erroConfiguracaoPlaylist(mensagem) {
  const erro = new Error(mensagem);
  erro.code = 'IBO_PRO_PLAYLIST_CONFIGURATION';
  return erro;
}

// Automação para iboplayer.pro
async function processarCliente(macAddress, deviceKey, servidor, page, tentativas = 3) {
  let tentativaAtual = 1;

  while (tentativaAtual < tentativas) {
    try {
      const response = await page.goto('https://iboplayer.pro/manage-playlists/login/');
      await verificarRateLimit(page, response, 'iboplayer.pro');
      await page.waitForTimeout(2000);

      await page.evaluate(() => window.scrollBy(0, 200));
      await page.waitForTimeout(2000);

      // Preencher MAC Address
      await page.locator('#mac_address').waitFor({ timeout: 15000 });
      await page.fill('#mac_address', macAddress);

      // Preencher Device Key (campo password)
      await page.fill('#password', deviceKey);

      // Login
      await page
        .locator('xpath=/html/body/div[1]/div[3]/div/form/button')
        .click();

      // Esperar o botão edit aparecer
      const editButton = page.locator(
        'xpath=/html/body/div[1]/div[3]/div/div[3]/div[2]/section/div/div[2]/table/tbody/tr/td[3]/button[1]'
      );
      await editButton.waitFor({ timeout: 15000 });

      await page.evaluate(() => window.scrollBy(0, 700));
      await page.waitForTimeout(2000);

      await editButton.click();

      // O campo URL pode continuar visível no modo XC; host + usuário + senha
      // identificam Xtream e têm prioridade para evitar atualizar o campo errado.
      const m3uInput = page.locator('input[name="url"]:visible, #playlist-url:visible').first();
      const hostInput = page.locator('[name="host"]:visible, #host:visible').first();
      const usernameInput = page.locator('input[name="username"]:visible, #username:visible');
      const passwordInput = page.locator('input[name="password"]:visible, #password:visible');
      // count() não espera: aguarda o formulário de edição renderizar antes de classificar a playlist.
      await m3uInput.or(hostInput).waitFor({ state: 'visible', timeout: 10000 }).catch(() => {});
      const temCamposXtream = await hostInput.count()
        && await usernameInput.count()
        && await passwordInput.count();

      if (temCamposXtream || await hostInput.count()) {
        const novoHost = lib.obterLinkAtualizado(servidor);
        if (!novoHost) throw erroConfiguracaoPlaylist('IBO PRO PLAYER: URL atual do servidor não informada.');
        await hostInput.fill(novoHost);
      } else if (await m3uInput.count()) {
        const urlOriginal = await m3uInput.inputValue();
        const novaUrl = lib.substituirBaseM3U(urlOriginal, lib.obterLinkM3UAtualizado(servidor));
        if (!novaUrl) {
          throw erroConfiguracaoPlaylist('IBO PRO PLAYER: URL M3U ou base m3u inválida; playlist não foi salva.');
        }
        await m3uInput.fill(novaUrl);
      } else {
        throw new Error(`IBO PRO PLAYER: tipo de playlist não identificado para MAC ${macAddress}; nada foi alterado.`);
      }

      // Salvar
      await page
        .locator('xpath=/html/body/div[3]/div/div/div[2]/form/button')
        .click();
      await page.waitForTimeout(2000);

      await page.reload();

      // Logout (site não usa mais id fixo — vira uma aba com o texto "Logout")
      const logoutButton = page.getByText('Logout', { exact: true });
      await logoutButton.waitFor({ timeout: 10000 });
      await logoutButton.click();

      await page.waitForTimeout(3000);
      console.log(
        `Processamento concluído para MAC: ${macAddress} com ${tentativaAtual} tentativas`
      );
      return true;

    } catch (e) {
      if (isSiteRateLimitError(e)) throw e;
      if (e.code === 'IBO_PRO_PLAYLIST_CONFIGURATION') {
        console.log(e.message);
        return false;
      }
      console.log(`IBO PRO PLAYER: Ocorreu um erro com MAC ${macAddress}`);
      console.log(e.message);
      await aguardarAntesDeRetry(page, tentativaAtual, tentativas);
      tentativaAtual++;
    }
  }

  console.log(`Todas as tentativas falharam para MAC: ${macAddress}`);
  return false;
}

module.exports = { processarCliente };
