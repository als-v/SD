import os
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

        # ADDFILE
        if(entrada.split()[0] == "ADDFILE"):
            nomeArquivo = entrada.split()[1]
            arquivos = os.listdir(path='./client_files')

            # Vejo se existe o arquivo
            if nomeArquivo in arquivos:
                fileNameSize = len(nomeArquivo)
                
                # Vejo se o nome não passa de 255 bytes
                if fileNameSize < 256:
                    messageType = 1
                    commandIdentif = 1
                    
                    # Cria o cabecalho
                    cabecalho = bytearray(3)
                    cabecalho[0] = messageType
                    cabecalho[1] = commandIdentif
                    cabecalho[2] = fileNameSize

                    client_socket.send(cabecalho)

                    # Envia o nome do arquivo
                    for nome in nomeArquivo:
                        byte = str.encode(nome)
                        client_socket.send(byte) 
                    
                    # Envia o tamanho do arquivo
                    tamanhoArquivo = (os.stat('./client_files/' + nomeArquivo).st_size).to_bytes(4, "big")
                    client_socket.send(tamanhoArquivo)

                    # Envia o arquivo
                    with open('./client_files/' + nomeArquivo, 'rb') as file:
                        byte = file.read(1)
                        while byte != b'':
                            client_socket.send(byte)
                            byte = file.read(1)

                    # Espera a resposta
                    resposta = client_socket.recv(3)
                    
                    if(int(resposta[0]) == 2 and int(resposta[1]) == 1):
                        if(int(resposta[2]) == 1):
                            print('Arquivo copiado com sucesso')
                        elif(int(resposta[2]) == 2):
                            print('Erro ao copiar arquivo')
                else:
                    print('O tamanho do nome do arquivo excedeu o limite de caracteres')
            else:
                print('O arquivo solicitado não existe')


        if(entrada.split()[0] == "DELETE"):
            pass
        
        # Retorna a data do sistema
        if(entrada.split()[0] == "GETFILESLIST"):
            pass

        # Recebe os arquivos da pasta padrão
        if(entrada.split()[0] == "GETFILE"):
            pass
main()