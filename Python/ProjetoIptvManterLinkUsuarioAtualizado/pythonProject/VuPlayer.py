from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
import time
import _Biblioteca as lib
from selenium.webdriver.common.keys import Keys

def processar_cliente(mac_address, device_key, servidor, driver, playlist_name='', tentativas=3):
    tentativa_atual = 1

    while tentativa_atual < tentativas:
        try:
            driver.get('https://vuproplayer.org/login')

            if tentativa_atual > 1:
              time.sleep(2)

            # Esperar o campo "macAddress" existir
            mac_address_field = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located((By.ID, "macAddress"))
            )

            time.sleep(5)

            # Enviar o texto (MAC Address)
            mac_address_field.send_keys(mac_address)

            # Enviar o texto (device key)
            driver.find_element(By.ID, "key").send_keys(device_key)

            login_button = driver.find_element(By.XPATH, '/html/body/div[2]/section[2]/div/div/div[2]/form/div/div[4]/button')

            login_button.click()
            time.sleep(2)

            # Esperar o elemento SVG EDITAR existir dentro do contêiner específico
            edit_button = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located(
                    (By.XPATH,
                     "/html/body/div[2]/section[2]/div/div[2]/div[1]/table/tbody/tr/td[3]/button[1]")
                )
            )
            time.sleep(2)
            # Clicar no elemento SVG
            edit_button.click()
            time.sleep(1)

            # Localiza o campo
            campoUrl = driver.find_element(By.XPATH, '//*[@id="playlist-url"]')

            # Obtém o conteúdo preenchido (valor) do campo
            conteudo = campoUrl.get_attribute('value')

            # Seleciona todo o texto e deleta
            campoUrl.send_keys(Keys.CONTROL, 'a')
            campoUrl.send_keys(Keys.DELETE)

            #Trata o link para pegar a url atualizada e reaproveitar os dados de usuário usuário e senha.

            campoUrl.clear()

            campoUrl.send_keys(lib.obterLinkAtualizado(servidor))

            # Esperar até que o botão de salvar exista e seja clicável
            save_button = WebDriverWait(driver, 10).until(
                EC.element_to_be_clickable((By.XPATH, '/html/body/div[2]/section[2]/div/div[2]/div[2]/div/div/div[3]/button[2]'))
            )
            # Clicar no botão de logout
            save_button.click()
            time.sleep(2)

            driver.get('https://vuproplayer.org/logout')

            return True  # Sucesso


        except Exception as e:
            #print(f"Ocorreu um erro com MAC {mac_address}: {e}")
            print(f"VUPLAYER: Ocorreu um erro com MAC {mac_address}")
            driver.refresh()
            tentativa_atual += 1
            continue

        finally:
            driver.refresh()