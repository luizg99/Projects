const lib = require('../biblioteca');
const { verificarRateLimit, isSiteRateLimitError } = require('../errors/siteRateLimitError');
const { aguardarAntesDeRetry } = require('../utils/retryDelay');

// Automação para quickplayer.app
// Particularidade: a URL é atualizada preservando usuário/senha da URL original (/get.php?username=...&password=...)
async function processarCliente(macAddress, deviceKey, servidor, page, tentativas = 3) {
  let tentativaAtual = 1;

  while (tentativaAtual < tentativas) {
    try {
      const response = await page.goto('https://quickplayer.app/#/login');
      await verificarRateLimit(page, response, 'quickplayer.app');
      await page.waitForTimeout(2000);

      // Preencher MAC Address
      await page.locator('#mac').waitFor({ timeout: 15000 });
      await page.fill('#mac', macAddress);

      // Loop: clicar no botão até o campo device key aparecer
      while (true) {
        const isVisible = await page.locator('#login_code').isVisible().catch(() => false);
        if (isVisible) break;
        await page
          .locator('xpath=//*[@id="login"]/div/div[2]/div/div/div/div/button')
          .click();
        await page.waitForTimeout(2000);
      }

      // Preencher Device Key
      await page.locator('#login_code').waitFor({ timeout: 15000 });
      await page.fill('#login_code', deviceKey);

      // Login
      const loginButton = page.locator('#login-btn');
      if (await loginButton.count()) {
        await loginButton.click();
      } else {
        await page
          .locator(
            'xpath=/html/body/div[2]/div/div[2]/div/div[2]/div/div[3]/div/div/div/div/div/button'
          )
          .click();
      }

      // Esperar e clicar no SVG de edição (width=29, height=29)
      const svgElement = page.locator("svg[width='29'][height='29']");
      await svgElement.waitFor({ timeout: 15000 });
      await svgElement.click();
      await page.waitForTimeout(1000);

      // Obter URL atual e construir nova URL mantendo usuário/senha
      const campoUrl = page.locator('#url');
      const conteudo = await campoUrl.inputValue();

      const partes = conteudo.split('/get.php');
      const novaUrl = lib.obterLinkAtualizado(servidor) + '/get.php' + partes[1];

      await campoUrl.fill(novaUrl);

      // Salvar
      const saveButton = page.locator('#save-btn');
      if (await saveButton.count()) {
        await saveButton.click();
      } else {
        await page
          .locator(
            'xpath=/html/body/div[2]/div/div[2]/div/div[2]/div/div/form/div/div[7]/button'
          )
          .click();
      }
      await page.waitForTimeout(1000);

      // Logout (2 cliques de confirmação)
      const logoutButton = page.locator('#logout-btn');
      if (await logoutButton.count()) {
        await logoutButton.click();
      } else {
        await page
          .locator('xpath=/html/body/div[1]/div/section/header/div[3]/button[2]')
          .click();
      }
      await page.waitForTimeout(1000);

      const confirmButton = page.locator('#confirm-btn');
      if (await confirmButton.count()) {
        await confirmButton.click();
      } else {
        await page
          .locator(
            'xpath=/html/body/div[3]/div/div[2]/div/div[2]/div/div/div[2]/button[2]'
          )
          .click();
      }
      await page.waitForTimeout(1000);

      console.log(`Processamento concluído para MAC: ${macAddress} com ${tentativaAtual} tentativas`);
      return true;

    } catch (e) {
      if (isSiteRateLimitError(e)) throw e;
      console.log(`QUICK PLAYER: Ocorreu um erro com MAC ${macAddress}`);
      console.log(e.message);
      await aguardarAntesDeRetry(page, tentativaAtual, tentativas);
      tentativaAtual++;
    }
  }

  console.log(`Todas as tentativas falharam para MAC: ${macAddress}`);
  return false;
}

module.exports = { processarCliente };
