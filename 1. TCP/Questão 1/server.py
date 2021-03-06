'''
    ### QUESTÃO 1 - TCP ###
    # Autores: Juan e Alisson
    # Data de criação:      07/07/2021
    # Data de modificação:  15/07/2021
    #Descrição: Processa mensagens recebidas do cliente, sendo possível executar as seguintes operações:
        - TIME: Retorna para o cliente o horário do sistema
        - DATE: Retorna para o ciente a data do sistema
        - FILES: Retorna para o cliente a quantidade e o nome de arquivos presentes na pasta padrão.
        - DOWN: Recebe como parâmetro o nome de um arquivo, e caso ele exista na pasta padrão, envia byte a byte para o cliente.
        - EXIT: Notifica que o cliente desconectou do servidor. 
'''

import threading 
import socket 
from datetime import date, datetime
import os
import time

host = ""
port = 7000
addr = (host,port) 

serv_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serv_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) 
serv_socket.bind(addr)

comandos = "========= Servidor do Juan e do Alisson =========\n;EXIT: finaliza a conexão com o servidor;TIME: retorna a hora do sistema;DATE: retorna a data do sistema;FILES: retorna os arquivos da pasta compartilhada;HELP: lista os comandos;DOWN 'nomeArquivo': faz o download de um arquivo;"

'''
### programa(ip, port, con) ###
# Metodo que executa as requisições do cliente
# Params: 
    - Ip: ip do cliente
    - Port: porta que o cliente se conectou
    - Conexao: a conexao realizada
'''
def programa(ip, port, con): 
    while True : 
        # Recebe a mensagem
        msg = con.recv(1024)

        # Decodifica a mensagem
        msg_str = msg.decode('utf-8')
        
        # Notifica servidor sobre a saída do cliente
        if(msg_str == 'EXIT'):
            print('Cliente com o ip: ', ip, ', na porta: ', port, ', foi desconectado!')
            break

        # Mostra os comandos do servidor
        if(msg_str == 'HELP'):
            con.send(comandos.encode('utf-8'))

        # Retorna a data do servidor
        if(msg_str == "DATE"):
            dataAtual = date.today().strftime('%d/%m/%Y')
            con.send(dataAtual.encode('utf-8'))

        # Retorna o horario do servidor
        if(msg_str == "TIME"):
            horarioServidor = datetime.now().strftime('%H:%M:%S')
            con.send(horarioServidor.encode('utf-8'))

        # Lista os arquivos do servidor
        if(msg_str == "FILES"):
            # todos os arquivos do diretorio
            arquivos = os.listdir(path='./server_files')
            con.sendall(str(len(arquivos)).encode('utf-8'))

            # para cada um dos arquivos (desconsiderando as pastas), envia o nome deles
            for dir in arquivos:
                print(dir)
                con.sendall(dir.encode('utf-8'))

        # Baixar um arquivo do servidor
        if((msg_str.split())[0] == 'DOWN'):
            # todos os arquivos do diretorio
            arquivos = os.listdir(path='./server_files')
            nomeArquivo = msg_str.split()[1]

            # caso tenha o arquivo no diretorio
            if nomeArquivo in arquivos:
                # envia os bytes do arquivo
                con.send(str(os.stat('./server_files/' + nomeArquivo).st_size).encode('utf-8'))

                # abre o arquivo, e envia byte a byte 
                with open('./server_files/' + nomeArquivo, 'r+b') as file:
                    byte = file.read(1)

                    while byte != b'':
                        con.send(byte)
                        byte = file.read(1)
            # caso não tenha o arquivo
            else:
                con.send(str(0).encode('utf-8'))

'''
### main() ###
# Metodo que realiza a conexão do cliente
# Params: 
    - none
'''
def main():
    vetorThreads = []

    while 1:
        # Limite de 20 conexões
        serv_socket.listen(20)

        # Servidor escuta as conexões
        (con, (ip,port) ) = serv_socket.accept()
        print('Sessão com o cliente: ', ip, ', na porta: ', port, ', foi estabelecida!')

        # Cria e inicia uma thread para cada cliente que chega
        thread = threading.Thread(target=programa, args=(ip, port, con, ))
        thread.start()
        
        # Adiciona ao vetor de threads
        vetorThreads.append(thread)
        
    # Aguarda todas as threads serem finalizadas
    for t in vetorThreads: 
        t.join()

    # Fecha conexao
    serv_socket.close()

main()