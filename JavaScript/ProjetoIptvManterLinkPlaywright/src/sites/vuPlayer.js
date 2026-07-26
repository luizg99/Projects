const lib = require('../biblioteca');

// Automação para vuplayer.pro
async function processarCliente(macAddress, deviceKey, servidor, page, tentativas = 3) {
  let tentativaAtual = 1;

  while (tentativaAtual < tentativas) {
    try {
      await page.goto('https://vuplayer.pro/login');

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
      await page.goto('https://vuplayer.pro/playlists?type=list');

      // Esperar o botão editar
      const editButton = page.getByRole('button', { name: 'Editar' }).first();
      await editButton.waitFor({ timeout: 15000 });
      await page.waitForTimeout(2000);
      await editButton.click();
      await page.waitForTimeout(1000);

      // Preencher o campo de URL da playlist com o link atualizado
      const campoUrl = page.locator('input[name="playlistHost"]');
      await campoUrl.fill(lib.obterLinkAtualizado(servidor));

      // Salvar
      const saveButton = page.getByRole('button', { name: 'Enviar' });
      await saveButton.waitFor({ timeout: 10000 });
      await saveButton.click();
      await page.waitForTimeout(2000);

      // Logout
      await page.getByRole('button', { name: 'Sair' }).click();
      await page.waitForTimeout(2000);

      console.log(`Processamento concluído para MAC: ${macAddress} com ${tentativaAtual} tentativas`);
      return true;

    } catch (e) {
      console.log(`VUPLAYER: Ocorreu um erro com MAC ${macAddress}`);
      console.log(e.message);
      await page.reload().catch(() => {});
      tentativaAtual++;
    } finally {
      await page.reload().catch(() => {});
    }
  }

  console.log(`Todas as tentativas falharam para MAC: ${macAddress}`);
  return false;
}

module.exports = { processarCliente };
