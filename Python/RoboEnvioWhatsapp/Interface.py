import tkinter as tk
import json
from selenium import webdriver
from selenium.common import NoSuchElementException
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
import datetime
import random
import os
import time
import pyperclip
import pandas as pd
import gspread
from oauth2client.service_account import ServiceAccountCredentials

# Variável global para armazenar a janela de consulta de mensagens a enviar
janela_consultar_enviar = None

def getGoogleSheetData(url):
    df = pd.read_csv(url, sep=',', quotechar='"', dtype=str).dropna(how="all")  # Remove linhas totalmente vazias
    return df


def formatar_telefone(telefone):
    """
    Formata o número de telefone, removendo o código do país, DDD, traços e espaços.
    Exemplo: "55 62 8126-5245" -> "6281265245"
    """
    # Remove todos os caracteres não numéricos
    telefone_formatado = ''.join(filter(str.isdigit, telefone))

    # Remove o código do país (55) e o DDD (62)
    if len(telefone_formatado) >= 2:
        telefone_formatado = telefone_formatado[2:]  # Remove os primeiros 4 dígitos (55 + DDD)

    return telefone_formatado


def atualizar_google_sheets(df, sheet_url):
    """
    Atualiza o Google Sheets com as alterações feitas no DataFrame.
    """
    try:
        # Substitui valores NaN por strings vazias
        df = df.fillna("")

        # Configura as credenciais da API do Google Sheets
        scope = [
            "https://spreadsheets.google.com/feeds",
            "https://www.googleapis.com/auth/spreadsheets",
            "https://www.googleapis.com/auth/drive"
        ]
        creds = ServiceAccountCredentials.from_json_keyfile_name('credenciais.json', scope)
        client = gspread.authorize(creds)

        # Abre a planilha pelo URL
        sheet = client.open_by_url(sheet_url).sheet1

        # Atualiza a planilha com os dados do DataFrame
        sheet.update([df.columns.values.tolist()] + df.values.tolist())
    except Exception as e:
        print(f"Erro ao atualizar o Google Sheets: {str(e)}")

def enviar_mensagens_planilha():
    try:
        # Grid 0 é a principal
        vGridId = 0

        # Grid de testes
        # vGridId = 493313803

        sheet_url_clientes = f"https://docs.google.com/spreadsheets/d/1ifSYQKY2W-DA0D0wYY00tKag90Tp5FJP3mdZ0lConUs/export?format=csv&gid={vGridId}"
        # Carregar a planilha
        df = pd.read_csv(sheet_url_clientes)

        # Verificar se as colunas necessárias existem
        if 'Data Vencimento' not in df.columns or 'Data ultimo envio' not in df.columns or 'Telefone' not in df.columns:
            status_label.config(text="Erro: A planilha não contém as colunas necessárias.")
            return

        # Data atual no formato da planilha (exemplo: 18/03/2025)
        data_atual = datetime.datetime.now().strftime('%d/%m/%Y')

        # Iterar sobre as linhas da planilha
        for index, row in df.iterrows():
            telefone = row['Telefone']
            data_vencimento = row['Data Vencimento']  # Pega o valor da coluna "Data Vencimento"
            data_ultimo_envio = row['Data ultimo envio']

            # Verificar se a data de vencimento está vazia ou é nula
            if pd.isna(data_vencimento) or data_vencimento.strip() == "":
                continue  # Pula para a próxima iteração (ou seja, "vaza")

            # Verificar se a data de vencimento é um dia após a data atual
            if data_vencimento == (datetime.datetime.now() + datetime.timedelta(days=1)).strftime('%d/%m/%Y'):
                # Verificar se a data do último envio é menor que a data atual ou está vazia
                if pd.isna(data_ultimo_envio) or data_ultimo_envio < data_atual:
                    # Formatar o número de telefone
                    telefone_formatado = formatar_telefone(telefone)

                    # Enviar a mensagem
                    mensagem = random.choice(MensagensEnviar)  # Escolhe uma mensagem aleatória
                    enviar_mensagem(telefone_formatado, mensagem)

                    # Atualizar a coluna "data ultimo envio" com a data atual
                    df.at[index, 'Data ultimo envio'] = data_atual

        # Salvar a planilha atualizada no Google Sheets
        atualizar_google_sheets(df, sheet_url_clientes)
        status_label.config(text="Mensagens enviadas e planilha atualizada com sucesso!")
    except Exception as e:
        status_label.config(text=f"Erro: {str(e)}")


