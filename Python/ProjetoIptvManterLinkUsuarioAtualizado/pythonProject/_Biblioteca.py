import requests
import base64
import time

# Função para resolver o CAPTCHA usando 2Captcha
def solve_captcha_with_2captcha(image_path, api_key):
    # Abrir a imagem e converter para base64
    with open(image_path, "rb") as img_file:
        base64_image = base64.b64encode(img_file.read()).decode('utf-8')

    # Enviar a imagem para o 2Captcha
    response = requests.post("http://2captcha.com/in.php", {
        'key': api_key,
        'method': 'base64',
        'body': base64_image,
        'json': 1
    })
    request_result = response.json()

    if request_result.get("status") != 1:
        raise Exception(f"Erro ao enviar CAPTCHA para 2Captcha: {request_result.get('request')}")

    captcha_id = request_result.get("request")
s