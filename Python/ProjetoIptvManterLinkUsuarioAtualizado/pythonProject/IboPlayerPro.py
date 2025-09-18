from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
import time
import _Biblioteca as lib

def processar_cliente(mac_address, device_key, servidor, driver, tentativas=3):
    tentativa_atual = 1

    while tentativa_atual < tentativas:
        try:
            driver.get('https://iboplayer.pro/manage-playlists/login/')

            # Scrollar para baixo
            driver.execute_script("window.scrollBy(0, 200);")
            time.sleep(2)

            # Esperar o campo "max-address" existir
            mac_address_field = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located((By.ID, "mac_address"))
            )
            # Enviar o texto (MAC Address)
            mac_address_field.send_keys(mac_address)

            # Enviar o texto (device key)
            driver.find_element(By.ID, "password").send_keys(device_key)

            time.sleep(1)

            # Clicar no botão de login
            driver.find_element(By.XPATH, '/html/body/div[1]/div[3]/div/form/button').click()

            # Esperar o botão edit existir
            edit_button = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located(
                    (By.XPATH, "/html/body/div[1]/div[3]/div/div[3]/div[2]/section/div/div[2]/table/tbody/tr/td[3]/button[1]")
                )
            )

            # Scrollar para baixo
            driver.execute_script("window.scrollBy(0, 700);")
            time.sleep(2)

            # Clicar no botão edit
            edit_button.click()

            # Substituir o valor do campo "Host"
            # Espera até 10 segundos para que o elemento exista no DOM
            host_input = WebDriverWait(driver, 15).until(
                EC.presence_of_element_located((By.XPATH, '//*[@name="host"]'))
            )

            host_input.clear()

            host_input.send_keys(lib.obterLinkAtualizado(servidor))

            # Salvar
            driver.find_element(By.XPATH, '/html/body/div[3]/div/div/div[2]/form/button').click()
            time.sleep(2)

            driver.refresh()

            # Logout
            # Esperar até que o botão de logout exista e seja clicável
            logout_button = WebDriverWait(driver, 10).until(
                EC.element_to_be_clickable((By.XPATH, '/html/body/div[1]/div[3]/div/div[3]/div[1]/div[3]'))
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