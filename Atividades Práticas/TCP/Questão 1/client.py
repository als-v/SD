import socket 
import os   

ip = "127.0.0.1"
port = 7000

addr = (ip, port) 
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
client_socket.connect(addr)

comandos = [
    "EXIT",
    "TIME",
    "DATE",
    "FILES",
    "HELP",
    "DOWN 'nomeArquivo'"
]
def main():
    while True:
        entrada = input("comando: ")
        
        # Lista os comandos
        if(entrada == "HELP"):
            for c in comandos:
                print(c)
            break

        # Envia mensagem
        client_socket.send(entrada.encode("utf-8"))
        
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
            pass

        # Recebe os arquivos da pasta padrão
        if(entrada == "FILES"):
            pass

        # Faz o download de um arquivo
        if((entrada.split())[0] == 'DOWN'):
            pass         
        
main()