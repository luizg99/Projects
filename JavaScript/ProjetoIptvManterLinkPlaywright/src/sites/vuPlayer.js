const lib = require('../biblioteca');

// Automação para vuplayer.pro
async function processarCliente(macAddress, deviceKey, servidor, page, tentativas = 3) {
  let tentativaAtual = 1;

  while (tentativaAtual < tentativas) {
    try {
      await page.goto('https://vuplayer.pro/login');

      // Preencher MAC Address
      await page.locator('#macAddress').waitFor({ timeout: 15000 });
      await page.waitForTimeout(5000);
      await page.fill('#macAddress', macAddress);

      // Preencher Device Key
      await page.fill('#key', deviceKey);

      // Login
      await page
        .locator(
          'xpath=/html/body/div[2]/section[2]/div/div/div[2]/form/div/div[4]/button'
        )
        .click();
      await page.waitForTimeout(2000);

      // Esperar o botão edit
      const editButton = page.locator(
        'xpath=/html/body/div[2]/section[2]/div/div[2]/div[1]/table/tbody/tr/td[3]/button[1]'
      );
      await editButton.waitFor({ timeout: 15000 });
      await page.waitForTimeout(2000);
      await editButton.click();
      await page.waitForTimeout(1000);

      // Preencher o campo de URL da playlist com o link atualizado
      const campoUrl = page.locator('#playlist-url');
      await campoUrl.fill(lib.obterLinkAtualizado(servidor));

      // Salvar
      const saveButton = page.locator(
        'xpath=/html/body/div[2]/section[2]/div/div[2]/div[2]/div/div/div[3]/button[2]'
      );
      await saveButton.waitFor({ timeout: 10000 });
      await saveButton.click();
      await page.waitForTimeout(2000);

      // Logout via URL direta
      await page.goto('https://vuplayer.pro/logout');

      console.log(`Processamento concluído para MAC: ${macAddress} com ${tentativaAtual} tentativas`);
      return true;

    } catch (e) {
      console.log(`VUPLAYER: Ocorreu um erro com MAC ${macAddress}`);
      console.log(e.message);
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
