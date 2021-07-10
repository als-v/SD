import socket 

ip = "127.0.0.1"
port = 7000

addr = (ip, port) 
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
client_socket.connect(addr)

def main():
    while True:
        entrada = input("comando: ")

        if(entrada.split()[0] == "ADDFILE"):
            pass

        if(entrada.split()[0] == "DELETE"):
            pass
        
        # Retorna a data do sistema
        if(entrada.split()[0] == "GETFILESLIST"):
            pass

        # Recebe os arquivos da pasta padrão
        if(entrada.split()[0] == "GETFILE"):
            pass
main()