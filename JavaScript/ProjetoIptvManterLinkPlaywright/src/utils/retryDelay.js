const INTERVALO_MS = Math.max(0, Number(process.env.AUTOMACAO_INTERVALO_MS) || 30000);

async function aguardarAntesDeRetry(page, tentativaAtual, tentativas) {
  if (tentativaAtual + 1 >= tentativas || INTERVALO_MS === 0) return;
  const esperaMs = INTERVALO_MS * (2 ** (tentativaAtual - 1));
  console.log(`Aguardando ${Math.ceil(esperaMs / 1000)}s antes da próxima tentativa.`);
  await page.waitForTimeout(esperaMs);
}

module.exports = { INTERVALO_MS, aguardarAntesDeRetry };
