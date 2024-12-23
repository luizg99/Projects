from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from PIL import Image
import time
import os
from io import BytesIO
import _Biblioteca as lib

# Configuração da API Key do 2Captcha
captcha_api_key = "0439b8069b74afca88f8062c8eb51716"

link_atualizado_tvs = 'http://grianze.com'
link_atualizado_uniplay = 'http://grianze.com'
link_atualizado_bit = 'http://play.biturl.vip'

def processar_cliente(mac_address, device_key, servidor, driver, tentativas=3):
    tentativa_atual = 1

    while tentativa_atual < tentativas:
        try:
            driver.get('https://iboplayer.com/device/login')

            # Esperar o campo "max-address" existir
            mac_address_field = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located((By.ID, "max-address"))
            )
            # Enviar o texto (MAC Address)
            mac_address_field.send_keys(mac_address)
            # Enviar o texto (device key)
            driver.find_element(By.ID, "device-key").send_keys(device_key)

            time.sleep(1)

            # Scrollar para baixo
            driver.execute_script("window.scrollBy(0, 800);")
            time.sleep(2)

            # Localizar e resolver o CAPTCHA
            captcha_element = driver.find_element(By.XPATH, '/html/body/div/main/div/form/div[4]')
            captcha_png = captcha_element.screenshot_as_png
            captcha_image = Image.open(BytesIO(captcha_png))

            # Recortar apenas o CAPTCHA, eliminando a parte superior com o texto "Captcha"
            width, height = captcha_image.size
            top_offset = int(height * 0.1)
            captcha_cropped = captcha_image.crop((0, top_offset, width, height))

            #Salva a imagem do captcha em documentos para ter uma ideia de como está sendo enviado a imagem...
            processed_path = os.path.join(os.path.expanduser("~"), "Documents", "captcha_black_and_white.png")
            captcha_cropped.save(processed_path)

            captcha_text = lib.solve_captcha_with_2captcha(processed_path, captcha_api_key, captcha_type = 'base64')
            driver.find_element(By.XPATH, "//label[text()='Captcha']/following-sibling::input").send_keys(captcha_text)

            # Clicar no botão de login
            driver.find_element(By.XPATH, '/html/body/div/main/div/form/button').click()

            # Esperar o elemento SVG existir
            svg_element = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located(
                    (By.XPATH, "//*[name()='svg' and contains(@class, 'text-blue-500') and contains(@class, 'cursor-pointer')]")
                )
            )
            # Clicar no elemento SVG
            svg_element.click()

            # Substituir o valor do campo "Host"
            # Espera até 10 segundos para que o elemento exista no DOM
            host_input = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located((By.XPATH, '//*[@id="host"]'))
            )

            host_input.clear()

            if servidor == 'TVS':
                host_input.send_keys(link_atualizado_tvs)
            if servidor == 'UNIPLAY':
                host_input.send_keys(link_atualizado_uniplay)
            if servidor == 'BIT':
                host_input.send_keys(link_atualizado_bit)

            # Salvar
            driver.find_element(By.XPATH, '//*[@id="root"]/main/div/section/form/div[2]/button').click()
            time.sleep(5)

            driver.execute_script("window.scrollBy(0, 800);")
            time.sleep(2)

            # Logout
            # Esperar até que o botão de logout exista e seja clicável
            logout_button = WebDriverWait(driver, 10).until(
                EC.element_to_be_clickable((By.XPATH, '//*[@id="root"]/main/aside/a[7]'))
            )
            # Clicar no botão de logout
            logout_button.click()

            time.sleep(3)
            print(f"Processamento concluído para MAC: {mac_address}" + f" com {tentativa_atual} tentativas")

            return True  # Sucesso

        except Exception as e:
            #print(f"Ocorreu um erro com MAC {mac_address}: {e}")
            print(f"Ocorreu um erro com MAC {mac_address}.")
            driver.refresh()
            tentativa_atual += 1
            continue

        finally:
            driver.refresh()

    print(f"Todas as tentativas falharam para MAC: {mac_address}")
    return False  # Falha após todas as tentativas