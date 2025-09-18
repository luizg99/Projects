import tkinter as tk
from tkinter import messagebox

class InterfaceAtualizacao:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("Atualização de Servidores")

        # Descrição
        lblDescricao = tk.Label(self.root, text="Servidores a atualizarem:")
        lblDescricao.grid(row=0, column=0, columnspan=3, padx=10, pady=10)

        # Variáveis dos checkboxes
        self.varTvs = tk.BooleanVar(value=True)
        self.varUniplay = tk.BooleanVar(value=True)
        self.varBit = tk.BooleanVar(value=True)
        self.varFast = tk.BooleanVar(value=True)

        # Checkboxes
        chkTvs = tk.Checkbutton(self.root, text="TVS", variable=self.varTvs)
        chkTvs.grid(row=1, column=0, padx=5, pady=5)

        chkUniplay = tk.Checkbutton(self.root, text="UNIPLAY", variable=self.varUniplay)
        chkUniplay.grid(row=1, column=1, padx=5, pady=5)

        chkBit = tk.Checkbutton(self.root, text="BIT", variable=self.varBit)
        chkBit.grid(row=1, column=2, padx=5, pady=5)

        chkFast = tk.Checkbutton(self.root, text="FAST", variable=self.varFast)
        chkFast.grid(row=1, column=3, padx=5, pady=5)

        # Botão para iniciar a atualização
        btnIniciar = tk.Button(self.root, text="Iniciar atualização links", command=self.onIniciar)
        btnIniciar.grid(row=2, column=0, columnspan=3, pady=10)

        self.selecionado = False

    def onIniciar(self):
        # Verifica se ao menos um checkbox está marcado
        if not (self.varTvs.get() or self.varUniplay.get() or self.varBit.get() or self.varFast.get()):
            messagebox.showwarning("Atenção", "Pelo menos um servidor deve ser selecionado!")
        else:
            self.selecionado = True
            self.root.quit()  # Fecha a interface após seleção

    def exibir(self):
        self.root.mainloop()
        return {
            'TVS': self.varTvs.get(),
            'UNIPLAY': self.varUniplay.get(),
            'BIT': self.varBit.get(),
            'FAST': self.varFast.get(),
            'selecionado': self.selecionado
        }
