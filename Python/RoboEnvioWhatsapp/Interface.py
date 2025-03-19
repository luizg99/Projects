import tkinter as tk
from tkinter import ttk
import json
from selenium import webdriver
from selenium.common import NoSuchElementException
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
import datetime
import random
from selenium.webdriver import ActionChains
import time
import pyperclip
import pandas as pd
import gspread
from oauth2client.service_account import ServiceAccountCredentials

# Variáveis globais
MensagensSaudacao = []
MensagensEnviar = []

def get_chrome_driver():
    service = Service(ChromeDriverManager().install())
    options = webdriver.ChromeOptions()
    options.add_argument(r"user-data-dir=C:\WhatsAppProfile")
    return webdriver.Chrome(service=service, options=options)

# Função para carregar mensagens de saudação
def carregar_mensagens_saudacao():
    global MensagensSaudacao
    try:
        with open("MensagensSaudacao.json", "r") as arquivo_json:
            dados = json.load(arquivo_json)
            if "MensagensSaudacao" in dados:
                MensagensSaudacao = dados["MensagensSaudacao"]
    except FileNotFoundError:
        MensagensSaudacao = []

# Função para carregar mensagens a enviar
def carregar_mensagens_enviar():
    global MensagensEnviar
    try:
        with open("MensagensEnviar.json", "r") as arquivo_json:
            dados = json.load(arquivo_json)
            if "MensagensEnviar" in dados:
                MensagensEnviar = dados["MensagensEnviar"]
    except FileNotFoundError:
        MensagensEnviar = []

# Função para salvar mensagens de saudação
def salvar_mensagens_saudacao():
    with open("MensagensSaudacao.json", "w", encoding="utf-8") as arquivo_json:
        json.dump({"MensagensSaudacao": MensagensSaudacao}, arquivo_json, ensure_ascii=False)

# Função para salvar mensagens a enviar
def salvar_mensagens_enviar():
    with open("MensagensEnviar.json", "w", encoding="utf-8") as arquivo_json:
        json.dump({"MensagensEnviar": MensagensEnviar}, arquivo_json, ensure_ascii=False)

# Função para ler JSON
def ler_json(filename):
    with open(filename, 'r', encoding='utf-8-sig') as file:
        data = file.read()
        return json.loads(data)

# Função para formatar número de telefone
def format_phone_number(phone_number):
    digits = ''.join(filter(str.isdigit, phone_number))
    if len(digits) == 10:
        return f'{digits[2:]}'
    elif len(digits) == 11:
        return f'{digits[3:]}'
    return None

# Função para salvar números de telefone
def salvar_numeros_telefones(numeros):
    with open("NumerosTelefones.json", "w") as arquivo_json:
        json.dump({"numeros": numeros}, arquivo_json)

# Função para exibir mensagem de sucesso
def exibir_mensagem_sucesso(mensagem):
    janela_sucesso = tk.Toplevel()
    janela_sucesso.title("Sucesso")
    label_sucesso = tk.Label(janela_sucesso, text=mensagem)
    label_sucesso.pack()
    fechar_button = tk.Button(janela_sucesso, text="Fechar", command=janela_sucesso.destroy)
    fechar_button.pack()

# Função para abrir a janela de edição de mensagem
def abrir_janela_edicao(index, mensagem):
    janela_edicao = tk.Toplevel()
    janela_edicao.title("Editar Mensagem")
    mensagem_editar_entry = tk.Text(janela_edicao, height=10, width=30)
    mensagem_editar_entry.pack()
    mensagem_editar_entry.insert("1.0", mensagem)

    def finalizar_edicao():
        nova_mensagem = mensagem_editar_entry.get("1.0", "end-1c")
        MensagensEnviar[index] = nova_mensagem
        salvar_mensagens_enviar()
        janela_edicao.destroy()
        abrir_janela_consultar_enviar()

    finalizar_button = tk.Button(janela_edicao, text="Finalizar Alteração", command=finalizar_edicao)
    finalizar_button.pack()

