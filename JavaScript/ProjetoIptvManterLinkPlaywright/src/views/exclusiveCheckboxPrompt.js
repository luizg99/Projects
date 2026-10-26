const PortugueseCheckboxPrompt = require('./portugueseCheckboxPrompt');

class ExclusiveCheckboxPrompt extends PortugueseCheckboxPrompt {
  toggleChoice(index) {
    const escolha = this.opt.choices.getChoice(index);
    if (!escolha || escolha.disabled) return;
    const marcar = !escolha.checked;
    this.opt.choices.forEach((opcao) => {
      if (opcao.type !== 'separator') opcao.checked = false;
    });
    escolha.checked = marcar;
  }

  onAllKey() {
    const escolha = this.opt.choices.getChoice(this.pointer);
    if (escolha && !escolha.checked) this.toggleChoice(this.pointer);
    this.render();
  }

  onInverseKey() {
    const marcado = this.opt.choices.realChoices.findIndex((escolha) => escolha.checked);
    this.pointer = marcado === 0 ? 1 : 0;
    const proximo = this.opt.choices.getChoice(this.pointer);
    if (proximo && !proximo.checked) this.toggleChoice(this.pointer);
    this.render();
  }
}

module.exports = ExclusiveCheckboxPrompt;
