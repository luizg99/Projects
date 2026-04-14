const inquirer = require('inquirer');

async function mostrarInterface() {
  const answers = await inquirer.prompt([
    {
      type: 'checkbox',
      name: 'servidores',
      message: 'Selecione os servidores a atualizar:',
      choices: [
        { name: 'TVS', value: 'TVS', checked: true },
        { name: 'UNIPLAY', value: 'UNIPLAY', checked: true },
        { name: 'BIT', value: 'BIT', checked: true },
        { name: 'FAST', value: 'FAST', checked: true },
      ],
      validate: (choices) =>
        choices.length > 0 || 'Selecione ao menos um servidor!',
    },
    {
      type: 'checkbox',
      name: 'aplicativos',
      message: 'Selecione os aplicativos a atualizar:',
      choices: [
        { name: 'IBO_PLAYER (iboplayer.com)', value: 'IBO_PLAYER', checked: true },
        { name: 'IBO_PRO_PLAYER (iboplayer.pro)', value: 'IBO_PRO_PLAYER', checked: true },
        { name: 'QUICK_PLAYER (quickplayer.app)', value: 'QUICK_PLAYER', checked: true },
        { name: 'VU_PLAYER_PRO (vuplayer.pro)', value: 'VU_PLAYER_PRO', checked: true },
      ],
      validate: (choices) =>
        choices.length > 0 || 'Selecione ao menos um aplicativo!',
    },
  ]);

  return {
    TVS: answers.servidores.includes('TVS'),
    UNIPLAY: answers.servidores.includes('UNIPLAY'),
    BIT: answers.servidores.includes('BIT'),
    FAST: answers.servidores.includes('FAST'),
    IBO_PLAYER: answers.aplicativos.includes('IBO_PLAYER'),
    IBO_PRO_PLAYER: answers.aplicativos.includes('IBO_PRO_PLAYER'),
    QUICK_PLAYER: answers.aplicativos.includes('QUICK_PLAYER'),
    VU_PLAYER_PRO: answers.aplicativos.includes('VU_PLAYER_PRO'),
    selecionado: answers.servidores.length > 0,
  };
}

module.exports = { mostrarInterface };
