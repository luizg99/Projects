from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from PIL import Image
import pandas as pd
import time
import os
from io import BytesIO
import _Biblioteca as lib

# Configurações do Selenium WebDriver
driver_path = r"C:\Users\altis\Desktop\ProjetoIptvManterLinkUsuarioAtualizado\chromedriver-win64\chromedriver.exe"
url = "https://iboplayer.com/device/login"

# Configuração da API Key do 2Captcha
captcha_api_key = "0439b8069b74afca88f8062c8eb51716"

# Caminho da planilha Excel
planilha_path = r"C:\Users\altis\Desktop\ProjetoIptvManterLinkUsuarioAtualizado\Clientes.xlsx"

link_atualizado_tvs = 'http://grianze.com'
link_atualizado_bit = 'http://play.biturl.vip'


# Função principal para executar o processo para cada cliente
def processar_cliente(mac_address, device_key, servidor, driver, tentativas=2):
    tentativa_atual = 0

    while tentativa_atual < tentativas:
        try:
            # Aguarde 10 segundos antes de interagir
            time.sleep(10)

            # Preencher o campo MAC Address e Device Key
            driver.find_element(By.ID, "max-address").send_keys(mac_address)
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

            processed_path = os.path.join(os.path.expanduser("~"), "Documents", "captcha_black_and_white.png")
            captcha_cropped.save(processed_path)

            captcha_text = lib.solve_captcha_with_2captcha(processed_path, captcha_api_key)
            driver.find_element(By.XPATH, "//label[text()='Captcha']/following-sibling::input").send_keys(captcha_text)

            # Clicar no botão de login
            driver.find_element(By.XPATH, '/html/body/div/main/div/form/button').click()
            time.sleep(10)

            # Clicar no botão de editar playlist
            driver.find_element(By.XPATH,
                                "//*[name()='svg' and contains(@class, 'text-blue-500') and contains(@class, 'cursor-pointer')]").click()
            time.sleep(6)

            # Substituir o valor do campo "Host"
            host_input = driver.find_element(By.XPATH, '//*[@id="host"]')
            host_input.clear()

            if servidor == 'TVS':
                host_input.send_keys(link_atualizado_tvs)
            if servidor == 'BIT':
                host_input.send_keys(link_atualizado_bit)

            # Salvar
            driver.find_element(By.XPATH, '//*[@id="root"]/main/div/section/form/div[2]/button').click()
            time.sleep(5)

            driver.execute_script("window.scrollBy(0, 800);")
            time.sleep(2)

            # Logout
            driver.find_element(By.XPATH, '//*[@id="root"]/main/aside/a[7]').click()
            print(f"Processamento concluído para MAC: {mac_address}")

            return True  # Sucesso

        except Exception as e:
            print(f"Ocorreu um erro com MAC {mac_address}: {e}")
            driver.refresh()
            continue

        finally:
            driver.refresh()

    print(f"Todas as tentativas falharam para MAC: {mac_address}")
    return False  # Falha após todas as tentativas

# Função para processar a planilha
def main():
    print("Lendo a planilha...")
    df = pd.read_excel(planilha_path)

    # Inicializa listas para controle de tentativas
    clientes_falhados = []

    service = Service(driver_path)
    driver = webdriver.Chrome(service=service)
    driver.maximize_window()
    driver.get(url)

    # Primeira tentativa com todos os clientes
    for index, row in df.iterrows():
        mac_address = row['MAC Address']
        device_key = row['Device Key']
        servidor = row['Servidor']

        print(f"Processando MAC: {mac_address}, servidor: {servidor}")
        sucesso = processar_cliente(mac_address, device_key, servidor, driver)
        if not sucesso:
            clientes_falhados.append((mac_address, device_key, servidor))

    # Realiza até 4 novas tentativas para os clientes que falharam
    tentativa = 1
    while clientes_falhados and tentativa <= 4:
        print(f"\nTentativa {tentativa} para clientes que falharam...")
        novos_falhados = []
        for mac_address, device_key, servidor in clientes_falhados:
            print(f"Reprocessando MAC: {mac_address}, Servidor: {servidor}")
            sucesso = processar_cliente(mac_address, device_key, servidor)
            if not sucesso:
                novos_falhados.append((mac_address, device_key, servidor))
        clientes_falhados = novos_falhados  # Atualiza a lista de falhados
        tentativa += 1

    # Exibe clientes que falharam após todas as tentativas
    if clientes_falhados:
        print("\nOs seguintes clientes falharam após 4 tentativas:")
        for mac_address, device_key, servidor in clientes_falhados:
            print(f"MAC: {mac_address}, Servidor: {servidor}")
    else:
        print("\nTodos os clientes foram processados com sucesso!")

    driver.quit()

if __name__ == "__main__":
    main()
