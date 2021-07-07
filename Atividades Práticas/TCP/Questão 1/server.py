import threading 
import socket 
from datetime import datetime

host = ""
port = 7000
addr = (host,port) 

serv_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serv_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) 
serv_socket.bind(addr)

BUFFER_SIZE = 4

def programa(ip, port, con): 
    while True : 
        # recebe a mensagem
        msg = con.recv(1024)

        # decodifica a mensagem
        msg_str = msg.decode('utf-8')
        
        if(msg_str == 'EXIT'):
            print('Digitou:', msg_str)
            break

        if(msg_str == "DATE"):
            print('Digitou:', msg_str)

        if(msg_str == "TIME"):
            horarioServidor = datetime.now().strftime('%H:%M:%S')
            con.send(horarioServidor.encode('utf-8'))
            # print('Horário:', horarioServidor)

        if(msg_str == "FILES"):
            print('Digitou:', msg_str)

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