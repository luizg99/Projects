const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');

function dois(n) { return String(n).padStart(2, '0'); }

// Cria Downloads/prints-erro-iptv/AAAA-MM-DD_HH-mm-ss para uma execução.
function criarPastaDaExecucao(agora = new Date()) {
  const nome = `${agora.getFullYear()}-${dois(agora.getMonth() + 1)}-${dois(agora.getDate())}`
    + `_${dois(agora.getHours())}-${dois(agora.getMinutes())}-${dois(agora.getSeconds())}`;
  return path.join(os.homedir(), 'Downloads', 'prints-erro-iptv', nome);
}

function nomeSeguro(texto) {
  return String(texto).replace(/[^a-zA-Z0-9.-]+/g, '-');
}

// Salva o print da página no momento da falha. Nunca lança erro: retorna null se falhar.
async function salvarPrint(pagina, pasta, playlist, numero) {
  if (!pagina || !pasta) return null;
  try {
    fs.mkdirSync(pasta, { recursive: true });
    const arquivo = path.join(pasta, `${nomeSeguro(playlist.siteAtivacao)}_${nomeSeguro(playlist.servidor || 'sem-servidor')}_${nomeSeguro(playlist.macAddress)}_${numero}.png`);
    await pagina.screenshot({ path: arquivo, timeout: 8000 });
    return arquivo;
  } catch (erro) {
    console.error(`Não foi possível salvar o print de erro (${erro.message.split('\n')[0]}).`);
    return null;
  }
}

module.exports = { criarPastaDaExecucao, salvarPrint };
