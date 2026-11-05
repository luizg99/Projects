const $ = (id) => document.getElementById(id);
let dados = { total: 0, elegiveis: 0, servidores: [], clientes: [], aplicativos: [] };
let modo = 'tradicional';
const clientesSelecionados = new Set();
let ultimoLog = -1;

async function api(caminho, corpo) {
  const resposta = await fetch(caminho, corpo === undefined ? {} : {
    method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(corpo),
  });
  const conteudo = await resposta.json();
  if (!resposta.ok) throw new Error(conteudo.erro || 'Não foi possível concluir a operação.');
  return conteudo;
}

function mostrarErro(mensagem) {
  $('resultado').className = 'result error';
  $('resultado').replaceChildren();
  const titulo = document.createElement('strong');
  titulo.textContent = 'Não foi possível continuar';
  const detalhe = document.createElement('div');
  detalhe.textContent = mensagem;
  $('resultado').append(titulo, detalhe);
}

function criarEscolha(container, texto, detalhe, valor, checked, change) {
  const label = document.createElement('label');
  label.className = 'choice';
  const input = document.createElement('input');
  input.type = 'checkbox';
  input.value = valor;
  input.checked = checked;
  input.addEventListener('change', change);
  const span = document.createElement('span');
  span.textContent = texto;
  if (detalhe) { const small = document.createElement('small'); small.textContent = detalhe; span.append(small); }
  label.append(input, span);
  container.append(label);
}

function atualizarPrevisao() {
  const quantidade = modo === 'cliente' ? clientesSelecionados.size :
    document.querySelectorAll('#servidores input:checked').length;
  $('previsao').textContent = modo === 'cliente'
    ? `${quantidade} cliente(s) selecionado(s)`
    : `${quantidade} servidor(es) selecionado(s)`;
  $('selecionados').textContent = `${clientesSelecionados.size} cliente(s) selecionado(s)`;
  const temApps = document.querySelectorAll('#aplicativos input:checked').length > 0;
  $('iniciar').disabled = !dados.elegiveis || !quantidade || (modo === 'tradicional' && !temApps) || $('fase').classList.contains('running');
}

function renderClientes() {
  const filtro = $('busca').value.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
  $('clientes').replaceChildren();
  const encontrados = dados.clientes.filter((c) =>
    c.nome.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().includes(filtro) || c.id.includes(filtro));
  for (const cliente of encontrados) criarEscolha($('clientes'), cliente.nome, `ID ${cliente.id}`, cliente.id,
    clientesSelecionados.has(cliente.id), (evento) => {
      if (evento.target.checked) clientesSelecionados.add(cliente.id);
      else clientesSelecionados.delete(cliente.id);
      atualizarPrevisao();
    });
  if (!encontrados.length) $('clientes').textContent = 'Nenhum cliente encontrado.';
  atualizarPrevisao();
}

function renderDados() {
  $('total').textContent = dados.total;
  $('elegiveis').textContent = dados.elegiveis;
  $('servidores').replaceChildren();
  $('aplicativos').replaceChildren();
  for (const servidor of dados.servidores) criarEscolha($('servidores'), servidor, '', servidor, true, atualizarPrevisao);
  for (const app of dados.aplicativos) criarEscolha($('aplicativos'), app.nome, app.site, app.id,
    app.quantidade > 0, atualizarPrevisao);
  if (!dados.servidores.length) $('servidores').textContent = 'Nenhum servidor elegível.';
  if (!dados.aplicativos.some((a) => a.quantidade)) $('aplicativos').textContent = 'Nenhum aplicativo elegível.';
  clientesSelecionados.clear();
  renderClientes();
  atualizarPrevisao();
}

function trocarModo(novoModo) {
  modo = novoModo;
  $('tab-tradicional').classList.toggle('active', modo === 'tradicional');
  $('tab-cliente').classList.toggle('active', modo === 'cliente');
  $('tradicional').classList.toggle('hidden', modo !== 'tradicional');
  $('cliente').classList.toggle('hidden', modo !== 'cliente');
  atualizarPrevisao();
}

async function carregar() {
  $('recarregar').disabled = true;
  $('iniciar').disabled = true;
  $('status-curto').textContent = 'Carregando';
  $('progress-title').textContent = 'Buscando playlists';
  $('progress-subtitle').textContent = 'Consultando os dados do Lembretinho...';
  $('resultado').classList.add('hidden');
  try {
    dados = await api('/api/carregar', {});
    renderDados();
    $('status-curto').textContent = 'Pronto';
    $('progress-title').textContent = 'Tudo pronto para começar';
    $('progress-subtitle').textContent = `${dados.elegiveis} playlists disponíveis para atualização.`;
  } catch (erro) {
    $('status-curto').textContent = 'Erro';
    $('progress-title').textContent = 'Falha ao carregar dados';
    $('progress-subtitle').textContent = erro.message;
    mostrarErro(erro.message);
  } finally { $('recarregar').disabled = false; }
}

async function iniciar() {
  const entrada = modo === 'cliente'
    ? { modo, clientes: [...clientesSelecionados] }
    : { modo, servidores: [...document.querySelectorAll('#servidores input:checked')].map((e) => e.value),
      aplicativos: [...document.querySelectorAll('#aplicativos input:checked')].map((e) => e.value) };
  try {
    await api('/api/iniciar', entrada);
    $('resultado').classList.add('hidden');
    ultimoLog = -1;
    await atualizarStatus();
  } catch (erro) { mostrarErro(erro.message); }
}

