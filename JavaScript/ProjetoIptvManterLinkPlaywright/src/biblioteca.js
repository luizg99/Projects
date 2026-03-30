const axios = require('axios');
const { parse } = require('csv-parse/sync');

// Variáveis globais dos links
let linkAtualizadoTvs = '';
let linkAtualizadoUniplay = '';
let linkAtualizadoBit = '';
let linkAtualizadoFast = '';

let captchaApiKey = '';

const sheetUrlLinks =
  'https://docs.google.com/spreadsheets/d/1lMIq91MwJcxuNDZsJaTLdYmj_M2bmLO5_R9Y5EWDEb8/export?format=csv';

async function getGoogleSheetData(url) {
  const response = await axios.get(url);
  const records = parse(response.data, {
    columns: true,
    skip_empty_lines: true,
    trim: true,
  });
  return records;
}

async function atualizarLinks() {
  const data = await getGoogleSheetData(sheetUrlLinks);

  for (const row of data) {
    const nomePainel = (row['Nome painel'] || '').trim();
    const link = (row['Link'] || '').trim();

    if (nomePainel === 'link_tvs') linkAtualizadoTvs = link;
    else if (nomePainel === 'link_uniplay') linkAtualizadoUniplay = link;
    else if (nomePainel === 'link_bit') linkAtualizadoBit = link;
    else if (nomePainel === 'link_fast') linkAtualizadoFast = link;
  }

  console.log('Links atualizados com sucesso:');
  console.log(`TVS: ${linkAtualizadoTvs}`);
  console.log(`Uniplay: ${linkAtualizadoUniplay}`);
  console.log(`Bit: ${linkAtualizadoBit}`);
  console.log(`Fast: ${linkAtualizadoFast}`);
}

function obterLinkAtualizado(servidor) {
  switch (servidor) {
    case 'TVS': return linkAtualizadoTvs;
    case 'UNIPLAY': return linkAtualizadoUniplay;
    case 'BIT': return linkAtualizadoBit;
    case 'FAST': return linkAtualizadoFast;
    default: return '';
  }
}

// Resolve CAPTCHA via 2Captcha enviando imagem em base64
async function solveCaptchaWith2Captcha(imageBuffer, apiKey) {
  const base64Image = imageBuffer.toString('base64');

  const params = new URLSearchParams();
  params.append('key', apiKey);
  params.append('method', 'base64');
  params.append('body', base64Image);
  params.append('json', '1');

  const response = await axios.post('http://2captcha.com/in.php', params);

  if (response.data.status !== 1) {
    throw new Error(`Erro ao enviar CAPTCHA para 2Captcha: ${response.data.request}`);
  }

  const captchaId = response.data.request;
  const resultUrl = `http://2captcha.com/res.php?key=${apiKey}&action=get&id=${captchaId}&json=1`;

  while (true) {
    await new Promise((resolve) => setTimeout(resolve, 5000));
    const result = await axios.get(resultUrl);

    if (result.data.status === 1) {
      return result.data.request;
    }

    if (result.data.request !== 'CAPCHA_NOT_READY') {
      throw new Error(`Erro ao resolver CAPTCHA: ${result.data.request}`);
    }
  }
}

module.exports = {
  getGoogleSheetData,
  atualizarLinks,
  obterLinkAtualizado,
  solveCaptchaWith2Captcha,
  get captchaApiKey() { return captchaApiKey; },
  set captchaApiKey(val) { captchaApiKey = val; },
};
