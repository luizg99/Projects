# Importação de bibliotecas.
import os
import datetime
import zipfile
import time
import ftplib
import sys

vUsuario = 'PINHEIRAO'

# Abrindo o cmd, utilizando o comando do oracle. Local que o backup será salvo.
os.system("start /wait cmd /c sqlplus sys as sysdba")
os.system("start /wait cmd /c altis")
os.system("start /wait cmd /c alter session set '_oracle_script'=TRUE")
os.system("start /wait cmd /c alter system enable restricted session")
os.system("start /wait cmd /c DROP USER " + vUsuario + " CASCADE")
os.system("start /wait cmd /c shutdown")
os.system("start /wait cmd /c startup")
# Criando usuário
os.system("start /wait cmd /c create user " + vUsuario + " identified by DADOS default tablespace DADOS temporary tablespace TEMPO quota unlimited on DADOS quota unlimited on INDICES account unlock")
os.system("start /wait cmd /c profile 'DEFAULT'")
# Definindo grants do usuário
os.system("start /wait cmd /c grant CREATE PROCEDURE to " + vUsuario)
os.system("start /wait cmd /c grant CREATE TRIGGER to " + vUsuario)
os.system("start /wait cmd /c grant CREATE TABLE to " + vUsuario)
os.system("start /wait cmd /c grant CREATE VIEW to " + vUsuario)
os.system("start /wait cmd /c grant CREATE SEQUENCE to " + vUsuario)
os.system("start /wait cmd /c grant CONNECT to " + vUsuario)
os.system("start /wait cmd /c grant CREATE JOB to " + vUsuario)
os.system("start /wait cmd /c grant CREATE DATABASE LINK to " + vUsuario)
os.system("start /wait cmd /c grant CREATE MATERIALIZED VIEW, ALTER ANY MATERIALIZED VIEW, QUERY REWRITE to " + vUsuario)





