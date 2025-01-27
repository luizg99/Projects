from selenium import webdriver
from selenium.webdriver.chrome.service import Service
import _Biblioteca as lib
import IboPlayer
import IboPlayerPro
from webdriver_manager.chrome import ChromeDriverManager
import Interface

# Configurações do Google Sheets
sheet_url_clientes = 'https://docs.google.com/spreadsheets/d/1ifSYQKY2W-DA0D0wYY00tKag90Tp5FJP3mdZ0lConUs/export?format=csv'

# Configuração da API Key do 2Captcha
lib.captcha_api_key = "0439b8069b74afca88f8062c8eb51716"

#Atualizando links
lib.atualizarLinks()


# Função para processar a planilha
def main():
    # Exibe a interface e obtém os servidores selecionados
    ui = Interface.InterfaceAtualizacao()
    opcoes = ui.exibir()

    if not opcoes['selecionado']:
        print("Nenhum servidor selecionado. Saindo...")
        return

    print("Lendo a planilha...")
    df = lib.getGoogleSheetData(sheet_url_clientes)

    # Inicializa listas para controle de tentativas
    clientes_falhados = []

    # Cria o driver usando WebDriver Manager
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
    driver.maximize_window()

    # Primeira tentativa com todos os clientes
    for index, row in df.iterrows():
        mac_address: str = row['MAC Address']
        device_key: str = str(row['Device Key'])
        servidor: str = row['Servidor']
        siteAtivacao: str = row['Site ativação']

        # Pula servidores não marcados
        if servidor == 'TVS' and not opcoes['TVS']:
            continue
        elif servidor == 'UNIPLAY' and not opcoes['UNIPLAY']:
            continue
        elif servidor == 'BIT' and not opcoes['BIT']:
            continue

        print(f"Processando MAC: {mac_address}, servidor: {servidor}")

        if siteAtivacao == 'iboplayer.com':
            sucesso = IboPlayer.processar_cliente(mac_address, device_key, servidor, driver)
        elif siteAtivacao == 'iboplayer.pro':
            sucesso = IboPlayerPro.processar_cliente(mac_address, device_key, servidor, driver)

        if not sucesso:
            clientes_falhados.append((mac_address, device_key, servidor, siteAtivacao))

    # Realiza até 4 novas tentativas para os clientes que falharam
    tentativa = 1
    while clientes_falhados and tentativa < 3:
        print(f"\nTentativa {tentativa} para clientes que falharam...")
        novos_falhados = []
        for mac_address, device_key, servidor, siteAtivacao in clientes_falhados:
            print(f"Reprocessando MAC: {mac_address}, Servidor: {servidor}")
            if siteAtivacao == 'iboplayer.com':
                sucesso = IboPlayer.processar_cliente(mac_address, device_key, servidor, driver)
            elif siteAtivacao == 'iboplayer.pro':
                sucesso = IboPlayerPro.processar_cliente(mac_address, device_key, servidor, driver)

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
    input("\nPressione Enter para fechar...")