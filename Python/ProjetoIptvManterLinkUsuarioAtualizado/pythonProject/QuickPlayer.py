from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from PIL import Image
import time
import os
from io import BytesIO
import _Biblioteca as lib
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.common.keys import Keys

def processar_cliente(mac_address, device_key, servidor, driver, playlist_name='', tentativas=3):
    tentativa_atual = 1

    while tentativa_atual < tentativas:
        try:
            driver.get('https://quickplayer.app/#/login')

            # Esperar o campo "max-address" existir
            mac_address_field = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located((By.ID, "mac"))
            )

            # Enviar o texto (MAC Address)
            mac_address_field.send_keys(mac_address)

            login_button = driver.find_element(By.XPATH, '//*[@id="login"]/div/div[2]/div/div/div/div/button')

            # Enquanto o campo "device key" não existir, continuar clicando no botão de login a cada 2 segundos
            while True:
                try:
                    device_key_field = driver.find_element(By.ID, "login_code")
                    break  # Sai do loop quando o campo for encontrado
                except:
                    login_button.click()
                    time.sleep(2)


            # Esperar o campo "do device key" existir
            device_key_field = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located((By.ID, "login_code"))
            )

            #digita o device
            device_key_field.send_keys(device_key)

            # Clicar no botão de login
            driver.find_element(By.XPATH, '/html/body/div[2]/div/div[2]/div/div[2]/div/div[3]/div/div/div/div/div/button').click()

            # -------------------------------------------------------------------------------------------
            # Esperar o elemento SVG EXCLUIR E EDITAR existir dentro do contêiner específico
            svg_container = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located(
                    (By.XPATH, "//*[@id='root']/div/section/main/div[1]/div/div[2]/div[1]/div[1]/div[2]")
                )
            )

            # Agora esperar o SVG dentro desse contêiner
            svg_element = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located(
                    (By.TAG_NAME, "svg")
                )
            )
            # -------------------------------------------------------------------------------------------


            # Espera o elemento SVG existir com width e height iguais a 29
            svg_element = WebDriverWait(driver, 15).until(
                EC.element_to_be_clickable(
                    (By.XPATH, "//*[name()='svg' and @width='29' and @height='29']")
                )
            )
            # Clicar no elemento SVG
            svg_element.click()
            time.sleep(1)

            # Localiza o campo
            campoUrl = driver.find_element(By.XPATH, '//*[@id="url"]')

            # Obtém o conteúdo preenchido (valor) do campo
            conteudo = campoUrl.get_attribute('value')

            # Seleciona todo o texto e deleta
            campoUrl.send_keys(Keys.CONTROL, 'a')
            campoUrl.send_keys(Keys.DELETE)

            if servidor == 'TVS':
                hostAtualizado = lib.link_atualizado_tvs
            if servidor == 'UNIPLAY':
                hostAtualizado = lib.link_atualizado_uniplay
            if servidor == 'BIT':
                hostAtualizado = lib.link_atualizado_bit

            #Trata o link para pegar a url atualizada e reaproveitar os dados de usuário usuário e senha.
            partes = conteudo.split("/get.php")
            nova_url = hostAtualizado + "/get.php" + partes[1]

            #Preenche o campo com a nova URL
            campoUrl.send_keys(nova_url)

            #Finalização do processo
            driver.find_element(By.XPATH, '/html/body/div[2]/div/div[2]/div/div[2]/div/div/form/div/div[6]/button').click()
            time.sleep(1)

            driver.find_element(By.XPATH, '/html/body/div[1]/div/section/header/div[3]/button[2]').click()
            time.sleep(1)

            driver.find_element(By.XPATH, '/html/body/div[3]/div/div[2]/div/div[2]/div/div/div[2]/button[2]').click()
            time.sleep(1)

            return True  # Sucesso


        except Exception as e:
            #print(f"Ocorreu um erro com MAC {mac_address}: {e}")
            print(f"Ocorreu um erro com MAC {mac_address}.")
            driver.refresh()
            tentativa_atual += 1
            continue

        finally:
            driver.refresh()