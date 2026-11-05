const http = require('node:http');
const fs = require('node:fs');
const path = require('node:path');
const playlistService = require('../services/playlistService');
const automacaoService = require('../services/automacaoService');
const { listarClientes } = require('../views/interfaceView');

const APLICATIVOS = [
  ['IBO_PLAYER', 'iboplayer.com', 'IBO Player'],
  ['IBO_PRO_PLAYER', 'iboplayer.pro', 'IBO Player Pro'],
  ['QUICK_PLAYER', 'quickplayer.app', 'Quick Player'],
  ['VU_PLAYER_PRO', 'vuplayer.pro', 'VU Player Pro'],
  ['PLAYER_APP_API', 'playbr.app.br', 'Player App'],
];
const ARQUIVOS = {
  '/': ['index.html', 'text/html; charset=utf-8'],
  '/app.css': ['app.css', 'text/css; charset=utf-8'],
  '/app.js': ['app.js', 'text/javascript; charset=utf-8'],
};

function criarInterface({ listar = playlistService.listar, listarElegiveis = playlistService.listarElegiveis,
  executar = automacaoService.executar, agrupar = automacaoService.agruparPorAplicativo,
  encerrar = () => {} } = {}) {
  let playlists = [];
  let carregado = false;
  let carregando = false;
  let executando = false;
  let estado = { fase: 'aguardando', total: 0, elegiveis: 0, logs: [], resultado: null, erro: null };

  function adicionarLog(tipo, mensagem) {
    estado.logs.push({ tipo, mensagem: String(mensagem), hora: new Date().toLocaleTimeString('pt-BR') });
    if (estado.logs.length > 300) estado.logs.shift();
  }

  function resumo() {
    const elegiveis = listarElegiveis(playlists);
    const servidores = [...new Set(elegiveis.map((p) => p.servidor).filter(Boolean))]
      .sort((a, b) => a.localeCompare(b, 'pt-BR'));
    const aplicativos = APLICATIVOS.map(([id, site, nome]) => ({
      id, site, nome, quantidade: elegiveis.filter((p) => p.siteAtivacao === site).length,
    }));
    return { total: playlists.length, elegiveis: elegiveis.length, servidores,
      clientes: listarClientes(elegiveis), aplicativos };
  }

  async function carregar() {
    if (executando || carregando) return { erro: 'Aguarde a operação em andamento.' };
    carregando = true;
    estado = { fase: 'carregando', total: 0, elegiveis: 0, logs: [], resultado: null, erro: null };
    try {
      playlists = await listar();
      carregado = true;
      const dados = resumo();
      estado.fase = 'pronto';
      estado.total = dados.total;
      estado.elegiveis = dados.elegiveis;
      return dados;
    } catch (erro) {
      carregado = false;
      playlists = [];
      estado.fase = 'erro';
      estado.erro = erro.message;
      return { erro: erro.message };
    } finally {
      carregando = false;
    }
  }

  function validarSelecao(entrada) {
    if (!carregado) throw new Error('Carregue as playlists antes de iniciar.');
    if (!entrada || !['tradicional', 'cliente'].includes(entrada.modo)) throw new Error('Escolha um modo válido.');
    const dados = resumo();
    const opcoes = { modo: entrada.modo };
    if (entrada.modo === 'cliente') {
      const permitidos = new Set(dados.clientes.map((c) => c.id));
      if (!Array.isArray(entrada.clientes) || !entrada.clientes.length ||
          entrada.clientes.some((id) => !permitidos.has(String(id)))) {
        throw new Error('Selecione ao menos um cliente válido.');
      }
      opcoes.clientes = [...new Set(entrada.clientes.map(String))];
    } else {
      const servidores = new Set(dados.servidores);
      const apps = new Set(APLICATIVOS.map(([id]) => id));
      if (!Array.isArray(entrada.servidores) || !entrada.servidores.length ||
          entrada.servidores.some((nome) => !servidores.has(nome))) {
        throw new Error('Selecione ao menos um servidor válido.');
      }
      if (!Array.isArray(entrada.aplicativos) || !entrada.aplicativos.length ||
          entrada.aplicativos.some((id) => !apps.has(id))) {
        throw new Error('Selecione ao menos um aplicativo válido.');
      }
      opcoes.servidores = [...new Set(entrada.servidores)];
      for (const id of apps) opcoes[id] = entrada.aplicativos.includes(id);
    }
    const filas = agrupar(listarElegiveis(playlists), opcoes);
    const quantidade = [...filas.values()].reduce((soma, fila) => soma + fila.length, 0);
    if (!quantidade) throw new Error('A seleção não contém playlists para atualizar.');
    return { opcoes, quantidade, porAplicativo: [...filas].map(([site, fila]) => ({ site, quantidade: fila.length })) };
  }

  function iniciar(entrada) {
    if (executando || carregando) throw new Error('Aguarde a operação em andamento.');
    const selecao = validarSelecao(entrada);
    executando = true;
    estado = { fase: 'executando', total: selecao.quantidade, elegiveis: selecao.quantidade,
      porAplicativo: selecao.porAplicativo, logs: [], resultado: null, erro: null };
    adicionarLog('info', `Iniciando atualização de ${selecao.quantidade} playlist(s).`);
    const logOriginal = console.log;
    const erroOriginal = console.error;
    console.log = (...args) => { adicionarLog('info', args.join(' ')); logOriginal(...args); };
    console.error = (...args) => { adicionarLog('erro', args.join(' ')); erroOriginal(...args); };
    Promise.resolve().then(() => executar(listarElegiveis(playlists), selecao.opcoes))
      .then((resultado) => {
        estado.resultado = {
          sucessos: resultado.sucessos,
          falhas: resultado.falhas.map((p) => ({ mac: p.macAddress, site: p.siteAtivacao, servidor: p.servidor })),
          bloqueadas: resultado.playlistsBloqueadas.length,
          sitesBloqueados: resultado.sitesBloqueados,
        };
        estado.fase = 'concluido';
        adicionarLog('info', `Concluído: ${resultado.sucessos} sucesso(s), ${resultado.falhas.length} falha(s).`);
      })
      .catch((erro) => {
        estado.fase = 'erro';
        estado.erro = erro.message;
        adicionarLog('erro', `Erro na automação: ${erro.message}`);
      })
      .finally(() => { console.log = logOriginal; console.error = erroOriginal; executando = false; });
    return { quantidade: selecao.quantidade, porAplicativo: selecao.porAplicativo };
  }

  function enviarJson(res, codigo, dados) {
    res.writeHead(codigo, { 'Content-Type': 'application/json; charset=utf-8', 'Cache-Control': 'no-store' });
    res.end(JSON.stringify(dados));
  }

  async function atender(req, res) {
    const host = req.headers.host || '';
    if (!/^127\.0\.0\.1:\d+$/.test(host) && !/^localhost:\d+$/.test(host)) {
      return enviarJson(res, 403, { erro: 'Acesso local apenas.' });
    }
    const pathname = new URL(req.url, `http://${host}`).pathname;
    if (req.method === 'GET' && ARQUIVOS[pathname]) {
      const [arquivo, tipo] = ARQUIVOS[pathname];
      res.writeHead(200, { 'Content-Type': tipo, 'Cache-Control': 'no-store', 'X-Content-Type-Options': 'nosniff' });
      return fs.createReadStream(path.join(__dirname, 'public', arquivo)).pipe(res);
    }
    if (req.method === 'GET' && pathname === '/api/status') return enviarJson(res, 200, estado);
    if (req.method === 'GET' && pathname === '/api/dados') {
      return enviarJson(res, 200, carregado ? resumo() : { total: 0, elegiveis: 0, servidores: [], clientes: [], aplicativos: [] });
    }
    if (req.method === 'POST' && ['/api/carregar', '/api/iniciar', '/api/encerrar'].includes(pathname)) {
      const origin = req.headers.origin;
      if (origin !== `http://${host}` || !req.headers['content-type']?.startsWith('application/json')) {
        return enviarJson(res, 403, { erro: 'Requisição inválida.' });
      }
      let corpo = '';
      for await (const parte of req) {
        corpo += parte;
        if (corpo.length > 65536) return enviarJson(res, 413, { erro: 'Dados muito grandes.' });
      }
      try {
        const entrada = JSON.parse(corpo || '{}');
        if (pathname === '/api/encerrar') {
          if (executando || carregando) return enviarJson(res, 409, { erro: 'Aguarde o fim da operação para encerrar.' });
          enviarJson(res, 200, { ok: true });
          setTimeout(encerrar, 100);
          return;
        }
        if (pathname === '/api/carregar') {
          const dados = await carregar();
          return enviarJson(res, dados.erro ? 400 : 200, dados);
        }
        return enviarJson(res, 200, iniciar(entrada));
      } catch (erro) {
        return enviarJson(res, 400, { erro: erro.message });
      }
    }
    enviarJson(res, 404, { erro: 'Não encontrado.' });
  }

  return { atender, carregar, iniciar, resumo, status: () => estado };
}

function iniciarServidor({ porta = 0, interfaceApp = criarInterface() } = {}) {
  const servidor = http.createServer((req, res) => {
    Promise.resolve(interfaceApp.atender(req, res)).catch((erro) => {
      if (!res.headersSent) { res.writeHead(500); res.end(erro.message); }
    });
  });
  return new Promise((resolve, reject) => {
    servidor.once('error', reject);
    servidor.listen(porta, '127.0.0.1', () => resolve({ servidor, url: `http://127.0.0.1:${servidor.address().port}` }));
  });
}

module.exports = { criarInterface, iniciarServidor };
