import tkinter as tk
import json

# Variável global para armazenar a janela de consulta de mensagens a enviar
janela_consultar_enviar = None

# Função para abrir a janela de cadastro de mensagens de saudação
def abrir_janela_cadastro_saudacao():
    janela_cadastro_saudacao = tk.Toplevel(janela_principal)
    janela_cadastro_saudacao.title("Cadastro de Mensagens de Saudação")

    mensagem_saudacao_label = tk.Label(janela_cadastro_saudacao,
                                       text="Insira as mensagens de saudação (uma por linha):")
    mensagem_saudacao_label.pack()

    mensagem_saudacao_entry = tk.Text(janela_cadastro_saudacao, height=10, width=30)
    mensagem_saudacao_entry.pack()

    # Preenche a caixa de texto com as mensagens de saudação cadastradas, se houver
    if MensagensSaudacao:
        mensagem_saudacao_entry.insert("1.0", "\n".join(MensagensSaudacao))

    cadastrar_button = tk.Button(janela_cadastro_saudacao, text="Cadastrar",
                                 command=lambda: cadastrar_mensagens_saudacao(mensagem_saudacao_entry))
    cadastrar_button.pack()

    janela_cadastro_saudacao.transient(
        janela_principal)  # Define a janela de cadastro como janela filha da janela principal
    janela_cadastro_saudacao.grab_set()  # Impede que a janela principal seja clicada enquanto a janela de cadastro estiver aberta

# Função para abrir a janela de cadastro de mensagens a enviar
def abrir_janela_cadastro_enviar():
    global janela_cadastro_enviar
    janela_cadastro_enviar = tk.Toplevel(janela_principal)
    janela_cadastro_enviar.title("Cadastro de Mensagens a Enviar")

    mensagem_enviar_label = tk.Label(janela_cadastro_enviar, text="Insira a mensagem a enviar:")
    mensagem_enviar_label.pack()

    mensagem_enviar_entry = tk.Text(janela_cadastro_enviar, height=10, width=30)
    mensagem_enviar_entry.pack()

    cadastrar_button = tk.Button(janela_cadastro_enviar, text="Cadastrar", command=lambda: cadastrar_mensagem_enviar(mensagem_enviar_entry))
    cadastrar_button.pack()

    janela_cadastro_enviar.transient(
        janela_principal)  # Define a janela de cadastro como janela filha da janela principal
    janela_cadastro_enviar.grab_set()  # Impede que a janela principal seja clicada enquanto a janela de cadastro estiver aberta

