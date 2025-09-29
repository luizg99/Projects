from selenium import webdriver
from selenium.webdriver.chrome.service import Service
import _Biblioteca as lib
import IboPlayer
import IboPlayerPro
import QuickPlayer
import VuPlayer
from webdriver_manager.chrome import ChromeDriverManager
import Interface


#Grid 0 é a principal
vGridId = 0

#Grid de testes
#vGridId = 493313803

sheet_url_clientes = f"https://docs.google.com/spreadsheets/d/1ifSYQKY2W-DA0D0wYY00tKag90Tp5FJP3mdZ0lConUs/export?format=csv&gid={vGridId}"

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

    qtdeClientes = 0
    # Primeira tentativa com todos os clientes
    for index, row in df.iterrows():
        mac_address: str = str(row['MAC Address']).strip()
        device_key: str = str(row['Device Key']).strip()
        servidor: str = str(row['Servidor']).strip()
        siteAtivacao: str = str(row['Site ativação']).strip()
        datavalidadeApp: str = str(row['data validade APP']).strip()
        Ativo: str = str(row['Ativo']).strip()

        Atualizar: str = str(row['Atualizar']).strip()


        if datavalidadeApp.strip() == "" or Ativo.strip() == "N" or Atualizar.strip() == "N":
            continue

        # Pula servidores não marcados
        if servidor == 'TVS' and not opcoes['TVS']:
            continue
        elif servidor == 'UNIPLAY' and not opcoes['UNIPLAY']:
            continue
        elif servidor == 'BIT' and not opcoes['BIT']:
            continue
        elif servidor == 'FAST' and not opcoes['BIT']:
            continue

        print(f"Processando MAC: {mac_address}, servidor: {servidor}")

        if siteAtivacao == 'iboplayer.com':
            sucesso = IboPlayer.processar_cliente(mac_address, device_key, servidor, driver)
        elif siteAtivacao == 'iboplayer.pro':
            sucesso = IboPlayerPro.processar_cliente(mac_address, device_key, servidor, driver)
        elif siteAtivacao == 'quickplayer.app':
            sucesso = QuickPlayer.processar_cliente(mac_address, device_key, servidor, driver)
        elif siteAtivacao == 'vuplayer.pro':
            sucesso = VuPlayer.processar_cliente(mac_address, device_key, servidor, driver)
        else: print("Erro: Nenhum servidor selecionado.")

        if not sucesso:
            clientes_falhados.append((index, mac_address, device_key, servidor, siteAtivacao))
            continue

        qtdeClientes = qtdeClientes + 1

    # Realiza até 4 novas tentativas para os clientes que falharam
    tentativa = 1
    print(f"Fim-------------------------------------------------------")
    print(f"TENTANDO PARA OS CLIENTES QUE FALHARAM:")
    while clientes_falhados and tentativa < 3:
        print(f"\nTentativa {tentativa} para clientes que falharam... Site: {siteAtivacao}")
        novos_falhados = []
        for index, mac_address, device_key, servidor, siteAtivacao in clientes_falhados:
            print(f"Reprocessando MAC: {mac_address}, Servidor: {servidor}, Site: {siteAtivacao}")
            if siteAtivacao == 'iboplayer.com':
                sucesso = IboPlayer.processar_cliente(mac_address, device_key, servidor, driver)
            elif siteAtivacao == 'iboplayer.pro':
                sucesso = IboPlayerPro.processar_cliente(mac_address, device_key, servidor, driver)
            elif siteAtivacao == 'quickplayer.app':
                sucesso = QuickPlayer.processar_cliente(mac_address, device_key, servidor, driver)
            elif siteAtivacao == 'vuplayer.pro':
                sucesso = VuPlayer.processar_cliente(mac_address, device_key, servidor, driver)

            if not sucesso:
                novos_falhados.append((index, mac_address, device_key, servidor, siteAtivacao))
        clientes_falhados = novos_falhados  # Atualiza a lista de falhados
        tentativa += 1

    # Exibe clientes que falharam após todas as tentativas
    if clientes_falhados:
        print("\nOs seguintes clientes falharam após 4 tentativas:")
        for index, mac_address, device_key, servidor, siteAtivacao in clientes_falhados:
            print(f"Linha: {index + 2} | MAC: {mac_address} | Servidor: {servidor} | Site: {siteAtivacao}")
    else:
        print("\nTodos os clientes foram processados com sucesso!")
        print(f"Quantidade de clientes atualizados: {qtdeClientes}")

    driver.quit()

if __name__ == "__main__":
    main()
    input("\nPressione Enter para fechar...")