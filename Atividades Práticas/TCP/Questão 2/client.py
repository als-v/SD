'''
    ### QUESTÃO 2 - TCP ###
    # Autores: Juan e Alisson
    # Data de criação:      10/07/2021
    # Data de modificação:  12/07/2021
    #Descrição: O cliente é capaz de envia mensagens para o servidor, tendo como propósito realizar operações sobre os arquivos
        existentes na pasta padrão do servidor. As mensagens que podem ser enviadas para o servidor são:
        - ADDFILE: Envia byte a byte para o servidor um arquivo que existe na pasta padrão do cliente, copiando o mesmo arquivo 
            para a pasta padrão do servidor.
        - DELETE: Passa como parâmetro um nome de arquivo, e caso ele exista na pasta do servidor, será excluído.
        - GETFILESLIST: Recebe do servidor uma lista com todos os arquivos existentes na pasta padrão do servidor.
        - GETFILE: Passa como parâmetro um nome de arquivo para o servidor, e caso ele exista na pasta do servidor, 
            receberá como resposta o arquivo byte a byte, e o tamanho do arquivo. Copiando então o arquivo que estava presente
            no servidor para o cliente.
'''

import os
import socket 

ip = "127.0.0.1"
port = 7000

addr = (ip, port) 
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
client_socket.connect(addr)

'''
### enviaCabecalho(entrada, nomeArquivo, comando) ###
# Metodo que envia o cabecalho esperado de todas as 
# requisicoes ao servidor.
# Params: 
    - entrada: comando digitado pelo cliente
    - nomeArquivo: nome do arquivo passado pelo comando
    - comando: o identificado do comando
'''
def enviaCabecalho(entrada, nomeArquivo, comando):
    funcao = entrada.split()[0]

    # caso não exista o arquivo
    if('ADDFILE' == funcao):
        arquivos = os.listdir(path='./client_files')

        if nomeArquivo not in arquivos:
            print('O arquivo solicitado não existe')
            return False

    # caso o arquivo já exista
    elif('GETFILE' == funcao):
        arquivos = os.listdir(path='./server_files')

        if nomeArquivo not in arquivos:
            print('O arquivo solicitado não existe')
            return False
    
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

'''
### main() ###
# Metodo que pega a entrada do cliente, e aguarda
# a resposta do servidor.
# Params: 
    - none
'''
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

        # GETFILESLIST
        if(entrada.split()[0] == "GETFILESLIST"):
            # str nomeArquivo
            nomeArquivo = ""
            #Lista de arquivos
            listaNomeArquivo = []

            #Caso o retorno da função enviaCabecalho seja verdadeira
            if enviaCabecalho(entrada, nomeArquivo, 3):
                #Espera o retorno da resposta 
                resposta = client_socket.recv(3)
                respostaTipo = int(resposta[0])
                respostaComando = int(resposta[1])
                respostaStatus = int(resposta[2])

                if (respostaTipo == 2 and respostaComando == 3):
                    #Caso o status da resposta seja igual a SUCESS
                    if (respostaStatus == 1):
                        #Recebe a quantidade de arquivos
                        numeroArquivos = int.from_bytes(client_socket.recv(2), byteorder='big')
                        print("Número de Arquivos:", numeroArquivos)

                        #Recebe o número de arquivos byte a byte
                        for _ in range(numeroArquivos):
                            tamanhoNomeArquivo = int.from_bytes(client_socket.recv(1), byteorder='big')
                            print("\tTamanho do Arquivo:", tamanhoNomeArquivo)
                            
                            #Recebe a nome dos arquivos e guarda em uma lista
                            for _ in range(tamanhoNomeArquivo):
                                caract = client_socket.recv(1)
                                nomeArquivo += caract.decode('utf-8')
                            listaNomeArquivo.append(nomeArquivo)
                            print("\tNome do Arquivo:", nomeArquivo)
                            nomeArquivo = ""
                    else:
                        print("Erro ao listar os arquivos")
                            

        # GETFILE
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

                        #Recebe byte a byte
                        arquivo = b''
                        for _ in range(tamanhoArquivo):
                            byte = client_socket.recv(1)
                            arquivo += byte

                        #Cria um novo arquivo
                        with open ('./client_files/' + nomeArquivo, 'w+b') as file:
                            file.write(arquivo)
                        print("Download concluído com sucesso")
                    
                    else:
                        print("Arquivo não encontrado")
 
main()