# Função para enviar a mensagem via WhatsApp
def enviar_mensagem(telefone, mensagem):
    try:
        service = Service(ChromeDriverManager().install())
        navegador = webdriver.Chrome(service=service)
        navegador.get("https://web.whatsapp.com")

        # Esperando o usuário logar
        while True:
            try:
                navegador.find_element('id', 'side')
                break  # Sai do loop se o elemento 'side' for encontrado
            except NoSuchElementException:
                time.sleep(1)

        link = f"https://web.whatsapp.com/send?phone=55{telefone}&text={mensagem}"
        navegador.get(link)

        while True:
            try:
                navegador.find_element('id', 'side')
                break  # Sai do loop se o elemento 'side' for encontrado
            except NoSuchElementException:
                time.sleep(1)

        time.sleep(4)
        navegador.find_element('xpath', '//*[@id="main"]/footer/div[1]/div/span/div/div[2]/div[1]/div[2]/div/p').send_keys(Keys.ENTER)
        time.sleep(3)
        navegador.close()
    except Exception as e:
        status_label.config(text=f"Erro ao enviar mensagem para {telefone}: {str(e)}")

# Função para abrir a janela de cadastro de mensagens de saudação
def abrir_janela_cadastro_saudacao():
    janela_cadastro_saudacao = tk.Toplevel(janela_principal)
    janela_cadastro_saudacao.title("Cadastro de Mensagens de Saudação")

    mensagem_saudacao_label = tk.Label(janela_cadastro_saudacao, text="Insira as mensagens de saudação (uma por linha):")
    mensagem_saudacao_label.pack()

    mensagem_saudacao_entry = tk.Text(janela_cadastro_saudacao, height=10, width=30)
    mensagem_saudacao_entry.pack()

    # Preenche a caixa de texto com as mensagens de saudação cadastradas, se houver
    if MensagensSaudacao:
        mensagem_saudacao_entry.insert("1.0", "\n".join(MensagensSaudacao))

    cadastrar_button = tk.Button(janela_cadastro_saudacao, text="Cadastrar",
                                 command=lambda: cadastrar_mensagens_saudacao(mensagem_saudacao_entry))
    cadastrar_button.pack()

    janela_cadastro_saudacao.transient(janela_principal)  # Define a janela de cadastro como janela filha da janela principal
    janela_cadastro_saudacao.grab_set()  # Impede que a janela principal seja clicada enquanto a janela de cadastro estiver aberta

# Função para cadastrar mensagens de saudação
def cadastrar_mensagens_saudacao(mensagem_saudacao_entry):
    mensagens = mensagem_saudacao_entry.get("1.0", "end-1c").split('\n')
    mensagens = [msg.strip() for msg in mensagens if msg.strip()]

    if mensagens:
        MensagensSaudacao.clear()
        MensagensSaudacao.extend(mensagens)
        status_label.config(text="Mensagens de saudação cadastradas com sucesso!")
        salvar_mensagens_saudacao()

# Função para salvar as mensagens de saudação em um arquivo JSON
def salvar_mensagens_saudacao():
    with open("MensagensSaudacao.json", "w", encoding="utf-8") as arquivo_json:
        json.dump({"MensagensSaudacao": MensagensSaudacao}, arquivo_json, ensure_ascii=False)

# Função para abrir a janela de cadastro de mensagens a enviar
def abrir_janela_cadastro_enviar():
    global janela_cadastro_enviar
    janela_cadastro_enviar = tk.Toplevel(janela_principal)
    janela_cadastro_enviar.title("Cadastro de Mensagens a Enviar")

    mensagem_enviar_label = tk.Label(janela_cadastro_enviar, text="Insira a mensagem a enviar:")
    mensagem_enviar_label.pack()

    mensagem_enviar_entry = tk.Text(janela_cadastro_enviar, height=10, width=30)
    mensagem_enviar_entry.pack()

    cadastrar_button = tk.Button(janela_cadastro_enviar, text="Cadastrar",
                                 command=lambda: cadastrar_mensagem_enviar(mensagem_enviar_entry))
    cadastrar_button.pack()

    janela_cadastro_enviar.transient(janela_principal)  # Define a janela de cadastro como janela filha da janela principal
    janela_cadastro_enviar.grab_set()  # Impede que a janela principal seja clicada enquanto a janela de cadastro estiver aberta

# Função para cadastrar mensagem a enviar
def cadastrar_mensagem_enviar(mensagem_enviar_entry):
    mensagem = mensagem_enviar_entry.get("1.0", "end-1c")

    if mensagem:
        MensagensEnviar.append(mensagem)
        salvar_mensagens_enviar()
        status_label.config(text="Mensagem cadastrada com sucesso!")

    janela_cadastro_enviar.destroy()

# Função para salvar as mensagens a enviar em um arquivo JSON
def salvar_mensagens_enviar():
    with open("MensagensEnviar.json", "w", encoding="utf-8") as arquivo_json:
        json.dump({"MensagensEnviar": MensagensEnviar}, arquivo_json, ensure_ascii=False)

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

# Cria um botão para iniciar o envio de mensagens com base na planilha
iniciar_envio_button = tk.Button(janela_principal, text="Iniciar Envio", command=enviar_mensagens_planilha)
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