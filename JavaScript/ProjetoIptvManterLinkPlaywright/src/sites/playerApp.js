const axios = require('axios');
const lib = require('../biblioteca');
const { SiteRateLimitError } = require('../errors/siteRateLimitError');

const ENDPOINT = process.env.PLAYERAPP_INTEGRATION_API_URL
  || 'https://api.playbr.app.br/api/integracoes/v1/playlists/atualizar-link';

function retryAfterEmSegundos(value) {
  if (!value) return null;
  if (/^\d+$/.test(value)) return Number(value);
  const data = Date.parse(value);
  return Number.isFinite(data) ? Math.max(0, Math.ceil((data - Date.now()) / 1000)) : null;
}

async function processarCliente(macAddress, deviceKey, servidor) {
  const apiKey = process.env.PLAYBR_API_KEY;
  const urlNova = lib.obterLinkAtualizado(servidor);
  const m3u = lib.obterLinkM3UAtualizado(servidor);

  if (!apiKey) throw new Error('Configure PLAYBR_API_KEY no arquivo .env.');
  if (!urlNova) {
    console.error(`PlayerApp API: nenhum link de destino configurado para o servidor ${servidor}.`);
    return false;
  }
  let endpoint;
  try {
    endpoint = new URL(ENDPOINT);
  } catch {
    throw new Error('PLAYERAPP_INTEGRATION_API_URL não é uma URL válida.');
  }
  if (endpoint.protocol !== 'https:') {
    throw new Error('PLAYERAPP_INTEGRATION_API_URL deve usar HTTPS.');
  }

  try {
    const resposta = await axios.post(endpoint.toString(), { macAddress, deviceKey, urlNova, m3u }, {
      headers: { 'x-api-key': apiKey },
      timeout: 30000,
    });
    const resultado = resposta.data?.resultado;
    const tipoPlaylist = String(resposta.data?.tipoPlaylist || '').toLowerCase();
    if (['atualizado', 'inalterado'].includes(resultado)
      && ['m3u', 'xtream'].includes(tipoPlaylist)) {
      console.log(`Processamento concluído para MAC: ${macAddress} (${resultado}, ${tipoPlaylist}) - Servidor: ${servidor} - Aplicativo: PLAYER APP.`);
      return true;
    }
    console.error('PlayerApp API: resposta sem resultado ou tipo de playlist reconhecido.');
    return false;
  } catch (erro) {
    const status = erro.response?.status;
    if (status === 404) {
      const erroNaoEncontrado = new Error('Dispositivo ou playlist não encontrado na API PlayerApp.');
      erroNaoEncontrado.code = 'PLAYERAPP_PLAYLIST_NOT_FOUND';
      throw erroNaoEncontrado;
    }
    if (status === 429) {
      throw new SiteRateLimitError('playbr.app.br', {
        retryAfterSeconds: retryAfterEmSegundos(erro.response.headers?.['retry-after']),
        status,
      });
    }
    console.error(`PlayerApp API: falha na atualização (HTTP ${status || 'sem resposta'}).`);
    return false;
  }
}

module.exports = { processarCliente };
