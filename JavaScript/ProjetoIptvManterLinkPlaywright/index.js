const { chromium } = require('playwright');
const lib = require('./src/biblioteca');
const Interface = require('./src/interface');
const iboPlayer = require('./src/sites/iboPlayer');
const iboPlayerPro = require('./src/sites/iboPlayerPro');
const quickPlayer = require('./src/sites/quickPlayer');
const vuPlayer = require('./src/sites/vuPlayer');

// Grid 0 é a principal
const GRID_ID = 0;
const sheetUrlClientes = `https://docs.google.com/spreadsheets/d/1ifSYQKY2W-DA0D0wYY00tKag90Tp5FJP3mdZ0lConUs/export?format=csv&gid=${GRID_ID}`;

// Configuração da API Key do 2Captcha
lib.captchaApiKey = '0439b8069b74afca88f8062c8eb51716';

async function processarCliente(row, opcoes, page) {
  const macAddress = (row['MAC Address'] || '').trim();
  const deviceKey = (row['Device Key'] || '').trim();
  const servidor = (row['Servidor'] || '').trim();
  const siteAtivacao = (row['Site ativação'] || '').trim();
  const dataValidadeApp = (row['data validade APP'] || '').trim();
  const ativo = (row['Ativo'] || '').trim();
  const atualizar = (row['Atualizar'] || '').trim();

  // Pular clientes sem data de validade, inativos ou marcados para não atualizar
  if (!dataValidadeApp || ativo === 'N' || atualizar === 'N') return null;

  // Pular servidores não selecionados
  if (servidor === 'TVS' && !opcoes.TVS) return null;
  if (servidor === 'UNIPLAY' && !opcoes.UNIPLAY) return null;
  if (servidor === 'BIT' && !opcoes.BIT) return null;
  if (servidor === 'FAST' && !opcoes.FAST) return null;
  if (!servidor || servidor === 'nan') return null;
  if (!siteAtivacao || siteAtivacao === 'nan') return null;

  console.log(`Processando MAC: ${macAddress}, servidor: ${servidor}`);

  if (siteAtivacao === 'iboplayer.com') {
    return iboPlayer.processarCliente(macAddress, deviceKey, servidor, page);
  } else if (siteAtivacao === 'iboplayer.pro') {
    return iboPlayerPro.processarCliente(macAddress, deviceKey, servidor, page);
  } else if (siteAtivacao === 'quickplayer.app') {
    return quickPlayer.processarCliente(macAddress, deviceKey, servidor, page);
  } else if (siteAtivacao === 'vuplayer.pro') {
    return vuPlayer.processarCliente(macAddress, deviceKey, servidor, page);
  } else {
    console.log(`Aviso: site de ativação não reconhecido: ${siteAtivacao}`);
    return null;
  }
}

async function main() {
  // Atualizar links a partir da planilha de links
  await lib.atualizarLinks();

  // Exibir interface e obter servidores selecionados
  const opcoes = await Interface.mostrarInterface();

  if (!opcoes.selecionado) {
    console.log('Nenhum servidor selecionado. Saindo...');
    return;
  }

  console.log('Lendo a planilha de clientes...');
  const clientes = await lib.getGoogleSheetData(sheetUrlClientes);

  const browser = await chromium.launch({ headless: false });
  const page = await browser.newPage();
  await page.setViewportSize({ width: 1280, height: 800 });

  const clientesFalhados = [];
  let qtdeClientes = 0;

  // Primeira passagem: todos os clientes
  for (const row of clientes) {
    const macAddress = (row['MAC Address'] || '').trim();
    const deviceKey = (row['Device Key'] || '').trim();
    const servidor = (row['Servidor'] || '').trim();
    const siteAtivacao = (row['Site ativação'] || '').trim();

    const sucesso = await processarCliente(row, opcoes, page);

    if (sucesso === null) continue; // pulado (filtrado)

    if (!sucesso) {
      clientesFalhados.push({ macAddress, deviceKey, servidor, siteAtivacao });
    } else {
      qtdeClientes++;
    }
  }

  // Tentativas extras para clientes que falharam (até 2 tentativas adicionais)
  console.log('Fim-------------------------------------------------------');
  console.log('TENTANDO PARA OS CLIENTES QUE FALHARAM:');

  let falhados = [...clientesFalhados];
  let tentativa = 1;

  while (falhados.length > 0 && tentativa < 3) {
    console.log(`\nTentativa ${tentativa} para clientes que falharam...`);
    const novosFalhados = [];

    for (const { macAddress, deviceKey, servidor, siteAtivacao } of falhados) {
      console.log(`Reprocessando MAC: ${macAddress}, Servidor: ${servidor}, Site: ${siteAtivacao}`);

      let sucesso = false;
      if (siteAtivacao === 'iboplayer.com') {
        sucesso = await iboPlayer.processarCliente(macAddress, deviceKey, servidor, page);
      } else if (siteAtivacao === 'iboplayer.pro') {
        sucesso = await iboPlayerPro.processarCliente(macAddress, deviceKey, servidor, page);
      } else if (siteAtivacao === 'quickplayer.app') {
        sucesso = await quickPlayer.processarCliente(macAddress, deviceKey, servidor, page);
      } else if (siteAtivacao === 'vuplayer.pro') {
        sucesso = await vuPlayer.processarCliente(macAddress, deviceKey, servidor, page);
      }

      if (!sucesso) novosFalhados.push({ macAddress, deviceKey, servidor, siteAtivacao });
    }

    falhados = novosFalhados;
    tentativa++;
  }

  // Relatório final
  if (falhados.length > 0) {
    console.log('\nOs seguintes clientes falharam após todas as tentativas:');
    for (const { macAddress, servidor, siteAtivacao } of falhados) {
      console.log(`MAC: ${macAddress} | Servidor: ${servidor} | Site: ${siteAtivacao}`);
    }
  } else {
    console.log('\nTodos os clientes foram processados com sucesso!');
    console.log(`Quantidade de clientes atualizados: ${qtdeClientes}`);
  }

  await browser.close();
}

main().catch((err) => {
  console.error('Erro fatal:', err);
  process.exit(1);
});
