import requests
import base64
import time

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