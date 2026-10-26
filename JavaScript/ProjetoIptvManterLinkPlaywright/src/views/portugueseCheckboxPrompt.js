const CheckboxPrompt = require('inquirer/lib/prompts/checkbox');

class PortugueseCheckboxPrompt extends CheckboxPrompt {
  constructor(questions, readline, answers) {
    super(questions, readline, answers);
    this.dontShowHints = true;
  }

  getQuestion() {
    const pergunta = super.getQuestion();
    if (this.status === 'answered' || !this.opt.hint) return pergunta;
    return `${pergunta}(${this.opt.hint})`;
  }
}

module.exports = PortugueseCheckboxPrompt;
