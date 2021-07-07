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

def programa(ip, port, con): 
    while True : 
        # recebe a mensagem
        msg = con.recv(1024)

        # decodifica a mensagem
        msg_str = msg.decode('utf-8')
        
        if(msg_str == 'EXIT'):
            print('Digitou:', msg_str)
            break

        if(msg_str == 'HELP'):
            con.send(comandos.encode('utf-8'))

        if(msg_str == "DATE"):
            print('Digitou:', msg_str)
            dataAtual = date.today().strftime('%d/%m/%Y')
            con.send(dataAtual.encode('utf-8'))

        if(msg_str == "TIME"):
            horarioServidor = datetime.now().strftime('%H:%M:%S')
            con.send(horarioServidor.encode('utf-8'))

        if(msg_str == "FILES"):
            qtdeFiles = os.listdir(path='./server_files')
            con.send(str(len(qtdeFiles)).encode('utf-8'))
            for dir in qtdeFiles:
                if(len(dir.split('.')) == 2):
                    time.sleep(0.1)
                    con.send(dir.encode('utf-8'))
            con.send("Alisson".encode('utf-8'))
            print(qtdeFiles)
            

        if((msg_str.split())[0] == 'DOWN'):
            print('Digitou:', msg_str)

def main():
    vetorThreads = []

    while 1:
        # Limite de 1 conexão
        serv_socket.listen(10)

        # Servidor escuta as conexões
        (con, (ip,port) ) = serv_socket.accept()
        print('Conexão aceita!\n', con, ip, port)

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