# Função para abrir a janela de consulta de mensagens a enviar
def abrir_janela_consultar_enviar():
    janela_consultar_enviar = tk.Toplevel()
    janela_consultar_enviar.title("Consultar Mensagens Enviar")
    lista_mensagens_enviar = tk.Listbox(janela_consultar_enviar, height=10, width=40)
    lista_mensagens_enviar.pack()

    for mensagem in MensagensEnviar:
        lista_mensagens_enviar.insert(tk.END, mensagem)

    def excluir_mensagem_selecionada():
        selecionados = lista_mensagens_enviar.curselection()
        for index in selecionados[::-1]:
            lista_mensagens_enviar.delete(index)
            del MensagensEnviar[index]
        salvar_mensagens_enviar()

    def editar_mensagem_selecionada():
        selecionados = lista_mensagens_enviar.curselection()
        if selecionados:
            index = selecionados[0]
            mensagem_selecionada = MensagensEnviar[index]
            abrir_janela_edicao(index, mensagem_selecionada)

    excluir_button = tk.Button(janela_consultar_enviar, text="Excluir Mensagem Selecionada", command=excluir_mensagem_selecionada)
    excluir_button.pack()

    editar_button = tk.Button(janela_consultar_enviar, text="Editar Mensagem Selecionada", command=editar_mensagem_selecionada)
    editar_button.pack()

# Função para abrir a janela de cadastro de mensagens de saudação
def abrir_janela_cadastro_saudacao():
    janela_cadastro_saudacao = tk.Toplevel()
    janela_cadastro_saudacao.title("Cadastro de Mensagens de Saudação")
    mensagem_saudacao_label = tk.Label(janela_cadastro_saudacao, text="Insira as mensagens de saudação (uma por linha):")
    mensagem_saudacao_label.pack()

    mensagem_saudacao_entry = tk.Text(janela_cadastro_saudacao, height=10, width=30)
    mensagem_saudacao_entry.pack()

    if MensagensSaudacao:
        mensagem_saudacao_entry.insert("1.0", "\n".join(MensagensSaudacao))

    def cadastrar_mensagens_saudacao():
        mensagens = mensagem_saudacao_entry.get("1.0", "end-1c").split('\n')
        mensagens = [msg.strip() for msg in mensagens if msg.strip()]
        if mensagens:
            MensagensSaudacao.clear()
            MensagensSaudacao.extend(mensagens)
            salvar_mensagens_saudacao()
            exibir_mensagem_sucesso("Mensagens de saudação cadastradas com sucesso!")

    cadastrar_button = tk.Button(janela_cadastro_saudacao, text="Cadastrar", command=cadastrar_mensagens_saudacao)
    cadastrar_button.pack()

# Função para abrir a janela de cadastro de mensagens a enviar
def abrir_janela_cadastro_enviar():
    janela_cadastro_enviar = tk.Toplevel()
    janela_cadastro_enviar.title("Cadastro de Mensagens a Enviar")
    mensagem_enviar_label = tk.Label(janela_cadastro_enviar, text="Insira a mensagem a enviar:")
    mensagem_enviar_label.pack()

    mensagem_enviar_entry = tk.Text(janela_cadastro_enviar, height=10, width=30)
    mensagem_enviar_entry.pack()

    def cadastrar_mensagem_enviar():
        mensagem = mensagem_enviar_entry.get("1.0", "end-1c")
        if mensagem:
            MensagensEnviar.append(mensagem)
            salvar_mensagens_enviar()
            exibir_mensagem_sucesso("Mensagem a enviar cadastrada com sucesso!")

    cadastrar_button = tk.Button(janela_cadastro_enviar, text="Cadastrar", command=cadastrar_mensagem_enviar)
    cadastrar_button.pack()

# Função para salvar números a enviar
def Salvar_numeros_a_enviar(telefone_text):
    numeros = telefone_text.get("1.0", "end-1c").split('\n')
    numeros = [numero.strip() for numero in numeros if numero.strip()]
    if numeros:
        salvar_numeros_telefones(numeros)