# Função para abrir a janela de consulta de mensagens a enviar
def abrir_janela_consultar_enviar():
    global janela_consultar_enviar  # Declara a variável global para a janela de consulta
    if janela_consultar_enviar:
        janela_consultar_enviar.destroy()  # Fecha a janela de consulta se já estiver aberta

    janela_consultar_enviar = tk.Toplevel(janela_principal)
    janela_consultar_enviar.title("Consultar Mensagens a Enviar")

    # Função para maximizar a janela
    def maximizar_janela():
        if janela_consultar_enviar.state() == "normal":
            janela_consultar_enviar.state("zoomed")  # Maximiza a janela
        else:
            janela_consultar_enviar.state("normal")  # Restaura a janela ao tamanho normal

    # Botão para maximizar/restaurar a janela
    maximizar_button = tk.Button(janela_consultar_enviar, text="Maximizar/Restaurar", command=maximizar_janela)
    maximizar_button.pack()

    # Frame para conter os componentes redimensionáveis
    frame = tk.Frame(janela_consultar_enviar)
    frame.pack(fill=tk.BOTH, expand=True)

    # Cria uma lista de mensagens a enviar
    lista_mensagens_enviar = tk.Listbox(frame, height=10, width=40)
    lista_mensagens_enviar.pack(fill=tk.BOTH, expand=True)

    # Preenche a lista com as mensagens cadastradas
    for mensagem in MensagensEnviar:
        lista_mensagens_enviar.insert(tk.END, mensagem)

    # Função para excluir uma mensagem selecionada
    def excluir_mensagem_selecionada():
        selecionados = lista_mensagens_enviar.curselection()
        for index in selecionados[::-1]:  # Percorre a lista de trás para frente para evitar problemas com índices
            lista_mensagens_enviar.delete(index)
            del MensagensEnviar[index]
        salvar_mensagens_enviar()

    # Função para editar uma mensagem selecionada
    def editar_mensagem_selecionada():
        selecionados = lista_mensagens_enviar.curselection()
        if selecionados:
            index = selecionados[0]  # Pega o primeiro item selecionado
            mensagem_selecionada = MensagensEnviar[index]
            abrir_janela_edicao(index, mensagem_selecionada)

    # Botão para excluir mensagem selecionada
    excluir_button = tk.Button(frame, text="Excluir Mensagem Selecionada",
                               command=excluir_mensagem_selecionada)
    excluir_button.pack(side=tk.LEFT)

    # Botão para editar mensagem selecionada
    editar_button = tk.Button(frame, text="Editar Mensagem Selecionada",
                              command=editar_mensagem_selecionada)
    editar_button.pack(side=tk.LEFT)

    # Configuração para que o frame se expanda quando a janela for redimensionada
    janela_consultar_enviar.rowconfigure(0, weight=1)
    janela_consultar_enviar.columnconfigure(0, weight=1)

    janela_consultar_enviar.transient(
        janela_principal)  # Define a janela de consulta como janela filha da janela principal
    janela_consultar_enviar.grab_set()  # Impede que a janela principal seja clicada enquanto a janela de consulta estiver aberta

# Função para abrir a janela de edição de mensagem
def abrir_janela_edicao(index, mensagem):
    janela_edicao = tk.Toplevel(janela_consultar_enviar)
    janela_edicao.title("Editar Mensagem")

    mensagem_editar_entry = tk.Text(janela_edicao, height=10, width=30)
    mensagem_editar_entry.pack()
    mensagem_editar_entry.insert("1.0", mensagem)

    # Função para finalizar a edição e salvar a mensagem editada
    def finalizar_edicao():
        nova_mensagem = mensagem_editar_entry.get("1.0", "end-1c")
        MensagensEnviar[index] = nova_mensagem
        salvar_mensagens_enviar()
        janela_edicao.destroy()
        abrir_janela_consultar_enviar()  # Reabre a tela de consulta após a edição

    # Botão para finalizar a edição
    finalizar_button = tk.Button(janela_edicao, text="Finalizar Alteração", command=finalizar_edicao)
    finalizar_button.pack()

# Função para cadastrar mensagens de saudação
def cadastrar_mensagens_saudacao(mensagem_saudacao_entry):
    mensagens = mensagem_saudacao_entry.get("1.0", "end-1c").split('\n')
    mensagens = [msg.strip() for msg in mensagens if msg.strip()]

    if mensagens:
        MensagensSaudacao.clear()
        MensagensSaudacao.extend(mensagens)
        status_label.config(text="Mensagens de saudação cadastradas com sucesso!")
        salvar_mensagens_saudacao()


# Função para exibir a mensagem de sucesso e restaurar a janela de cadastro
def exibir_mensagem_sucesso(mensagem):
    janela_sucesso = tk.Toplevel(janela_principal)
    janela_sucesso.title("Sucesso")

    label_sucesso = tk.Label(janela_sucesso, text=mensagem)
    label_sucesso.pack()

    # Configure um botão para fechar a janela de sucesso e restaurar a janela de cadastro
    def fechar_janela_sucesso():
        janela_sucesso.destroy()
        janela_principal.deiconify()  # Restaurar a janela principal, se necessário

    fechar_button = tk.Button(janela_sucesso, text="Fechar", command=fechar_janela_sucesso)
    fechar_button.pack()

# Função para iniciar o envio de números de telefone
def iniciar_envio_numeros():
    numeros = telefone_text.get("1.0", "end-1c").split('\n')  # Obtém números de telefone como uma lista
    numeros = [numero.strip() for numero in numeros if numero.strip()]
    if numeros:
        salvar_numeros_telefones(numeros)
        exibir_mensagem_sucesso("Números de telefone cadastrados com sucesso!")

