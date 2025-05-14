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
        self.dest_folder = tk.StringVar(value=r"C:\subirbase")  # raw string
        self.client_folder = tk.StringVar(value="MIMACO")
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
        """Mostra até 3 backups mais recentes, seguidos de seus arquivos FILES (se houver)"""
        if not self.ftp:
            messagebox.showwarning("Aviso", "Conecte-se ao FTP primeiro")
            return

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
            self.ftp.cwd(selected_name)
            self.current_directory = self.ftp.pwd()

            files = []
            self.ftp.dir(files.append)

            backup_pattern = re.compile(r'.*\.(zip|7z|rar|tar|gz|bak|dmp)$', re.IGNORECASE)

            backups = []
            files_extra = []

            for item in files:
                parts = item.split()
                if len(parts) >= 8:
                    name_idx = item.find(parts[7]) + len(parts[7]) + 1
                    name = item[name_idx:].strip()

                    if not parts[0].startswith('d'):
                        date_str = f"{parts[5]} {parts[6]} {parts[7]}"
                        try:
                            current_year = datetime.datetime.now().year
                            parsed_date = parse(f"{date_str} {current_year}", fuzzy=True)
                            size = int(parts[4])
                            if "FILES" in name.upper():
                                files_extra.append((name, parsed_date, size))
                            elif backup_pattern.match(name):
                                backups.append((name, parsed_date, size))
                        except Exception as e:
                            print(f"Erro ao processar {name}: {e}")

            # Ordenar backups por data (decrescente)
            backups.sort(key=lambda x: x[1], reverse=True)
            top3_backups = backups[:3]

            # Limpar lista atual
            self.file_list.delete(*self.file_list.get_children())

            # Exibir backups, depois seus FILES
            for backup in top3_backups:
                name, date, size = backup
                self.file_list.insert("", tk.END, values=(name, date.strftime("%b %d %H:%M"), size))

            for backup in top3_backups:
                date = backup[1].date()
                matching_file = next((f for f in files_extra if f[1].date() == date), None)
                if matching_file:
                    name, date, size = matching_file
                    self.file_list.insert("", tk.END, values=(name, date.strftime("%b %d %H:%M"), size))

            if top3_backups:
                self.status_var.set(f"Backups + FILES exibidos para '{selected_name}'")
            else:
                self.status_var.set(f"Nenhum backup encontrado em '{selected_name}'")
                messagebox.showinfo("Informação", "Nenhum backup encontrado nessa pasta.")

        except Exception as e:
            messagebox.showerror("Erro", f"Falha ao acessar pasta '{selected_name}': {str(e)}")

    def get_selected_file(self):
        """Retorna o nome do arquivo selecionado ou None"""
        selection = self.file_list.selection()
        if not selection:
            messagebox.showwarning("Aviso", "Selecione um arquivo de backup")
            return None

        values = self.file_list.item(selection[0], "values")
        return values[0]

    def download_file(self, filename, dest_path):
        """Baixa um arquivo do FTP com barra de progresso real"""
        if not self.ftp:
            return False

        try:
            # Obter o tamanho total do arquivo
            total_size = self.ftp.size(filename)
            downloaded = 0

            self.status_var.set(f"Baixando {filename}...")
            self.progress.config(mode='determinate', maximum=total_size, value=0)
            self.progress.start(10)  # só pra mostrar algo no início
            self.root.update()

            # Criar diretório de destino
            os.makedirs(os.path.dirname(dest_path), exist_ok=True)

            with open(dest_path, 'wb') as f:
                def callback(data):
                    nonlocal downloaded
                    f.write(data)
                    downloaded += len(data)
                    self.progress['value'] = downloaded
                    self.root.update_idletasks()

                self.ftp.retrbinary(f'RETR {filename}', callback)

            self.status_var.set(f"Download de {filename} concluído")
            return True

        except Exception as e:
            messagebox.showerror("Erro", f"Falha ao baixar arquivo: {str(e)}")
            self.status_var.set("Falha no download")
            return False

        finally:
            self.progress.stop()
            self.progress['value'] = 0
            self.progress.config(mode='indeterminate')  # volta ao modo original

    def extract_file(self, filepath, dest_folder):
        """Extrai um arquivo para o destino especificado com barra de progresso real"""
        try:
            self.status_var.set(f"Extraindo {os.path.basename(filepath)}...")
            self.progress.config(mode='indeterminate')
            self.progress.start(10)
            self.root.update_idletasks()

            filename = os.path.basename(filepath).lower()
            os.makedirs(dest_folder, exist_ok=True)
            extracted_files = []

            # Progressivo real para formatos iteráveis
            def extract_with_progress(file_list, extractor_func):
                total = len(file_list)
                self.progress.stop()
                self.progress.config(mode='determinate', maximum=total, value=0)

                for i, item in enumerate(file_list, 1):
                    extractor_func(item)
                    self.progress['value'] = i
                    self.root.update_idletasks()

            if filename.endswith('.zip'):
                with zipfile.ZipFile(filepath, 'r') as zip_ref:
                    file_list = zip_ref.namelist()
                    extract_with_progress(file_list, lambda item: zip_ref.extract(item, dest_folder))
                    extracted_files = file_list

            elif filename.endswith('.7z'):
                with py7zr.SevenZipFile(filepath, mode='r') as z:
                    file_list = z.getnames()
                    extract_with_progress(file_list, lambda _: z.extractall(dest_folder))
                    extracted_files = file_list

            elif filename.endswith('.rar'):
                with rarfile.RarFile(filepath, 'r') as rf:
                    file_list = rf.namelist()
                    extract_with_progress(file_list, lambda item: rf.extract(item, dest_folder))
                    extracted_files = file_list

            elif filename.endswith('.tar'):
                with tarfile.open(filepath, 'r') as tar:
                    file_list = tar.getnames()
                    extract_with_progress(file_list, lambda item: tar.extract(item, dest_folder))
                    extracted_files = file_list

            elif filename.endswith('.gz'):
                if filename.endswith('.tar.gz'):
                    with tarfile.open(filepath, 'r:gz') as tar:
                        file_list = tar.getnames()
                        extract_with_progress(file_list, lambda item: tar.extract(item, dest_folder))
                        extracted_files = file_list
                else:
                    output_file = os.path.join(dest_folder, os.path.splitext(os.path.basename(filepath))[0])
                    with gzip.open(filepath, 'rb') as f_in:
                        with open(output_file, 'wb') as f_out:
                            data = f_in.read()
                            f_out.write(data)
                    extracted_files = [output_file]

            else:
                import shutil
                dest_file = os.path.join(dest_folder, os.path.basename(filepath))
                shutil.copy2(filepath, dest_file)
                extracted_files = [dest_file]

            # Armazenar caminho do .dmp ou outro extraído
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
            self.progress.config(mode='indeterminate')
            self.progress['value'] = 0

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

    def subir_base_oracle(self, diretorioBase):
        """Executa o processo completo de subir a base no Oracle"""
        try:
            self.status_var.set("Iniciando processo de subir a base...")
            self.progress.start()
            self.root.update()

            TextoToUser = "MASTER"
            TextofromUser = "ALTIS"

            if not TextoToUser or not TextofromUser:
                messagebox.showerror("Erro", "Preencha os campos de usuário de origem e destino")
                return False

            if not diretorioBase or not diretorioBase.lower().endswith(".dmp") or not os.path.exists(diretorioBase):
                messagebox.showerror("Erro", "Arquivo .dmp não encontrado.")
                return False

            self.scriptCompleto(TextodeleteUser=TextoToUser, TextoToUser=TextoToUser)

            subprocess.call(f'start cmd /k sqlplus /nolog @{diretorioNomeArquivo}', shell=True)

            comando_imp = (
                f'start cmd /k "C:/Oracle21c/bin/imp.exe {TextoToUser}/ALTIS@XE '
                f'file=\"{diretorioBase}\" fromuser={TextofromUser} touser={TextoToUser}"'
            )
            subprocess.call(comando_imp, shell=True)

            self.status_var.set("Processo de importação iniciado com sucesso")
            messagebox.showinfo("Sucesso", "Base Oracle foi preparada e a importação iniciada com sucesso.")
            return True

        except Exception as e:
            messagebox.showerror("Erro", f"Falha ao subir base: {str(e)}")
            self.status_var.set("Erro durante o processo de importação")
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

        dest_path = os.path.join(dest_folder, filename)

        def process():
            if self.download_file(filename, dest_path):
                if self.extract_file(dest_path, dest_folder):
                    caminho_dmp = self.last_extracted_file

                    # Verificações de segurança antes de prosseguir
                    if not caminho_dmp or not caminho_dmp.lower().endswith(".dmp") or not os.path.exists(caminho_dmp):
                        messagebox.showerror("Erro", "Arquivo .dmp extraído não encontrado ou inválido.")
                        self.status_var.set("Erro ao localizar o .dmp extraído")
                        return

                    # Normaliza o caminho para evitar problemas com barras invertidas
                    caminho_dmp = os.path.normpath(caminho_dmp)

                    self.subir_base_oracle(caminho_dmp)

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