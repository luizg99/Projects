const { AsyncLocalStorage } = require('node:async_hooks');

// Associa as mensagens de console emitidas durante o processamento a uma playlist,
// mesmo com vários aplicativos rodando em paralelo.
const armazenamento = new AsyncLocalStorage();

function instalarCaptura() {
  const originais = {};
  for (const metodo of ['log', 'error', 'warn']) {
    originais[metodo] = console[metodo];
    console[metodo] = (...args) => {
      const linhas = armazenamento.getStore();
      if (linhas) linhas.push(args.map(String).join(' '));
      originais[metodo].apply(console, args);
    };
  }
  return () => Object.assign(console, originais);
}

async function capturar(fn) {
  const linhas = [];
  try {
    return { resultado: await armazenamento.run(linhas, fn), linhas };
  } catch (erro) {
    if (erro && typeof erro === 'object') erro.linhasCapturadas = linhas;
    throw erro;
  }
}

module.exports = { instalarCaptura, capturar };
