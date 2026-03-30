const lib = require('../biblioteca');

// Automação para iboplayer.com
// Possui CAPTCHA resolvido via 2Captcha
async function processarCliente(macAddress, deviceKey, servidor, page, tentativas = 3) {
  let tentativaAtual = 1;

  while (tentativaAtual < tentativas) {
    try {
      await page.goto('https://iboplayer.com/device/login');
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

      // Preencher o campo Host com o link atualizado
      const hostInput = page.locator('#host');
      await hostInput.waitFor({ timeout: 15000 });
      await hostInput.fill(lib.obterLinkAtualizado(servidor));

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
        `Processamento concluído para MAC: ${macAddress} com ${tentativaAtual} tentativas`
      );
      return true;

    } catch (e) {
      console.log(`IBO PLAYER: Ocorreu um erro com MAC ${macAddress}`);
      console.log(e.message);

      try {
        await page
          .locator('xpath=//*[@id="root"]/main/aside/a[7]')
          .click({ timeout: 3000 });
      } catch {}

      await page.reload();
      tentativaAtual++;
    } finally {
      await page.reload().catch(() => {});
    }
  }

  console.log(`Todas as tentativas falharam para MAC: ${macAddress}`);
  return false;
}

module.exports = { processarCliente };
