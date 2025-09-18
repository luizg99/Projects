import requests
import base64
import time
import pandas as pd

# Declaração das variáveis globais
link_atualizado_tvs = ''
link_atualizado_uniplay = ''
link_atualizado_bit = ''
link_atualizado_fast = ''

sheet_url_links = "https://docs.google.com/spreadsheets/d/1lMIq91MwJcxuNDZsJaTLdYmj_M2bmLO5_R9Y5EWDEb8/export?format=csv"

# Configuração da API Key do 2Captcha
captcha_api_key = ''
# Função para resolver o CAPTCHA usando 2Captcha
def solve_captcha_with_2captcha(image_path, api_key, captcha_type='base64', **kwargs):
    """
    Resolve CAPTCHAs usando o serviço 2Captcha.

    :param image_path: Caminho para a imagem do CAPTCHA (para base64).
    :param api_key: Chave da API do 2Captcha.
    :param captcha_type: Tipo do CAPTCHA (base64, userrecaptcha, hcaptcha, etc.).
    :param kwargs: Parâmetros adicionais necessários para tipos específicos de CAPTCHA.
    :return: A resposta do CAPTCHA resolvido.
    """
    data = {
        'key': api_key,
        'method': captcha_type,
        'json': 1
    }

    # Para 'base64', envie a imagem em base64
    if captcha_type == 'base64':
        with open(image_path, "rb") as img_file:
            base64_image = base64.b64encode(img_file.read()).decode('utf-8')
            data['body'] = base64_image

    # Adicione parâmetros adicionais (ex.: googlekey, sitekey, etc.)
    data.update(kwargs)

    # Enviar solicitação para 2Captcha
    response = requests.post("http://2captcha.com/in.php", data=data)
    request_result = response.json()

    if request_result.get("status") != 1:
        raise Exception(f"Erro ao enviar CAPTCHA para 2Captcha: {request_result.get('request')}")

    # Pegar o ID do CAPTCHA
    captcha_id = request_result.get("request")

    # Consultar o resultado até ser resolvido
    result_url = f"http://2captcha.com/res.php?key={api_key}&action=get&id={captcha_id}&json=1"
    while True:
        time.sleep(5)  # Esperar 5 segundos entre tentativas
        result_response = requests.get(result_url)
        result_json = result_response.json()

        if result_json.get("status") == 1:
            return result_json.get("request")  # Resultado do CAPTCHA

        if result_json.get("request") != "CAPCHA_NOT_READY":
            raise Exception(f"Erro ao resolver CAPTCHA: {result_json.get('request')}")

# Lê a planilha usando pandas
# Lê a planilha usando pandas
def getGoogleSheetData(url):
    df = pd.read_csv(url, sep=',', quotechar='"', dtype=str).dropna(how="all")  # Remove linhas totalmente vazias
    return df


# Lê a planilha e atualiza os links de acordo com o "Nome painel"
def atualizarLinks():
    global link_atualizado_tvs, link_atualizado_uniplay, link_atualizado_bit, link_atualizado_fast  # Declara que as variáveis são globais


    df = getGoogleSheetData(sheet_url_links)

    # Dicionário para armazenar os links atualizados
    links_atualizados = {}

    # Itera sobre cada linha da planilha
    for index, row in df.iterrows():
        nome_painel = row['Nome painel']
        link = row['Link']

        # Verifica qual variável deve ser atualizada com base no nome do painel
        if nome_painel == 'link_tvs':
            links_atualizados['tvs'] = link
        elif nome_painel == 'link_uniplay':
            links_atualizados['uniplay'] = link
        elif nome_painel == 'link_bit':
            links_atualizados['bit'] = link
        elif nome_painel == 'link_fast':
            links_atualizados['fast'] = link

    # Atualiza as variáveis globais com os links extraídos da planilha e remove espaços
    link_atualizado_tvs = links_atualizados.get('tvs', '').strip()
    link_atualizado_uniplay = links_atualizados.get('uniplay', '').strip()
    link_atualizado_bit = links_atualizados.get('bit', '').strip()
    link_atualizado_fast = links_atualizados.get('fast', '').strip()

    print("Links atualizados com sucesso:")
    print(f"TVS: {link_atualizado_tvs}")
    print(f"Uniplay: {link_atualizado_uniplay}")
    print(f"Bit: {link_atualizado_bit}")
    print(f"Fast: {link_atualizado_fast}")

def obterLinkAtualizado(pServidor: str) -> str:
    if pServidor == 'TVS':
        return link_atualizado_tvs
    elif pServidor == 'UNIPLAY':
        return link_atualizado_uniplay
    elif pServidor == 'BIT':
        return link_atualizado_bit
    elif pServidor == 'FAST':
        return link_atualizado_fast
    else:
        return ""