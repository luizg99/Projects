const axios = require('axios');

const ENDPOINT = process.env.LEMBRETINHO_API_URL
  || 'https://lembretinho.com.br/api/integracoes/v1/playlists';

async function listarTodas({ apiKey = process.env.LEMBRETINHO_API_KEY, apiUrl = ENDPOINT } = {}) {
  if (!apiKey) throw new Error('Configure LEMBRETINHO_API_KEY no arquivo .env.');
  const registros = [];
  const cursoresConsultados = new Set();
  let cursor;

  while (true) {
    const params = { limite: 500 };
    if (cursor !== undefined) params.cursor = cursor;
    const resposta = await axios.get(apiUrl, {
      headers: { 'x-api-key': apiKey },
      params,
      timeout: 30000,
    });
    if (!Array.isArray(resposta.data?.dados)) {
      throw new Error('Resposta inválida da API de playlists do Lembretinho.');
    }
    registros.push(...resposta.data.dados);
    cursor = resposta.data.proximoCursor;
    if (cursor == null) return registros;
    if (typeof cursor !== 'string' || cursoresConsultados.has(cursor)) {
      throw new Error('Cursor inválido ou repetido na API de playlists do Lembretinho.');
    }
    cursoresConsultados.add(cursor);
  }
}

module.exports = { listarTodas };
