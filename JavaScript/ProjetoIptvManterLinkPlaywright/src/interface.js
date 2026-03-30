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
  ]);

  return {
    TVS: answers.servidores.includes('TVS'),
    UNIPLAY: answers.servidores.includes('UNIPLAY'),
    BIT: answers.servidores.includes('BIT'),
    FAST: answers.servidores.includes('FAST'),
    selecionado: answers.servidores.length > 0,
  };
}

module.exports = { mostrarInterface };
