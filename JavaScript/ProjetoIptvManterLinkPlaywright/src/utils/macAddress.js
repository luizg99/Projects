function normalizarMacAddress(valor) {
  const mac = String(valor ?? '').trim();
  if (!/^(?:[\da-f]{2}:){5}[\da-f]{2}$/i.test(mac)
    && !/^(?:[\da-f]{2}-){5}[\da-f]{2}$/i.test(mac)
    && !/^[\da-f]{12}$/i.test(mac)) return '';

  const digitos = mac.replace(/[:-]/g, '').toUpperCase();
  return digitos.match(/.{2}/g).join(':');
}

module.exports = { normalizarMacAddress };
