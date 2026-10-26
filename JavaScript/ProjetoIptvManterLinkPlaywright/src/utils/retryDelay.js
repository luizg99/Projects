const INTERVALO_MS = Math.max(0, Number(process.env.AUTOMACAO_INTERVALO_MS) || 30000);

async function aguardarComContagemRegressiva(esperaMs, destino, esperar = (ms) =>
  new Promise((resolve) => setTimeout(resolve, ms))) {
  if (esperaMs <= 0) return;

  const terminaEm = Date.now() + esperaMs;
  const totalSegundos = Math.ceil(esperaMs / 1000);
  let ultimoRelato = totalSegundos;
  let proximoMarco = totalSegundos > 5 ? totalSegundos - 5 : totalSegundos - 1;
  console.log(`Aguardando ${totalSegundos}s ${destino}.`);

  const contador = setInterval(() => {
    const restantes = Math.max(0, Math.ceil((terminaEm - Date.now()) / 1000));
    if (restantes === 0 || restantes === ultimoRelato) return;
    if (restantes <= 5 || restantes <= proximoMarco) {
      console.log(`Restam ${restantes}s ${destino}.`);
      ultimoRelato = restantes;
      proximoMarco = restantes - 5;
    }
  }, 250);

  try {
    await esperar(esperaMs);
  } finally {
    clearInterval(contador);
  }
}

async function aguardarAntesDeRetry(page, tentativaAtual, tentativas, macAddress = '') {
  if (tentativaAtual + 1 >= tentativas || INTERVALO_MS === 0) return;
  const esperaMs = INTERVALO_MS * (2 ** (tentativaAtual - 1));
  const destino = macAddress
    ? `antes da próxima tentativa (MAC ${macAddress})`
    : 'antes da próxima tentativa';
  await aguardarComContagemRegressiva(esperaMs, destino, (ms) => page.waitForTimeout(ms));
}

module.exports = { INTERVALO_MS, aguardarAntesDeRetry, aguardarComContagemRegressiva };
