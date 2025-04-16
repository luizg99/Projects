import os
import sys
import ftplib
import tkinter as tk
from tkinter import filedialog, messagebox, ttk
import datetime
import re
import subprocess
import threading
from dateutil.parser import parse
import zipfile
import py7zr
import rarfile
import tarfile
import gzip

diretorioNomeArquivo = f"C:\subirbase\script.sql"

class FTPBackupManager:
    def __init__(self, root):
        self.root = root
        self.root.title("Gerenciador de Backups Oracle")
        self.root.geometry("700x500")
        self.root.resizable(True, True)

        # Variáveis de configuração
        self.ftp_host = tk.StringVar()
        self.ftp_user = tk.StringVar()
        self.ftp_password = tk.StringVar()
        self.dest_folder = tk.StringVar(value="C:/subirbase")
        self.client_folder = tk.StringVar(value="backup-bd/")
        self.ftp = None
        self.current_directory = "/"

        # Criar interface
        self.create_widgets()

    #Parte Visual...
    def create_widgets(self):
        # Frame para configurações de FTP
        ftp_frame = ttk.LabelFrame(self.root, text="Configurações de FTP")
        ftp_frame.pack(fill=tk.X, padx=10, pady=5)

        ttk.Label(ftp_frame, text="Host:").grid(row=0, column=0, sticky=tk.W, padx=5, pady=5)
        ttk.Entry(ftp_frame, textvariable=self.ftp_host, width=30).grid(row=0, column=1, padx=5, pady=5)

        ttk.Label(ftp_frame, text="Usuário:").grid(row=1, column=0, sticky=tk.W, padx=5, pady=5)
        ttk.Entry(ftp_frame, textvariable=self.ftp_user, width=30).grid(row=1, column=1, padx=5, pady=5)

        ttk.Label(ftp_frame, text="Senha:").grid(row=2, column=0, sticky=tk.W, padx=5, pady=5)
        ttk.Entry(ftp_frame, textvariable=self.ftp_password, width=30, show="*").grid(row=2, column=1, padx=5, pady=5)

        ttk.Button(ftp_frame, text="Conectar", command=self.connect_ftp).grid(row=3, column=0, padx=5, pady=5)
        ttk.Button(ftp_frame, text="Salvar Credenciais", command=self.save_credentials).grid(row=3, column=1, padx=5,
                                                                                             pady=5)
        ttk.Button(ftp_frame, text="Carregar Credenciais", command=self.load_credentials).grid(row=3, column=2, padx=5,
                                                                                               pady=5)

        # Frame para pasta de destino
        dest_frame = ttk.LabelFrame(self.root, text="Pasta de Destino")
        dest_frame.pack(fill=tk.X, padx=10, pady=5)

        ttk.Label(dest_frame, text="Pasta:").grid(row=0, column=0, sticky=tk.W, padx=5, pady=5)
        ttk.Entry(dest_frame, textvariable=self.dest_folder, width=50).grid(row=0, column=1, padx=5, pady=5)
        ttk.Button(dest_frame, text="Selecionar", command=self.select_folder).grid(row=0, column=2, padx=5, pady=5)

        # Frame para navegação do FTP
        nav_frame = ttk.LabelFrame(self.root, text="Navegação FTP")
        nav_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=5)

        ttk.Label(nav_frame, text="Cliente:").grid(row=0, column=0, sticky=tk.W, padx=5, pady=5)
        ttk.Entry(nav_frame, textvariable=self.client_folder, width=30).grid(row=0, column=1, padx=5, pady=5)
        ttk.Button(nav_frame, text="Buscar", command=self.search_in_list).grid(row=0, column=2, padx=5, pady=5)

        # Lista de arquivos
        self.file_list = ttk.Treeview(nav_frame, columns=("name", "date", "size"), show="headings")
        self.file_list.heading("name", text="Nome")
        self.file_list.heading("date", text="Data")
        self.file_list.heading("size", text="Tamanho")
        self.file_list.column("name", width=250)
        self.file_list.column("date", width=150)
        self.file_list.column("size", width=100)
        self.file_list.grid(row=1, column=0, columnspan=3, sticky=tk.NSEW, padx=5, pady=5)

        scrollbar = ttk.Scrollbar(nav_frame, orient=tk.VERTICAL, command=self.file_list.yview)
        scrollbar.grid(row=1, column=3, sticky=tk.NS)
        self.file_list.configure(yscrollcommand=scrollbar.set)

        nav_frame.grid_rowconfigure(1, weight=1)
        nav_frame.grid_columnconfigure(0, weight=1)
        nav_frame.grid_columnconfigure(1, weight=1)
        nav_frame.grid_columnconfigure(2, weight=1)

        # Botões de ação
        action_frame = ttk.Frame(self.root)
        action_frame.pack(fill=tk.X, padx=10, pady=5)

        ttk.Button(action_frame, text="Baixar e Extrair", command=self.download_and_extract).pack(side=tk.LEFT, padx=5)
        ttk.Button(action_frame, text="Baixar, Extrair e Subir Base", command=self.full_process).pack(side=tk.LEFT,
                                                                                                      padx=5)
        ttk.Button(action_frame, text="Mostrar Últimos 3 Backups", command=self.show_last_backups).pack(side=tk.LEFT,
                                                                                                        padx=5)

        # Status bar
        self.status_var = tk.StringVar(value="Pronto")
        self.status_bar = ttk.Label(self.root, textvariable=self.status_var, relief=tk.SUNKEN, anchor=tk.W)
        self.status_bar.pack(fill=tk.X, side=tk.BOTTOM, padx=5, pady=5)

        self.progress = ttk.Progressbar(self.root, orient=tk.HORIZONTAL, length=100, mode='indeterminate')
        self.progress.pack(fill=tk.X, side=tk.BOTTOM, padx=5, pady=2)

        # Carregar configurações salvas no início
        self.load_credentials()

    def ftplogin(self, user, password):
        """Realiza o login no servidor FTP"""
        try:
            self.ftp.login(user, password)
            return True
        except ftplib.error_perm as e:
            raise Exception(f"Erro de login: {str(e)}")
        except Exception as e:
            raise Exception(f"Falha ao realizar login: {str(e)}")

    def connect_ftp(self):
        """Conecta ao servidor FTP"""
        host_input = self.ftp_host.get()
        user = self.ftp_user.get()
        password = self.ftp_password.get()

        # Separa host e porta se estiverem no formato host:porta
        if ":" in host_input:
            host, port = host_input.split(":", 1)
            port = int(port)
        else:
            host = host_input
            port = 21  # Porta FTP padrão

        if not host or not user or not password:
            messagebox.showerror("Erro", "Por favor, preencha todos os campos de configuração FTP")
            return

        try:
            self.status_var.set("Conectando ao FTP...")
            self.progress.start()
            self.root.update()

            self.ftp = ftplib.FTP()
            self.ftp.connect(host, port)
            self.ftplogin(user, password)
            self.ftp.cwd("/backup-bd-new")  # entra direto na pasta
            self.current_directory = self.ftp.pwd()

            self.update_file_list()
            self.status_var.set(f"Conectado a {host}:{port}")
            messagebox.showinfo("Conexão", f"Conectado com sucesso a {host}:{port}")
        except Exception as e:
            messagebox.showerror("Erro", f"Falha ao conectar: {str(e)}")
            self.status_var.set("Falha na conexão")
        finally:
            self.progress.stop()

    def update_file_list(self):
        """Atualiza a lista de arquivos/diretórios no FTP"""
        if not self.ftp:
            return

        # Limpar lista atual
        for item in self.file_list.get_children():
            self.file_list.delete(item)

        # Obter listagem
        file_list = []
        self.ftp.dir(file_list.append)

        # Processar e mostrar arquivos
        for item in file_list:
            parts = item.split()
            if len(parts) >= 8:
                # Extrair informações
                permissions = parts[0]
                is_dir = permissions.startswith('d')
                size = parts[4]
                date_str = f"{parts[5]} {parts[6]} {parts[7]}"

                # Nome do arquivo (pode conter espaços)
                name_idx = item.find(parts[7]) + len(parts[7]) + 1
                name = item[name_idx:].strip()

                if name not in ['.', '..']:
                    self.file_list.insert("", tk.END, values=(name, date_str, size if not is_dir else "<DIR>"))

    def search_in_list(self):
        """Filtra visualmente os arquivos/diretórios com base no texto do campo Cliente"""
        if not self.ftp:
            messagebox.showwarning("Aviso", "Conecte-se ao FTP primeiro")
            return

        search_term = self.client_folder.get().strip().lower()

        # Atualiza a lista completa (sem filtro)
        full_list = []
        self.ftp.dir(full_list.append)

        # Limpa a visualização atual
        for item in self.file_list.get_children():
            self.file_list.delete(item)

        # Adiciona só os itens que batem com o filtro
        for line in full_list:
            parts = line.split()
            if len(parts) >= 8:
                permissions = parts[0]
                is_dir = permissions.startswith('d')
                size = parts[4]
                date_str = f"{parts[5]} {parts[6]} {parts[7]}"
                name_idx = line.find(parts[7]) + len(parts[7]) + 1
                name = line[name_idx:].strip()

                if name not in ['.', '..'] and search_term in name.lower():
                    self.file_list.insert("", tk.END, values=(name, date_str, size if not is_dir else "<DIR>"))

        self.status_var.set(f"Resultados contendo: '{search_term}'")

    # Buscar: navega para a pasta do cliente específico
    def navigate_to_client(self):
        """Navega para a pasta do cliente específico"""
        if not self.ftp:
            messagebox.showwarning("Aviso", "Conecte-se ao FTP primeiro")
            return

        client = self.client_folder.get()
        if not client:
            messagebox.showwarning("Aviso", "Informe o nome do cliente")
            return

        try:
            self.status_var.set(f"Navegando para a pasta do cliente {client}...")
            self.progress.start()
            self.root.update()

            # Tentar voltar para o diretório raiz e depois navegar
            self.ftp.cwd("/backup-bd-new")  # entra primeiro na pasta base
            self.ftp.cwd(client)  # depois entra no cliente informado
            self.current_directory = self.ftp.pwd()

            #Chama a função update_file_list
            self.update_file_list()
            self.status_var.set(f"Pasta do cliente: {self.current_directory}")

            # Automaticamente encontrar e selecionar o backup mais recente
            self.show_latest_backup()
        except Exception as e:
            messagebox.showerror("Erro", f"Falha ao navegar: {str(e)}")
            self.status_var.set("Falha na navegação")
        finally:
            self.progress.stop()

    def find_backup_files(self):
        """Encontra arquivos de backup na pasta atual"""
        if not self.ftp:
            return []

        backup_files = []

        try:
            files = []
            self.ftp.dir(files.append)

            # Regex para identificar arquivos de backup
            backup_pattern = re.compile(r'.*\.(zip|7z|rar|tar|gz|bak|dmp)$', re.IGNORECASE)

            for item in files:
                parts = item.split()
                if len(parts) >= 8:
                    # Nome do arquivo (pode conter espaços)
                    name_idx = item.find(parts[7]) + len(parts[7]) + 1
                    name = item[name_idx:].strip()

                    # Verificar se é um arquivo e se parece um backup
                    if not parts[0].startswith('d') and backup_pattern.match(name):
                        # Data no formato do FTP (ex: Apr 14 17:01)
                        date_str = f"{parts[5]} {parts[6]} {parts[7]}"
                        try:
                            # Força o ano atual no parsing
                            current_year = datetime.datetime.now().year
                            date = parse(f"{date_str} {current_year}", fuzzy=True)
                            size = int(parts[4])
                            backup_files.append((name, date, size))
                        except Exception as e:
                            print(f"Erro ao converter data de {name}: {e}")
                            backup_files.append((name, datetime.datetime(1970, 1, 1), int(parts[4])))

            # Ordenar por data decrescente
            backup_files.sort(key=lambda x: x[1], reverse=True)

        except Exception as e:
            messagebox.showerror("Erro", f"Erro ao listar backups: {str(e)}")

        return backup_files

    def show_latest_backup(self):
        """Mostra e seleciona o backup mais recente"""
        backup_files = self.find_backup_files()

        if not backup_files:
            self.status_var.set("Nenhum backup encontrado. Verificando dia anterior...")
            self.show_last_backups()
            return

        # Selecionar o mais recente na lista
        latest = backup_files[0][0]

        # Encontrar o item na lista e selecionar
        for item in self.file_list.get_children():
            values = self.file_list.item(item, "values")
            if values[0] == latest:
                self.file_list.selection_set(item)
                self.file_list.see(item)
                break

        self.status_var.set(f"Backup mais recente: {latest}")

    def show_last_backups(self):
        """Entra na pasta selecionada e mostra os últimos 3 arquivos de backup"""
        if not self.ftp:
            messagebox.showwarning("Aviso", "Conecte-se ao FTP primeiro")
            return

        # Verifica se algum diretório está selecionado
        selection = self.file_list.selection()
        if not selection:
            messagebox.showwarning("Aviso", "Selecione uma pasta do cliente na lista")
            return

        values = self.file_list.item(selection[0], "values")
        selected_name = values[0]
        is_dir = values[2] == "<DIR>"

        if not is_dir:
            messagebox.showwarning("Aviso", f"'{selected_name}' não é uma pasta")
            return

        try:
            # Entrar na pasta do cliente
            self.ftp.cwd(selected_name)
            self.current_directory = self.ftp.pwd()

            # Buscar arquivos de backup dentro da pasta
            files = []
            self.ftp.dir(files.append)

            backup_pattern = re.compile(r'.*\.(zip|7z|rar|tar|gz|bak|dmp)$', re.IGNORECASE)
            backup_files = []

            for item in files:
                parts = item.split()
                if len(parts) >= 8:
                    name_idx = item.find(parts[7]) + len(parts[7]) + 1
                    name = item[name_idx:].strip()

                    if not parts[0].startswith('d') and backup_pattern.match(name):
                        date_str = f"{parts[5]} {parts[6]} {parts[7]}"
                        try:
                            current_year = datetime.datetime.now().year
                            parsed_date = parse(f"{date_str} {current_year}", fuzzy=True)
                            size = int(parts[4])
                            backup_files.append((name, parsed_date, size))
                        except Exception as e:
                            print(f"Erro ao processar {name}: {e}")

            # Ordenar e pegar os 3 mais recentes
            backup_files.sort(key=lambda x: x[1], reverse=True)
            top3 = backup_files[:3]

            # Limpar lista atual
            for item in self.file_list.get_children():
                self.file_list.delete(item)

            # Mostrar os 3 backups na grid
            for name, date, size in top3:
                self.file_list.insert("", tk.END, values=(name, date.strftime("%b %d %H:%M"), size))

            if top3:
                self.status_var.set(f"Mostrando os últimos 3 backups de '{selected_name}'")
            else:
                self.status_var.set(f"Nenhum backup encontrado em '{selected_name}'")

        except Exception as e:
            messagebox.showerror("Erro", f"Falha ao acessar pasta '{selected_name}': {str(e)}")


            # Ordenar e pegar os 3 mais recentes
            backup_files.sort(key=lambda x: x[1], reverse=True)
            top3 = backup_files[:3]

            # Limpar lista atual
            for item in self.file_list.get_children():
                self.file_list.delete(item)

            # Mostrar os 3 backups na grid
            for name, date, size in top3:
                self.file_list.insert("", tk.END, values=(name, date.strftime("%b %d %H:%M"), size))

            if top3:
                self.status_var.set(f"Mostrando os últimos 3 backups de '{selected_name}'")
            else:
                self.status_var.set(f"Nenhum backup encontrado em '{selected_name}'")
    def get_selected_file(self):
        """Retorna o nome do arquivo selecionado ou None"""
        selection = self.file_list.selection()
        if not selection:
            messagebox.showwarning("Aviso", "Selecione um arquivo de backup")
            return None

        values = self.file_list.item(selection[0], "values")
        return values[0]

    def download_file(self, filename, dest_path):
        """Baixa um arquivo do FTP para o destino especificado"""
        if not self.ftp:
            return False

        try:
            self.status_var.set(f"Baixando {filename}...")
            self.progress.start()
            self.root.update()

            # Criar diretório de destino se não existir
            os.makedirs(os.path.dirname(dest_path), exist_ok=True)

            # Baixar o arquivo
            with open(dest_path, 'wb') as f:
                self.ftp.retrbinary(f'RETR {filename}', f.write)

            self.status_var.set(f"Download de {filename} concluído")
            return True
        except Exception as e:
            messagebox.showerror("Erro", f"Falha ao baixar arquivo: {str(e)}")
            self.status_var.set("Falha no download")
            return False
        finally:
            self.progress.stop()

    def extract_file(self, filepath, dest_folder):
        """Extrai um arquivo para o destino especificado"""
        try:
            self.status_var.set(f"Extraindo {os.path.basename(filepath)}...")
            self.progress.start()
            self.root.update()

            filename = os.path.basename(filepath).lower()

            # Criar o diretório de destino se não existir
            os.makedirs(dest_folder, exist_ok=True)

            # Extrair de acordo com o formato
            extracted_files = []

            if filename.endswith('.zip'):
                with zipfile.ZipFile(filepath, 'r') as zip_ref:
                    zip_ref.extractall(dest_folder)
                    extracted_files = zip_ref.namelist()
            elif filename.endswith('.7z'):
                with py7zr.SevenZipFile(filepath, mode='r') as z:
                    z.extractall(dest_folder)
                    extracted_files = z.getnames()
            elif filename.endswith('.rar'):
                with rarfile.RarFile(filepath, 'r') as rf:
                    rf.extractall(dest_folder)
                    extracted_files = rf.namelist()
            elif filename.endswith('.tar'):
                with tarfile.open(filepath, 'r') as tar:
                    tar.extractall(dest_folder)
                    extracted_files = tar.getnames()
            elif filename.endswith('.gz'):
                if filename.endswith('.tar.gz'):
                    with tarfile.open(filepath, 'r:gz') as tar:
                        tar.extractall(dest_folder)
                        extracted_files = tar.getnames()
                else:
                    # Extrair arquivo .gz simples
                    output_file = os.path.join(dest_folder, os.path.splitext(os.path.basename(filepath))[0])
                    with gzip.open(filepath, 'rb') as f_in:
                        with open(output_file, 'wb') as f_out:
                            f_out.write(f_in.read())
                    extracted_files = [output_file]
            else:
                # Para outros formatos, apenas copiar
                import shutil
                dest_file = os.path.join(dest_folder, os.path.basename(filepath))
                shutil.copy2(filepath, dest_file)
                extracted_files = [dest_file]

            # Armazenar o caminho do arquivo DMP ou o primeiro arquivo extraído
            dmp_files = [f for f in extracted_files if f.lower().endswith('.dmp')]
            if dmp_files:
                self.last_extracted_file = os.path.join(dest_folder, dmp_files[0])
            elif extracted_files:
                self.last_extracted_file = os.path.join(dest_folder, extracted_files[0])
            else:
                self.last_extracted_file = filepath

            self.status_var.set(f"Extração de {os.path.basename(filepath)} concluída")
            return True
        except Exception as e:
            messagebox.showerror("Erro", f"Falha ao extrair arquivo: {str(e)}")
            self.status_var.set("Falha na extração")
            return False
        finally:
            self.progress.stop()

    def scriptDeletarBase(self, TextodeleteUser):
        with open(diretorioNomeArquivo, "w") as script:

            script.write("CONNECT sys/ALTIS@XE AS SYSDBA\n")

            script.write('ALTER SESSION SET "_oracle_script"=TRUE;\n')

            script.write("DROP USER " + TextodeleteUser + " CASCADE;\n")
            script.write("\n")

            script.write("EXIT")
            script.close()

    def scriptCriarUsuario(self, TextoToUser):
        with open(diretorioNomeArquivo, "w") as script:
            script.write("CONNECT sys/ALTIS@XE AS SYSDBA\n")

            script.write('ALTER SESSION SET "_oracle_script"=TRUE;\n')

            script.write("CREATE USER " + TextoToUser + "\n")
            script.write("IDENTIFIED BY ALTIS\n")
            script.write('DEFAULT TABLESPACE "DADOS"\n')
            script.write('TEMPORARY TABLESPACE "TEMPO"\n')
            script.write("QUOTA UNLIMITED ON DADOS\n")
            script.write("QUOTA UNLIMITED ON INDICES\n")
            script.write("ACCOUNT UNLOCK\n")
            script.write('PROFILE "DEFAULT"; ')
            script.write("\n")
            script.write("alter profile DEFAULT limit password_life_time UNLIMITED;\n")
            script.write("grant all privileges to " + TextoToUser + ";\n")
            script.write("\n")
            script.write("EXIT")

            script.write("\n")
            script.close()

    def scriptCompleto(self, TextodeleteUser, TextoToUser):
        with open(diretorioNomeArquivo, "w") as script:
            script.write("CONNECT sys/ALTIS@XE AS SYSDBA\n")

            script.write('ALTER SESSION SET "_oracle_script"=TRUE;\n')

            script.write("DROP USER " + TextodeleteUser + " CASCADE;\n")
            script.write("\n")

            script.write("CREATE USER " + TextoToUser + "\n")
            script.write("IDENTIFIED BY ALTIS\n")
            script.write('DEFAULT TABLESPACE "DADOS"\n')
            script.write('TEMPORARY TABLESPACE "TEMPO"\n')
            script.write("QUOTA UNLIMITED ON DADOS\n")
            script.write("QUOTA UNLIMITED ON INDICES\n")
            script.write("ACCOUNT UNLOCK\n")
            script.write('PROFILE "DEFAULT"; ')
            script.write("\n")
            script.write("alter profile DEFAULT limit password_life_time UNLIMITED;\n")
            script.write("grant all privileges to " + TextoToUser + ";\n")
            script.write("\n")
            script.write("EXIT")

            script.write("\n")
            script.close()

    def execute_upload_script(self):
        """Executa o comando para subir a base no Oracle"""
        try:
            self.status_var.set("Iniciando processo de subir a base...")
            self.progress.start()
            self.root.update()

            # Obter valores dos campos
            TextoToUser = "MASTER"  # Campo para usuário de destino
            TextofromUser = "MASTER"  # Campo para usuário de origem

            # Verificar se os campos estão preenchidos
            if not TextoToUser or not TextofromUser:
                messagebox.showerror("Erro", "Preencha os campos de usuário de origem e destino")
                return False

            # Obter caminho do arquivo extraído
            diretorioBase = self.last_extracted_file
            if not diretorioBase or not os.path.exists(diretorioBase):
                messagebox.showerror("Erro", "Arquivo de base não encontrado")
                return False

            # Executar o comando apropriado de acordo com a versão do Oracle
            self.scriptDeletarBase(TextofromUser)
            self.scriptCriarUsuario(TextoToUser)
            self.scriptCompleto(TextoToUser, TextofromUser)

            subprocess.call(["sqlplus", "/nolog", "@" + diretorioNomeArquivo])

            os.system(
                f"start /wait cmd /c D:/Oracle21c/bin/imp.exe {TextoToUser}/ALTIS@XE file={diretorioNomeArquivo} fromuser={TextofromUser} touser={TextoToUser}")


            self.status_var.set("Processo de importação iniciado com sucesso")
            messagebox.showinfo("Sucesso", "Processo de importação da base iniciado")
            return True
        except Exception as e:
            messagebox.showerror("Erro", f"Falha ao iniciar a importação: {str(e)}")
            self.status_var.set("Falha ao iniciar a importação")
            return False
        finally:
            self.progress.stop()

    def download_and_extract(self):
        """Baixa e extrai o arquivo selecionado"""
        filename = self.get_selected_file()
        if not filename:
            return

        dest_folder = self.dest_folder.get()
        if not dest_folder:
            messagebox.showwarning("Aviso", "Selecione uma pasta de destino")
            return

        # Caminho completo para o arquivo baixado
        dest_path = os.path.join(dest_folder, filename)

        # Executar em uma thread separada para não bloquear a UI
        def process():
            if self.download_file(filename, dest_path):
                if self.extract_file(dest_path, dest_folder):
                    messagebox.showinfo("Sucesso",
                                        f"Arquivo {filename} baixado e extraído com sucesso para {dest_folder}")

        threading.Thread(target=process).start()

    def full_process(self):
        """Executa o processo completo: baixar, extrair e subir a base"""
        filename = self.get_selected_file()
        if not filename:
            return

        dest_folder = self.dest_folder.get()
        if not dest_folder:
            messagebox.showwarning("Aviso", "Selecione uma pasta de destino")
            return

        # Caminho completo para o arquivo baixado
        dest_path = os.path.join(dest_folder, filename)

        # Executar em uma thread separada para não bloquear a UI
        def process():
            if self.download_file(filename, dest_path):
                if self.extract_file(dest_path, dest_folder):
                    self.execute_upload_script()

        threading.Thread(target=process).start()

    def select_folder(self):
        """Abre um diálogo para selecionar a pasta de destino"""
        folder = filedialog.askdirectory(initialdir=self.dest_folder.get())
        if folder:
            self.dest_folder.set(folder)

    def save_credentials(self):
        """Salva as credenciais do FTP em um arquivo"""
        try:
            config_dir = os.path.join(os.path.expanduser("~"), ".oracle_backup_manager")
            os.makedirs(config_dir, exist_ok=True)

            config_file = os.path.join(config_dir, "config.txt")

            with open(config_file, "w") as f:
                f.write(f"host={self.ftp_host.get()}\n")
                f.write(f"user={self.ftp_user.get()}\n")
                f.write(f"password={self.ftp_password.get()}\n")
                f.write(f"dest_folder={self.dest_folder.get()}\n")

            messagebox.showinfo("Configuração", "Credenciais salvas com sucesso")
        except Exception as e:
            messagebox.showerror("Erro", f"Falha ao salvar credenciais: {str(e)}")

    def load_credentials(self):
        """Carrega as credenciais do FTP de um arquivo"""
        config_file = os.path.join(os.path.expanduser("~"), ".oracle_backup_manager", "config.txt")

        if not os.path.exists(config_file):
            return

        try:
            with open(config_file, "r") as f:
                for line in f:
                    if "=" in line:
                        key, value = line.strip().split("=", 1)
                        if key == "host":
                            self.ftp_host.set(value)
                        elif key == "user":
                            self.ftp_user.set(value)
                        elif key == "password":
                            self.ftp_password.set(value)
                        elif key == "dest_folder":
                            self.dest_folder.set(value)

            self.status_var.set("Credenciais carregadas")
        except Exception as e:
            messagebox.showerror("Erro", f"Falha ao carregar credenciais: {str(e)}")


if __name__ == "__main__":
    root = tk.Tk()
    app = FTPBackupManager(root)
    root.mainloop()