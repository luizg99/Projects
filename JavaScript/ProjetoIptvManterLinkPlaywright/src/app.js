const automacaoController = require('./controllers/automacaoController');
const interfaceView = require('./views/interfaceView');

async function iniciar() {
  try {
    await automacaoController.executar();
  } catch (erro) {
    interfaceView.mostrarErroFatal(erro);
    process.exitCode = 1;
  }
}

module.exports = { iniciar };