def iniciar_envio_numeros(numeros_entry):
    # Salva os números digitados
    numeros = numeros_entry.get("1.0", "end-1c").split('\n')
    numeros = [numero.strip() for numero in numeros if numero.strip()]
    if numeros:
        salvar_numeros_telefones(numeros)
    else:
        exibir_mensagem_sucesso("Nenhum número informado!")
        return

    # Carrega os dados dos arquivos JSON
    numerosArray = ler_json('NumerosTelefones.json')
    Saudacao = ler_json('MensagensSaudacao.json')
    MensagemEnviar = ler_json('MensagensEnviar.json')

    # Abre o navegador utilizando a função que salva a sessão
    navegador = get_chrome_driver()
    navegador.get("https://web.whatsapp.com")

    # Aguarda o login (caso a sessão não esteja salva ainda)
    while True:
        try:
            navegador.find_element('id', 'side')
            break
        except NoSuchElementException:
            time.sleep(1)

    count = 0
    NumerosEncaminhar = []
    UltimaMensagem = ''

    for i, numero in enumerate(numerosArray['numeros']):
        try:
            tempoEsperar = random.randint(5, 15)
            time.sleep(tempoEsperar)
            mensagemSaudacao = random.choice(Saudacao["MensagensSaudacao"])
            link = f"https://web.whatsapp.com/send?phone=55{numero}&text={mensagemSaudacao}"
            navegador.get(link)

            count += 1

            while True:
                try:
                    navegador.find_element('id', 'side')
                    break
                except NoSuchElementException:
                    time.sleep(1)
            time.sleep(4)
            navegador.find_element('xpath','//*[@id="main"]/footer/div[1]/div/span[2]/div/div[2]/div[1]/div/div[1]/p') \
                     .send_keys(Keys.ENTER)
            time.sleep(3)
        except Exception:
            print(f'O número: {numero} é inválido, nenhuma mensagem foi enviada. {datetime.date.today()}')
            if i < len(numerosArray['numeros']) - 1:
                continue

        NumerosEncaminhar.append(numero)

        if count >= 5:
            mensagem = random.choice(MensagemEnviar["MensagensEnviar"])
            while UltimaMensagem == mensagem:
                mensagem = random.choice(MensagemEnviar["MensagensEnviar"])

            navegador.get("https://web.whatsapp.com")
            while True:
                try:
                    navegador.find_element('id', 'side')
                    break
                except NoSuchElementException:
                    time.sleep(1)
            time.sleep(4)
            navegador.find_element('xpath', '//*[@id="side"]/div[1]/div/div/button/div[2]/span').click()
            navegador.find_element('xpath', '//*[@id="side"]/div[1]/div/div/div[2]/div/div[1]/p') \
                     .send_keys("você")
            navegador.find_element('xpath', '//*[@id="side"]/div[1]/div/div/div[2]/div/div[1]/p') \
                     .send_keys(Keys.ENTER)
            time.sleep(1)
            pyperclip.copy(mensagem)
            navegador.find_element('xpath', '//*[@id="main"]/footer/div[1]/div/span[2]/div/div[2]/div[1]/div/div[1]/p') \
                     .send_keys(Keys.CONTROL + "v")
            navegador.find_element('xpath', '//*[@id="main"]/footer/div[1]/div/span[2]/div/div[2]/div[1]/div/div[1]/p') \
                     .send_keys(Keys.ENTER)
            time.sleep(2)
            UltimaMensagem = mensagem

            lista_elementos = navegador.find_elements('class name', '_2AOIt')
            elemento = None
            for item in lista_elementos:
                msg_sem_quebra = mensagem.replace("\n", "")
                texto = item.text.replace("\n", "")
                if msg_sem_quebra in texto:
                    elemento = item
                    break

            if elemento:
                ActionChains(navegador).move_to_element(elemento).perform()
                elemento.find_element('class name', '_3u9t-').click()
                time.sleep(0.5)
                navegador.find_element('xpath', '//*[@id="app"]/div/span[4]/div/ul/div/li[4]/div').click()
                navegador.find_element('xpath', '//*[@id="main"]/span[2]/div/button[5]/span').click()
                time.sleep(1)
                if NumerosEncaminhar:
                    for numEncaminhar in NumerosEncaminhar:
                        numEncaminhar = format_phone_number(numEncaminhar)
                        navegador.find_element('xpath', '//*[@id="app"]/div/span[2]/div/div/div/div/div/div/div/div[1]/div/div/div[2]/div/div[1]/p') \
                                 .send_keys(numEncaminhar)
                        time.sleep(1)
                        navegador.find_element('xpath', '//*[@id="app"]/div/span[2]/div/div/div/div/div/div/div/div[1]/div/div/div[2]/div/div[1]/p') \
                                 .send_keys(Keys.ENTER)
                        time.sleep(1)
                        navegador.find_element('xpath','//*[@id="app"]/div/span[2]/div/div/div/div/div/div/div/div[1]/div/div/div[2]/div/div[1]/p') \
                                 .send_keys(Keys.CONTROL + 'a')
                        time.sleep(1)
                        navegador.find_element('xpath', '//*[@id="app"]/div/span[2]/div/div/div/div/div/div/div/div[1]/div/div/div[2]/div/div[1]/p') \
                                 .send_keys(Keys.BACKSPACE)
                        time.sleep(1)
                    navegador.find_element('xpath', '//*[@id="app"]/div/span[2]/div/div/div/div/div/div/div/span/div/div/div/span') \
                             .click()
                    time.sleep(3)
                    NumerosEncaminhar.clear()
                    count = 0

    exibir_mensagem_sucesso('Processo finalizado com sucesso.')
    navegador.close()