function renderResultado(resultado) {
  const caixa = $('resultado');
  caixa.className = resultado.falhas.length || resultado.sitesBloqueados.length ? 'result error' : 'result';
  caixa.replaceChildren();
  const titulo = document.createElement('strong');
  titulo.textContent = `${resultado.sucessos} sucesso(s) · ${resultado.falhas.length} falha(s)`;
  caixa.append(titulo);
  if (resultado.bloqueadas) {
    const texto = document.createElement('div');
    texto.textContent = `${resultado.bloqueadas} playlist(s) não processada(s) por limite de acesso.`;
    caixa.append(texto);
  }
  for (const bloqueio of resultado.sitesBloqueados) {
    const texto = document.createElement('div');
    texto.textContent = `Limite de acesso em ${bloqueio.site}. ${bloqueio.retryAfterSeconds ? `Aguarde ao menos ${bloqueio.retryAfterSeconds}s.` : 'Aguarde antes de tentar novamente.'}`;
    caixa.append(texto);
  }
  if (resultado.falhas.length) {
    const lista = document.createElement('ul');
    for (const falha of resultado.falhas) {
      const item = document.createElement('li');
      item.textContent = `${falha.mac} · ${falha.site} · ${falha.servidor}`;
      lista.append(item);
    }
    caixa.append(lista);
  }
}

async function atualizarStatus() {
  try {
    const estado = await api('/api/status');
    const emExecucao = estado.fase === 'executando';
    $('fase').textContent = ({ aguardando: 'Aguardando', carregando: 'Carregando', pronto: 'Pronto', executando: 'Em andamento', concluido: 'Concluído', erro: 'Erro' })[estado.fase] || estado.fase;
    $('fase').className = `phase ${emExecucao ? 'running' : estado.fase === 'concluido' ? 'done' : estado.fase === 'erro' ? 'error' : ''}`;
    $('status-curto').textContent = $('fase').textContent;
    $('recarregar').disabled = emExecucao || estado.fase === 'carregando';
    $('encerrar').disabled = emExecucao || estado.fase === 'carregando';
    atualizarPrevisao();
    if (emExecucao) {
      $('progress-title').textContent = 'Atualização em andamento';
      $('progress-subtitle').textContent = `Processando ${estado.total} playlist(s) selecionada(s).`;
    } else if (estado.fase === 'concluido') {
      $('progress-title').textContent = 'Atualização concluída';
      $('progress-subtitle').textContent = 'Confira o resumo e o registro abaixo.';
    } else if (estado.fase === 'erro') {
      $('progress-title').textContent = 'A atualização foi interrompida';
      $('progress-subtitle').textContent = estado.erro || 'Confira o registro abaixo.';
    }
    const restante = [...estado.logs].reverse().find((log) => /Faltam? (\d+) MAC/.test(log.mensagem));
    const faltam = restante ? Number(restante.mensagem.match(/Faltam? (\d+) MAC/)[1]) : estado.total;
    const percentual = estado.fase === 'concluido' ? 100 : estado.total ? Math.max(0, Math.round((1 - faltam / estado.total) * 100)) : 0;
    $('progress-fill').style.width = `${percentual}%`;
    $('progress-number').textContent = `${percentual}%`;
    if (estado.logs.length !== ultimoLog) {
      $('logs').replaceChildren();
      for (const log of estado.logs) {
        const linha = document.createElement('div');
        linha.className = `log-row ${log.tipo === 'erro' ? 'error' : ''}`;
        const hora = document.createElement('time'); hora.textContent = log.hora;
        const texto = document.createElement('span'); texto.textContent = log.mensagem;
        linha.append(hora, texto); $('logs').append(linha);
      }
      if (!estado.logs.length) $('logs').innerHTML = '<div class="empty-log">Nenhuma atividade até agora.</div>';
      $('logs').scrollTop = $('logs').scrollHeight;
      ultimoLog = estado.logs.length;
    }
    if (estado.resultado) renderResultado(estado.resultado);
    else if (estado.fase === 'erro' && estado.erro) mostrarErro(estado.erro);
  } catch (erro) { $('status-curto').textContent = 'Desconectado'; }
}

$('tab-tradicional').addEventListener('click', () => trocarModo('tradicional'));
$('tab-cliente').addEventListener('click', () => trocarModo('cliente'));
$('busca').addEventListener('input', renderClientes);
$('recarregar').addEventListener('click', carregar);
$('iniciar').addEventListener('click', iniciar);
$('encerrar').addEventListener('click', async () => {
  try {
    await api('/api/encerrar', {});
    $('progress-title').textContent = 'Interface encerrada';
    $('progress-subtitle').textContent = 'Você pode fechar esta aba. Abra o atalho para usar novamente.';
    $('encerrar').disabled = true;
    clearInterval(atualizacaoAutomatica);
  } catch (erro) { mostrarErro(erro.message); }
});
$('todos-servidores').addEventListener('click', () => { document.querySelectorAll('#servidores input').forEach((e) => { e.checked = true; }); atualizarPrevisao(); });
$('todos-apps').addEventListener('click', () => { document.querySelectorAll('#aplicativos input').forEach((e) => { e.checked = true; }); atualizarPrevisao(); });
api('/api/dados').then((valor) => { dados = valor; renderDados(); if (!dados.total) carregar(); }).catch(mostrarErro);
atualizarStatus();
const atualizacaoAutomatica = setInterval(atualizarStatus, 1000);
