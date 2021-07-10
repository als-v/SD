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

        # Adicionar um novo arquivo
        if(entrada.split()[0] == "ADDFILE"):
            nomeArquivo = entrada.split()[1]
            arquivos = os.listdir(path='./client_files')

            if nomeArquivo in arquivos:
                fileNameSize = str.encode(str(len(nomeArquivo)))

                if len(fileNameSize) < 256:
                    messageType = str.encode(str(1))
                    commandIdentif = str.encode(str(1))
                    
                    client_socket.send(messageType)
                    client_socket.send(commandIdentif)
                    client_socket.send(fileNameSize)

                    for nome in nomeArquivo:
                        byte = str.encode(nome)
                        client_socket.send(byte) 
                    
                    tamanhoArquivo = (os.stat('./client_files/' + nomeArquivo).st_size).to_bytes(4, "big")
                    client_socket.send(tamanhoArquivo)

                    arquivoBytes = b''
                    with open('./client_files/' + nomeArquivo, 'rb') as file:
                        byte = file.read(1)
                        while byte != b'':
                            client_socket.send(byte)
                            byte = file.read(1)

                    print('aguardando resposta....')
                    resposta = client_socket.recv(3)
                    print('resposta: ', resposta)

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