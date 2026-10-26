const lib = require('../biblioteca');
const { verificarRateLimit, isSiteRateLimitError } = require('../errors/siteRateLimitError');
const { aguardarAntesDeRetry } = require('../utils/retryDelay');

function erroConfiguracaoPlaylist(mensagem) {
  const erro = new Error(mensagem);
  erro.code = 'IBO_PLAYLIST_CONFIGURATION';
  return erro;
}

// Automação para iboplayer.com
// Possui CAPTCHA resolvido via 2Captcha
async function processarCliente(macAddress, deviceKey, servidor, page, tentativas = 2) {
  let tentativaAtual = 1;

  while (tentativaAtual <= tentativas) {
    try {
      const response = await page.goto('https://iboplayer.com/device/login');
      await verificarRateLimit(page, response, 'iboplayer.com');
      await page.waitForTimeout(2000);

      // Aceitar termos
      await page.locator('xpath=/html/body/div[1]/div/div/div/button').click();

      // Preencher MAC Address
      await page.locator('#max-address').waitFor({ timeout: 15000 });
      await page.fill('#max-address', macAddress);

      // Preencher Device Key
      await page.fill('#device-key', deviceKey);

      // Scroll para revelar o CAPTCHA
      await page.evaluate(() => window.scrollBy(0, 800));
      await page.waitForTimeout(2000);

      // Capturar screenshot do elemento CAPTCHA
      const captchaElement = page.locator('xpath=//*[@id="root"]/main/div/form/div[5]/div');
      const captchaBuffer = await captchaElement.screenshot();

      // Resolver CAPTCHA via 2Captcha
      const captchaText = await lib.solveCaptchaWith2Captcha(captchaBuffer, lib.captchaApiKey);

      // Preencher o campo de CAPTCHA
      await page
        .locator("xpath=//label[text()='Captcha']/following-sibling::input")
        .fill(captchaText);

      // Login
      await page.locator('xpath=/html/body/div/main/div/form/button').click();

      // Esperar o botão SVG de edição e clicar
      const svgElement = page.locator('svg.text-blue-500.cursor-pointer');
      await svgElement.waitFor({ timeout: 15000 });
      await svgElement.click();

      // O painel usa formulários diferentes para M3U e Xtream.
      const m3uInput = page.locator('#playlist-url:visible, input[name="playlist-url"]:visible, #url:visible').first();
      const hostInput = page.locator('#host:visible, input[name="host"]:visible, #server:visible, input[name="server"]:visible').first();
      const usernameInput = page.locator('#username:visible, input[name="username"]:visible');
      const passwordInput = page.locator('#password:visible, input[name="password"]:visible');

      // count() não espera: aguarda o formulário de edição renderizar antes de classificar a playlist.
      await m3uInput.or(hostInput).waitFor({ state: 'visible', timeout: 10000 }).catch(() => {});

      const temCamposXtream = await hostInput.count()
        && await usernameInput.count()
        && await passwordInput.count();

      if (temCamposXtream) {
        const novoHost = lib.obterLinkAtualizado(servidor);
        if (!novoHost) throw erroConfiguracaoPlaylist('IBO Player: URL atual do servidor não foi informada.');
        await hostInput.fill(novoHost);
      } else if (await m3uInput.count()) {
        const urlOriginal = await m3uInput.inputValue();
        const urlBaseM3U = lib.obterLinkM3UAtualizado(servidor);
        const novaUrl = lib.substituirBaseM3U(urlOriginal, urlBaseM3U);
        if (!novaUrl) {
          throw erroConfiguracaoPlaylist('IBO Player: URL M3U ou base m3u inválida; playlist não foi salva.');
        }
        await m3uInput.fill(novaUrl);
      } else if (await hostInput.count()) {
        // Compatibilidade com o formulário antigo do IBO, que expõe somente o campo Host.
        const novoHost = lib.obterLinkAtualizado(servidor);
        if (!novoHost) throw erroConfiguracaoPlaylist('IBO Player: URL atual do servidor não foi informada.');
        await hostInput.fill(novoHost);
      } else {
        throw new Error(`IBO Player: tipo de playlist não identificado para MAC ${macAddress}; nada foi alterado.`);
      }

      // Salvar
      await page
        .locator('xpath=//*[@id="root"]/main/div/section/form/div[2]/button')
        .click();
      await page.waitForTimeout(5000);

      await page.evaluate(() => window.scrollBy(0, 800));
      await page.waitForTimeout(2000);

      // Logout
      const logoutButton = page.locator('xpath=//*[@id="root"]/main/aside/a[6]');
      await logoutButton.waitFor({ timeout: 10000 });
      await logoutButton.click();

      await page.waitForTimeout(3000);
      console.log(
        `Processamento concluído para MAC: ${macAddress} com ${tentativaAtual} tentativa${tentativaAtual === 1 ? '' : 's'} - Servidor: ${servidor}`
      );
      return true;

    } catch (e) {
      if (isSiteRateLimitError(e)) throw e;
      if (e.code === 'IBO_PLAYLIST_CONFIGURATION') {
        console.log(e.message);
        return false;
      }
      console.log(`IBO PLAYER: Ocorreu um erro com MAC ${macAddress}`);
      console.log(e.message);

      try {
        await page
          .locator('xpath=//*[@id="root"]/main/aside/a[7]')
          .click({ timeout: 3000 });
      } catch {}

      await aguardarAntesDeRetry(page, tentativaAtual, tentativas);
      tentativaAtual++;
    }
  }

  console.log(`Todas as tentativas falharam para MAC: ${macAddress}`);
  return false;
}

module.exports = { processarCliente };
