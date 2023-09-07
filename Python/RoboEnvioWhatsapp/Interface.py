import tkinter as tk
import json
from selenium import webdriver
from selenium.webdriver import ActionChains
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.keys import Keys
import datetime
import random
import os
import time
import pyperclip
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



#Função para salvar os números
def Salvar_numeros_a_enviar():
    numeros = telefone_text.get("1.0", "end-1c").split('\n')  # Obtém números de telefone como uma lista
    numeros = [numero.strip() for numero in numeros if numero.strip()]
    if numeros:
        salvar_numeros_telefones(numeros)

def ler_json(filename):
    # Construct the full path to the JSON file
    filepath = os.path.join(os.getcwd(), filename)

    # Read and return the JSON data
    with open(filepath, 'r', encoding='utf-8-sig') as file:
        data = file.read()
        return json.loads(data)

# Função para iniciar o envio de números de telefone
def iniciar_envio_numeros():
    # Load JSON data for numerosArray, Saudacao, and MensagemEnviar
    numerosArray = ler_json('NumerosTelefones.json')
    Saudacao = ler_json('MensagensSaudacao.json')
    MensagemEnviar = ler_json('MensagensEnviar.json')

    Salvar_numeros_a_enviar()
    service = Service(ChromeDriverManager().install())
    navegador = webdriver.Chrome(service=service)
    navegador.get("https://web.whatsapp.com")

    #tempo determinado para o usuário logar. tentar fazer a lógica mais eficiente para isso.
    time.sleep(20)

    count = 0;
    NumerosEncaminhar = []  # Create an empty array to store phone numbers to forward
    UltimaMensagem = ''
    for numero in numerosArray['numeros']:
        try:
            mensagemSaudacao = random.choice(Saudacao["MensagensSaudacao"])
            link = f"https://web.whatsapp.com/send?phone={numero}&text={mensagemSaudacao}"
            navegador.get(link)

            #while len(navegador.find_elements_by_id('side')) < 1:
            #    time.sleep(1)

            time.sleep(5)
            navegador.find_element('xpath','//*[@id="main"]/footer/div[1]/div/span[2]/div/div[2]/div[1]/div/div[1]/p').send_keys(Keys.ENTER)
            time.sleep(3)

            count = count + 1;
            NumerosEncaminhar.append(numero)
        except Exception:
            print('O número: ', numero, ' é invalido por isso não foi enviado nem uma mensagem.   ', datetime.now())

        if count == 3:
            # Pegar uma mensagem aleatória
            mensagem = random.choice(MensagemEnviar["MensagensEnviar"])

            # se a ultima mensagem mandada for igual a próxima sorteada, sortear novamente até pegar uma mensagem diferente.
            while UltimaMensagem == mensagem:
                mensagem = random.choice(MensagemEnviar["MensagensEnviar"])
            # enviar a mensagem para o Meu Numero para poder depois encaminhar


            # clicar na lupa
            navegador.find_element('xpath', '//*[@id="side"]/div[1]/div/div/button/div[2]/span').click()
            navegador.find_element('xpath', '//*[@id="side"]/div[1]/div/div/div[2]/div/div[1]/p').send_keys("você")
            navegador.find_element('xpath', '//*[@id="side"]/div[1]/div/div/div[2]/div/div[1]/p').send_keys(Keys.ENTER)
            time.sleep(1)

            # escrever a mensagem para nós mesmos
            pyperclip.copy(mensagem)
            navegador.find_element('xpath', '//*[@id="main"]/footer/div[1]/div/span[2]/div/div[2]/div[1]/div/div[1]/p').send_keys(Keys.CONTROL + "v")
            navegador.find_element('xpath', '//*[@id="main"]/footer/div[1]/div/span[2]/div/div[2]/div[1]/div/div[1]/p').send_keys(Keys.ENTER)
            time.sleep(2)

            #Informa qual a última mensagem enviada.
            UltimaMensagem = mensagem

            # selecionar a mensagem para enviar e abre a caixa de encaminhar
            lista_elementos = navegador.find_elements('class name', '_2AOIt')
            for item in lista_elementos:
                mensagem = mensagem.replace("\n", "")
                texto = item.text.replace("\n", "")
                if mensagem in texto:
                    elemento = item

            ActionChains(navegador).move_to_element(elemento).perform()
            elemento.find_element('class name', '_3u9t-').click()
            time.sleep(0.5)
            navegador.find_element('xpath', '//*[@id="app"]/div/span[4]/div/ul/div/li[4]/div').click()
            navegador.find_element('xpath', '//*[@id="main"]/span[2]/div/button[4]/span').click()
            time.sleep(1)

            for numEncaminhar in NumerosEncaminhar:
                # selecionar os 5 conttos para enviar
                # escrever o nome do contato
                navegador.find_element('xpath', '//*[@id="app"]/div/span[2]/div/div/div/div/div/div/div/div[1]/div/div/div[2]/div/div[1]/p').send_keys(numEncaminhar)
                time.sleep(1)
                # dar enter
                navegador.find_element('xpath', '//*[@id="app"]/div/span[2]/div/div/div/div/div/div/div/div[1]/div/div/div[2]/div/div[1]/p').send_keys(Keys.ENTER)
                time.sleep(1)
                # apagar o nome do contato
                navegador.find_element('xpath', '//*[@id="app"]/div/span[2]/div/div/div/div/div/div/div/div[1]/div/div/div[2]/div/div[1]/p').send_keys(Keys.BACKSPACE)
                time.sleep(1)

            navegador.find_element('xpath', '//*[@id="app"]/div/span[2]/div/div/div/div/div/div/div/span/div/div/div/span').click()
            time.sleep(3)
            NumerosEncaminhar.clear()

    exibir_mensagem_sucesso('Processo finalizado com sucesso.')

# Função para salvar os números de telefone em um arquivo JSON
def salvar_numeros_telefones(numeros):
    with open("NumerosTelefones.json", "w") as arquivo_json:
        json.dump({"numeros": numeros}, arquivo_json)

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

# Load phone numbers from "NumerosTelefones.json" and insert them into the telefone_text widget
try:
    with open("NumerosTelefones.json", "r") as arquivo_json:
        dados = json.load(arquivo_json)
        if "numeros" in dados:
            numeros = dados["numeros"]
            for numero in numeros:
                telefone_text.insert(tk.END, f"{numero}\n")
except FileNotFoundError:
    pass

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
