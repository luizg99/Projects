const { test, expect } = require('@playwright/test');
const { normalizarMacAddress } = require('../src/utils/macAddress');
const { processarPlaylist } = require('../src/services/automacaoService');

test('normaliza MAC compacto e delimitado', () => {
  expect(normalizarMacAddress('B9ED03C8B9CF')).toBe('B9:ED:03:C8:B9:CF');
  expect(normalizarMacAddress('aa-bb-cc-dd-ee-ff')).toBe('AA:BB:CC:DD:EE:FF');
  expect(normalizarMacAddress('AA:BB:CC:DD:EE:FF')).toBe('AA:BB:CC:DD:EE:FF');
});

test('rejeita MAC malformado antes de abrir o painel', async () => {
  const playlist = {
    siteAtivacao: 'quickplayer.app',
    macAddress: 'NA:RA:6X:LS:ZO:ZZ',
    servidor: 'TVS',
  };

  const sucesso = await processarPlaylist(playlist, {
    servidores: ['TVS'],
    QUICK_PLAYER: true,
  });

  expect(sucesso).toBe(false);
  expect(playlist.motivoFalha).toMatch(/MAC inválido/);
});