# Função para obter dados do Google Sheets
def getGoogleSheetData(url):
    df = pd.read_csv(url, sep=',', quotechar='"', dtype=str).dropna(how="all")
    return df

# Função para formatar telefone
def formatar_telefone(telefone):
    telefone_formatado = ''.join(filter(str.isdigit, telefone))
    if len(telefone_formatado) >= 2:
        telefone_formatado = telefone_formatado[2:]
    return telefone_formatado

def atualizar_google_sheets(df, sheet_url):
    try:
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

        # Substitui valores NaN por None (ou string vazia)
        df = df.where(pd.notna(df), None)  # Substitui NaN por None

        # Obtém o índice da coluna 'Data ultimo envio' no DataFrame
        coluna_nome = 'Data ultimo envio'
        if coluna_nome not in df.columns:
            raise ValueError(f"A coluna '{coluna_nome}' não foi encontrada no DataFrame.")

        # Obtém a letra da coluna correspondente no Google Sheets
        coluna_index = df.columns.get_loc(coluna_nome)  # Índice da coluna no DataFrame
        coluna_letra = chr(ord('A') + coluna_index)  # Converte o índice para letra (A, B, C, ...)

        # Atualiza apenas a coluna 'Data ultimo envio'
        coluna_data = df[coluna_nome].tolist()

        # Define o intervalo de atualização (ex: 'B2:B' + número de linhas)
        intervalo = f"{coluna_letra}2:{coluna_letra}{len(coluna_data) + 1}"

        # Nova convenção de argumentos: valores primeiro, intervalo segundo
        sheet.update(values=[[valor] for valor in coluna_data], range_name=intervalo)
    except Exception as e:
        print(f"Erro ao atualizar o Google Sheets: {str(e)}")

