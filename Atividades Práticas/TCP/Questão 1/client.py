import socket 
import os   

ip = "127.0.0.1"
port = 7000

addr = (ip, port) 
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
client_socket.connect(addr)

def main():
    while True:
        entrada = input("comando: ").upper()

        # Envia mensagem
        client_socket.send(entrada.encode("utf-8"))
        # client_socket.send(bytearray(entrada, encoding='utf-8'))
        
        # Lista os comandos
        if(entrada == "HELP"):
            comandos = client_socket.recv(1024).decode('utf-8')
            for comandos in comandos.split(';'):
                print(comandos)

        # Envia a mensagem e fecha a conexão
        if(entrada == "EXIT"):
            client_socket.close()
            break

        # Retorna a hora do sistema
        if(entrada == "TIME"):
            print(client_socket.recv(1024).decode("utf-8"))
            pass
        
        # Retorna a data do sistema
        if(entrada == "DATE"):
            print(client_socket.recv(1024).decode('utf-8'))

        # Recebe os arquivos da pasta padrão
        if(entrada == "FILES"):
            pass

        # Faz o download de um arquivo
        if((entrada.split())[0] == 'DOWN'):
            pass         
        
main()