# Função para salvar os números de telefone em um arquivo JSON
def salvar_numeros_telefones(numeros):
    with open("NumerosTelefones.json", "w") as arquivo_json:
        json.dump({"NumerosTelefones": numeros}, arquivo_json)

# Função para cadastrar mensagem a enviar
def cadastrar_mensagem_enviar(mensagem_enviar_entry):
    mensagem = mensagem_enviar_entry.get("1.0", "end-1c")

    if mensagem:
        MensagensEnviar.append(mensagem)
        salvar_mensagens_enviar()

    janela_cadastro_enviar.destroy()
    # Exibe a mensagem de sucesso
    exibir_mensagem_sucesso("Mensagem cadastrada com sucesso!")

# Função para salvar as mensagens de saudação cadastradas em um arquivo JSON
def salvar_mensagens_saudacao():
    with open("MensagensSaudacao.json", "w") as arquivo_json:
        json.dump({"MensagensSaudacao": MensagensSaudacao}, arquivo_json)
    status_label.config(text="Mensagens de saudação cadastradas salvas com sucesso!")

# Função para salvar as mensagens a enviar cadastradas em um arquivo JSON
def salvar_mensagens_enviar():
    with open("MensagensEnviar.json", "w") as arquivo_json:
        json.dump({"MensagensEnviar": MensagensEnviar}, arquivo_json)
    status_label.config(text="Mensagens a enviar cadastradas salvas com sucesso!")

# Cria a janela principal
janela_principal = tk.Tk()
janela_principal.title("Interface Principal")

# Cria um botão para abrir a janela de cadastro de mensagens de saudação
abrir_janela_cadastro_saudacao_button = tk.Button(janela_principal, text="Cadastrar Mensagem Saudação",
                                                  command=abrir_janela_cadastro_saudacao)
abrir_janela_cadastro_saudacao_button.pack()

# Cria um botão para abrir a janela de cadastro de mensagens a enviar
abrir_janela_cadastro_enviar_button = tk.Button(janela_principal, text="Cadastrar Mensagem a Enviar",
                                                command=abrir_janela_cadastro_enviar)
abrir_janela_cadastro_enviar_button.pack()

# Cria um botão para abrir a janela de consulta de mensagens a enviar
abrir_janela_consultar_enviar_button = tk.Button(janela_principal, text="Consultar Mensagens Enviar",
                                                 command=abrir_janela_consultar_enviar)
abrir_janela_consultar_enviar_button.pack()

# Cria uma caixa de texto de várias linhas para o usuário inserir números de telefone
telefone_text = tk.Text(janela_principal, height=10, width=30)
telefone_text.pack()

# Cria um botão para iniciar o envio de números de telefone
iniciar_envio_button = tk.Button(janela_principal, text="Iniciar Envio", command=iniciar_envio_numeros)
iniciar_envio_button.pack()

# Cria um rótulo para exibir o status
status_label = tk.Label(janela_principal, text="")
status_label.pack()

# Inicializa a lista de mensagens de saudação
MensagensSaudacao = []

# Inicializa a lista de mensagens de saudação com mensagens padrão, se houver
try:
    with open("MensagensSaudacao.json", "r") as arquivo_json:
        dados = json.load(arquivo_json)
        if "MensagensSaudacao" in dados:
            MensagensSaudacao = dados["MensagensSaudacao"]
except FileNotFoundError:
    pass

# Inicializa a lista de mensagens a enviar
MensagensEnviar = []

# Inicializa a lista de mensagens a enviar com mensagens padrão, se houver
try:
    with open("MensagensEnviar.json", "r") as arquivo_json:
        dados = json.load(arquivo_json)
        if "MensagensEnviar" in dados:
            MensagensEnviar = dados["MensagensEnviar"]
except FileNotFoundError:
    pass

# Inicia a interface gráfica
janela_principal.mainloop()
