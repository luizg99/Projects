const { spawn } = require('node:child_process');
const { criarInterface, iniciarServidor } = require('./src/gui/server');

function abrirNavegador(url) {
  const comando = process.platform === 'win32' ? 'cmd.exe' : process.platform === 'darwin' ? 'open' : 'xdg-open';
  const argumentos = process.platform === 'win32' ? ['/c', 'start', '', url] : [url];
  const processo = spawn(comando, argumentos, { detached: true, stdio: 'ignore', windowsHide: true });
  processo.on('error', (erro) => console.error(`Abra ${url} no navegador: ${erro.message}`));
  processo.unref();
}

const porta = 32177;
iniciarServidor({ porta, interfaceApp: criarInterface({ encerrar: () => process.exit(0) }) }).then(({ url }) => {
  console.log(`Interface disponível em ${url}`);
  abrirNavegador(url);
}).catch(async (erro) => {
  if (erro.code === 'EADDRINUSE') {
    const url = `http://127.0.0.1:${porta}`;
    try {
      const resposta = await fetch(`${url}/api/status`);
      if (resposta.ok) { abrirNavegador(url); return; }
    } catch {}
  }
  console.error(erro);
  process.exitCode = 1;
});
