const lib = require('../biblioteca');
const { verificarRateLimit, isSiteRateLimitError } = require('../errors/siteRateLimitError');
const { aguardarAntesDeRetry } = require('../utils/retryDelay');

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

      // Preencher o campo Host
      const hostInput = page.locator('[name="host"]');
      await hostInput.waitFor({ timeout: 10000 });
      await hostInput.fill(lib.obterLinkAtualizado(servidor));

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