def enviar_mensagens_planilha():
    try:
        # Grid 0 é a principal
        vGridId = 0

        sheet_url_clientes = (
            f"https://docs.google.com/spreadsheets/d/1ifSYQKY2W-DA0D0wYY00tKag90Tp5FJP3mdZ0lConUs/export?format=csv&gid={vGridId}"
        )
        # Carregar a planilha em um DataFrame
        df = pd.read_csv(sheet_url_clientes)

        # Verificar se as colunas necessárias existem
        colunas_necessarias = ["Data vencimento", "Data ultimo envio", "Telefone"]
        for col in colunas_necessarias:
            if col not in df.columns:
                print(f"Erro: A planilha não contém a coluna '{col}'.")
                return

        # Data atual (string e objeto datetime)
        data_atual_str = datetime.datetime.now().strftime('%d/%m/%Y')
        data_atual_date = datetime.datetime.strptime(data_atual_str, '%d/%m/%Y')

        for index, row in df.iterrows():
            telefone = row["Telefone"]
            data_vencimento = row["Data vencimento"]
            data_ultimo_envio = row["Data ultimo envio"]

            # 1) Se telefone está vazio, pula
            if pd.isna(telefone) or telefone.strip() == "":
                continue

            # 2) Se data de vencimento está vazia, pula
            if pd.isna(data_vencimento) or data_vencimento.strip() == "":
                continue

            # Converte a data de vencimento em objeto datetime
            try:
                data_vencimento_date = datetime.datetime.strptime(data_vencimento.strip(), '%d/%m/%Y')
            except ValueError:
                # Se não conseguir converter, pula
                continue

            # ------------------------------------------------------------
            # SITUAÇÃO 1: Vencimento é amanhã => manda lembrete
            # ------------------------------------------------------------
            if data_vencimento.strip() == (data_atual_date + datetime.timedelta(days=1)).strftime('%d/%m/%Y'):
                # Se data_ultimo_envio estiver vazio ou for menor que data_atual, enviar
                if pd.isna(data_ultimo_envio) or data_ultimo_envio.strip() == "":
                    enviar_novamente = True
                else:
                    try:
                        data_ultimo_envio_date = datetime.datetime.strptime(data_ultimo_envio.strip(), '%d/%m/%Y')
                        enviar_novamente = (data_ultimo_envio_date < data_atual_date)
                    except ValueError:
                        enviar_novamente = True

                if enviar_novamente:
                    telefone_formatado = formatar_telefone(telefone)
                    # Define a mensagem fixa substituindo [data] pela data de vencimento
                    mensagem = f"Caro cliente, estamos passando para informar que sua assinatura vence em {data_vencimento.strip()}. Para evitar interrupções no serviço 📺🎬📽, pedimos que realize a renovação dentro do prazo. Qualquer dúvida, estamos à disposição! 😊\n\nObs: Caso não queira receber lembretes de vencimento digite 'Não receber'."
                    enviar_mensagem(telefone_formatado, mensagem, df, index, sheet_url_clientes)
                    df.at[index, "Data ultimo envio"] = data_atual_str

            # ------------------------------------------------------------
            # SITUAÇÃO 2: Cliente já está vencido (data_vencimento < hoje)
            # => reenviar mensagem se passaram 7 dias desde a última cobrança
            # ------------------------------------------------------------
            elif data_vencimento_date < data_atual_date:
                if pd.isna(data_ultimo_envio) or data_ultimo_envio.strip() == "":
                    telefone_formatado = formatar_telefone(telefone)
                    # Define a mensagem fixa para cliente vencido, usando também a data de vencimento
                    mensagem = f"Caro cliente, estamos passando para informar que sua assinatura vence em {data_vencimento.strip()}. Para evitar interrupções no serviço 📺🎬📽, pedimos que realize a renovação dentro do prazo. Qualquer dúvida, estamos à disposição! 😊\n\nObs: Caso não queira receber lembretes de vencimento digite 'Não receber'."
                    enviar_mensagem(telefone_formatado, mensagem, df, index, sheet_url_clientes)
                    df.at[index, "Data ultimo envio"] = data_atual_str
                else:
                    try:
                        data_ultimo_envio_date = datetime.datetime.strptime(data_ultimo_envio.strip(), '%d/%m/%Y')
                        diferenca_dias = (data_atual_date - data_ultimo_envio_date).days

                        if diferenca_dias >= 7:
                            telefone_formatado = formatar_telefone(telefone)
                            mensagem = f"Caro cliente, estamos passando para informar que sua assinatura vence em {data_vencimento.strip()}. Para evitar interrupções no serviço 📺🎬📽, pedimos que realize a renovação dentro do prazo. Qualquer dúvida, estamos à disposição! 😊\n\nObs: Caso não queira receber lembretes de vencimento digite 'Não receber'."
                            enviar_mensagem(telefone_formatado, mensagem, df, index, sheet_url_clientes)
                            df.at[index, "Data ultimo envio"] = data_atual_str
                    except ValueError:
                        telefone_formatado = formatar_telefone(telefone)
                        mensagem = f"Caro cliente, estamos passando para informar que sua assinatura vence em {data_vencimento.strip()}. Para evitar interrupções no serviço 📺🎬📽, pedimos que realize a renovação dentro do prazo. Qualquer dúvida, estamos à disposição! 😊\n\nObs: Caso não queira receber lembretes de vencimento digite 'Não receber'."
                        enviar_mensagem(telefone_formatado, mensagem, df, index, sheet_url_clientes)
                        df.at[index, "Data ultimo envio"] = data_atual_str

    except Exception as e:
        print(f"Erro: {str(e)}")

