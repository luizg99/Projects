const SITE_RATE_LIMITED = 'SITE_RATE_LIMITED';

class SiteRateLimitError extends Error {
  constructor(site, { rayId = '', retryAfterSeconds = null, status = null } = {}) {
    super(`O site ${site} limitou temporariamente os acessos.`);
    this.name = 'SiteRateLimitError';
    this.code = SITE_RATE_LIMITED;
    this.site = site;
    this.rayId = rayId;
    this.retryAfterSeconds = retryAfterSeconds;
    this.status = status;
  }
}

function isSiteRateLimitError(error) {
  return error?.code === SITE_RATE_LIMITED;
}

async function verificarRateLimit(page, response, site) {
  const status = response?.status() ?? null;
  let body = '';
  try {
    body = await page.locator('body').innerText({ timeout: 2000 });
  } catch {}

  if (status !== 429 && !/error\s*1015|you are being rate limited/i.test(body)) return;

  const headers = response?.headers() || {};
  const rayId = headers['cf-ray'] || body.match(/Ray ID:\s*([a-z\d-]+)/i)?.[1] || '';
  const retryHeader = headers['retry-after'];
  let retryAfterSeconds = null;
  if (retryHeader && /^\d+$/.test(retryHeader)) {
    retryAfterSeconds = Number(retryHeader);
  } else if (retryHeader) {
    const retryAt = Date.parse(retryHeader);
    if (Number.isFinite(retryAt)) retryAfterSeconds = Math.max(0, Math.ceil((retryAt - Date.now()) / 1000));
  }

  throw new SiteRateLimitError(site, { rayId, retryAfterSeconds, status });
}

module.exports = { SiteRateLimitError, isSiteRateLimitError, verificarRateLimit };
