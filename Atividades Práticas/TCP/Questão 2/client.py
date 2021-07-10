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

        if nomeArquivo in arquivos:
            print('O arquivo solicitado já existe')
    
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
            print("nome",nome)
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
                respostaTipo = int(resposta[0])
                respostaComando = int(resposta[1])
                respostaStatus = int(resposta[2])
                
                if(respostaTipo == 2 and respostaComando == 1):
                    if(respostaStatus == 1):
                        print('Arquivo copiado com sucesso')
                    elif(respostaStatus == 2):
                        print('Erro ao copiar arquivo')

        # DELETE
        if(entrada.split()[0] == "DELETE"):
            nomeArquivo = entrada.split()[1]

            if enviaCabecalho(entrada, nomeArquivo, 2):
                # Espera a resposta
                resposta = client_socket.recv(3)
                respostaTipo = int(resposta[0])
                respostaComando = int(resposta[1])
                respostaStatus = int(resposta[2])

                if(respostaTipo == 2 and respostaComando == 1):
                    if(respostaStatus == 1):
                        print('Arquivo deletado com sucesso')
                    elif(respostaStatus == 2):
                        print('Erro ao deletar arquivo')

        # Retorna a data do sistema
        if(entrada.split()[0] == "GETFILESLIST"):
            # str nomeArquivo
            nomeArquivo = ""
            listaNomeArquivo = []
            #Caso o retorno da função enviaCabecalho seja verdadeira
            if enviaCabecalho(entrada, nomeArquivo, 3):
                resposta = client_socket.recv(3)
                respostaTipo = int(resposta[0])
                respostaComando = int(resposta[1])
                respostaStatus = int(resposta[2])

                if (respostaTipo == 2 and respostaComando == 3):
                    print("Aqui")
                    if (respostaStatus == 1):
                        numeroArquivos = int.from_bytes(client_socket.recv(2), byteorder='big')
                        print("Número de Arquivos:", numeroArquivos)

                        for _ in range(numeroArquivos):
                            tamanhoNomeArquivo = int.from_bytes(client_socket.recv(1), byteorder='big')
                            print("\tTamanho do Arquivo:", tamanhoNomeArquivo)
                            
                            for _ in range(tamanhoNomeArquivo):
                                caract = client_socket.recv(1)
                                nomeArquivo += caract.decode('utf-8')
                            listaNomeArquivo.append(nomeArquivo)
                            print("\tNome do Arquivo:", nomeArquivo)
                            nomeArquivo = ""
                    else:
                        print("Erro ao listar os arquivos")
                            

        # Recebe os arquivos da pasta padrão
        if(entrada.split()[0] == "GETFILE"):
            #Verifica o nome do arquivo
            nomeArquivo = entrada.split()[1]
            
            #Caso o retorno da função enviaCabecalho seja verdadeira
            if enviaCabecalho(entrada, nomeArquivo, 4):
                #Resposta do servidor
                resposta = client_socket.recv(3)
                respostaTipo = int(resposta[0])
                respostaComando = int(resposta[1])
                respostaStatus = int(resposta[2])

                if respostaTipo == 2 and respostaComando == 4:
                    #Verifica se o status da resposta é igual a 1 (1 = SUCESS)
                    if respostaStatus == 1:
                        tamanhoArquivo = int.from_bytes(client_socket.recv(4), byteorder='big')
                        print(tamanhoArquivo)

                        #Recebe byte a byte
                        arquivo = b''
                        for _ in range(tamanhoArquivo):
                            byte = client_socket.recv(1)
                            print(byte)
                            arquivo += byte

                        #Cria um novo arquivo
                        with open ('./client_files/' + nomeArquivo, 'w+b') as file:
                            file.write(arquivo)
                        print("Download concluído com sucesso")
                    
                    else:
                        print("Arquivo não encontrado")
 
                
                
main()