def enviar_mensagem(telefone, mensagem, df, index, sheet_url_clientes):
    try:
        # Data atual no formato da planilha (exemplo: 18/03/2025)
        data_atual = datetime.datetime.now().strftime('%d/%m/%Y')

        navegador = get_chrome_driver()
        navegador.get("https://web.whatsapp.com")
        # Aguarda até que o elemento com id "side" esteja disponível (indicando que o WhatsApp carregou)
        while True:
            try:
                navegador.find_element('id', 'side')
                break
            except NoSuchElementException:
                time.sleep(1)
        link = f"https://web.whatsapp.com/send?phone=55{telefone}&text={mensagem}"
        navegador.get(link)
        while True:
            try:
                navegador.find_element('id', 'side')
                break
            except NoSuchElementException:
                time.sleep(1)
        time.sleep(4)
        navegador.find_element('xpath', '//*[@id="main"]/footer/div[1]/div/span/div/div[2]/div[1]/div[2]/div/p').send_keys(Keys.ENTER)
        time.sleep(3)
        navegador.close()

        # Atualizar a coluna "Data ultimo envio" com a data atual
        df.at[index, 'Data ultimo envio'] = data_atual

        # Salvar a planilha atualizada no Google Sheets
        atualizar_google_sheets(df, sheet_url_clientes)
        print(f"Mensagens enviadas e planilha atualizada com sucesso!")
    except Exception as e:
        exibir_mensagem_sucesso(f"Erro ao enviar mensagem para {telefone}: {str(e)}")

# --- Novas funções para atualizar data de vencimento com base no texto colado ---

def atualizar_data_vencimento_usuario(usuario, nova_data):
    try:
        scope = [
            "https://spreadsheets.google.com/feeds",
            "https://www.googleapis.com/auth/spreadsheets",
            "https://www.googleapis.com/auth/drive"
        ]
        creds = ServiceAccountCredentials.from_json_keyfile_name('credenciais.json', scope)
        client = gspread.authorize(creds)
        # Utilize a URL base da planilha (remova a parte de exportação)
        sheet_url = "https://docs.google.com/spreadsheets/d/1ifSYQKY2W-DA0D0wYY00tKag90Tp5FJP3mdZ0lConUs/edit"
        sheet = client.open_by_url(sheet_url).sheet1

        # Procura o usuário na coluna "Usuário"
        cell = sheet.find(usuario)
        if cell is None:
            print("Usuário não encontrado.")
            return False
        row = cell.row

        # Procura a coluna "Data vencimento" no cabeçalho
        header_cell = sheet.find("Data vencimento")
        if header_cell is None:
            print("Coluna 'Data vencimento' não encontrada.")
            return False
        col = header_cell.col

        # Atualiza a célula com a nova data
        sheet.update_cell(row, col, nova_data)
        return True
    except Exception as e:
        print(f"Erro ao atualizar a data de vencimento: {e}")
        return False


def processar_texto_e_atualizar(texto):
    linhas = texto.splitlines()
    usuario = None
    nova_data = None

    # Percorre o array de strings para identificar as informações
    for linha in linhas:
        if "Usuário:" in linha and usuario is None:
            usuario = linha.split("Usuário:")[1].strip()
        if "O novo vencimento é:" in linha and nova_data is None:
            # Extrai o texto completo após o marcador
            nova_data_full = linha.split("O novo vencimento é:")[1].strip()
            # Pega apenas a data (primeiro elemento antes do espaço)
            nova_data = nova_data_full.split()[0]

    # Verifica se o usuário foi encontrado
    if not usuario:
        return "Usuário não encontrado no texto. Não foi possível atualizar."

    # Verifica se uma data foi encontrada
    if not nova_data:
        return "Data de vencimento não encontrada no texto. Não foi possível atualizar."

    # Verifica se a data é válida (formato DD/MM/YYYY)
    try:
        datetime.datetime.strptime(nova_data, '%d/%m/%Y')
    except ValueError:
        return "Data de vencimento inválida. Certifique-se de que a data está no formato DD/MM/YYYY."

    # Atualiza a data de vencimento no Google Sheets
    atualizado = atualizar_data_vencimento_usuario(usuario, nova_data)
    if atualizado:
        return f"Data de vencimento do usuário {usuario} atualizada para {nova_data} com sucesso!"
    else:
        return "Falha ao atualizar a data de vencimento. Verifique os logs."

