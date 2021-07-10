import os
import socket 

ip = "127.0.0.1"
port = 7000

addr = (ip, port) 
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
client_socket.connect(addr)

# Função que envia o cabeçalho da requisicao
def enviaCabecalho(entrada, nomeArquivo, comando):
    funcao = entrada.split()[0]

    if('ADDFILE' == funcao):
        arquivos = os.listdir(path='./client_files')

        if nomeArquivo not in arquivos:
            print('O arquivo solicitado não existe')
            return False

    elif('GETFILE' == funcao):
        arquivos = os.listdir(path='./client_files')

        if nomeArquivo not in arquivo:
            return True
    
    fileNameSize = len(nomeArquivo)
        
    # Vejo se o nome não passa de 255 bytes
    if fileNameSize < 256:
        messageType = 1
        commandIdentif = comando
            
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
        
        return True
    else:
        print('O tamanho do nome do arquivo excedeu o limite de caracteres')
        return False

def main():
    while True:
        entrada = input("comando: ")

        # ADDFILE
        if(entrada.split()[0] == "ADDFILE"):
            nomeArquivo = entrada.split()[1]

            if enviaCabecalho(entrada, nomeArquivo, 1):
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

        # DELETE
        if(entrada.split()[0] == "DELETE"):
            pass

        # Retorna a data do sistema
        if(entrada.split()[0] == "GETFILESLIST"):
            pass

        # Recebe os arquivos da pasta padrão
        if(entrada.split()[0] == "GETFILE"):
            nomeArquivo = entrada[1]
            
            if enviaCabecalho(entrada, nomeArquivo, 4):

main()