# Página de Atualização de Data de Vencimento do Cliente
class PaginaAtualizarDataVencimentoCliente(tk.Frame):
    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        self.controller = controller

        label_instrucao = tk.Label(self, text="Cole o texto contendo os dados do usuário e a nova data de vencimento:")
        label_instrucao.pack()

        self.texto_entry = tk.Text(self, height=10, width=40)
        self.texto_entry.pack()

        atualizar_button = tk.Button(self, text="Atualizar data de vencimento", command=self.atualizar_data)
        atualizar_button.pack()

    def atualizar_data(self):
        texto = self.texto_entry.get("1.0", "end-1c")
        resultado = processar_texto_e_atualizar(texto)
        exibir_mensagem_sucesso(resultado)

# --- Páginas já existentes ---

# Página de Cadastro
class PaginaCadastro(tk.Frame):
    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        self.controller = controller

        cadastrar_saudacao_button = tk.Button(self, text="Cadastrar Mensagem Saudação", command=abrir_janela_cadastro_saudacao)
        cadastrar_saudacao_button.pack()

        cadastrar_enviar_button = tk.Button(self, text="Cadastrar Mensagem a Enviar", command=abrir_janela_cadastro_enviar)
        cadastrar_enviar_button.pack()

        consultar_enviar_button = tk.Button(self, text="Consultar Mensagens Enviar", command=abrir_janela_consultar_enviar)
        consultar_enviar_button.pack()

# Página de Envio Padrão
class PaginaEnvioPadrao(tk.Frame):
    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        self.controller = controller

        numeros_label = tk.Label(self, text="Digite os números de telefone (um por linha):")
        numeros_label.pack()

        self.numeros_entry = tk.Text(self, height=10, width=30)
        self.numeros_entry.pack()

        # Load phone numbers from "NumerosTelefones.json" and insert them into the telefone_text widget
        try:
            with open("NumerosTelefones.json", "r") as arquivo_json:
                dados = json.load(arquivo_json)
                if "numeros" in dados:
                    numeros = dados["numeros"]
                    for numero in numeros:
                        self.numeros_entry.insert(tk.END, f"{numero}\n")
        except FileNotFoundError:
            pass

        iniciar_envio_button = tk.Button(self, text="Iniciar Envio Padrão", command=lambda: iniciar_envio_numeros(self.numeros_entry))
        iniciar_envio_button.pack()

# Página de Lembrete Vencimento
class PaginaLembreteVencimento(tk.Frame):
    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        self.controller = controller

        iniciar_lembrete_button = tk.Button(self, text="Iniciar Envio de Lembretes", command=enviar_mensagens_planilha)
        iniciar_lembrete_button.pack()

# Classe principal da aplicação
class App(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Interface Principal")
        self.geometry("600x400")

        carregar_mensagens_saudacao()
        carregar_mensagens_enviar()

        self.notebook = ttk.Notebook(self)
        self.notebook.pack(fill="both", expand=True)

        self.pagina_principal = tk.Frame(self.notebook)
        self.pagina_cadastro = PaginaCadastro(self.notebook, self)
        self.pagina_envio_padrao = PaginaEnvioPadrao(self.notebook, self)
        self.pagina_lembrete_vencimento = PaginaLembreteVencimento(self.notebook, self)
        self.pagina_atualizar_data_vencimento = PaginaAtualizarDataVencimentoCliente(self.notebook, self)

        self.notebook.add(self.pagina_principal, text="Principal")
        self.notebook.add(self.pagina_cadastro, text="Cadastro")
        self.notebook.add(self.pagina_envio_padrao, text="Envio Padrão")
        self.notebook.add(self.pagina_lembrete_vencimento, text="Lembrete Vencimento")
        self.notebook.add(self.pagina_atualizar_data_vencimento, text="Atualizar data vencimento cliente")

        self.status_label = tk.Label(self, text="", bd=1, relief=tk.SUNKEN, anchor=tk.W)
        self.status_label.pack(side=tk.BOTTOM, fill=tk.X)

# Iniciar a aplicação
if __name__ == "__main__":
    app = App()
    app.mainloop()
