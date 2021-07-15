'''
    ### QUESTÃO 2 - TCP ###
    # Autores: Juan e Alisson
    # Data de criação:      10/07/2021
    # Data de modificação:  12/07/2021
    #Descrição: O servidor gerencia um conjunto de arquivos remotes, permitindo que vários usuários se conectem.
    O servidor deve permitir que as seguintes operações sejam realizadas:
        - ADDFILE: Cria um arquivo na pasta padrão do servidor, recebendo o arquivo byte a byte e o nome do arquvo como parâmetro.
        - DELETE: Recebe como parâmetro o nome de um arquivo, e caso ele exista na pasta padrão do servidor, ele será excluído.
        - GETFILESLIST: Retorna para o cliente uma lista com os nomes dos arquivos existentes na pasta padrão do servidor.
        - GETFILE: Recebe como parâmetro do cliente um nome de arquivo, e caso ele exista, retorna para o cliente o tamanho do arquivo
            e envia o arquivo em sí byte a byte.
'''

import os
import threading 
import socket 

host = ""
port = 7000
addr = (host,port) 

serv_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serv_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) 
serv_socket.bind(addr)

'''
### programa(ip, port, con) ###
# Metodo que executa as requisições do cliente
# Params: 
    - Ip: ip do cliente
    - Port: porta que o cliente se conectou
    - Conexao: a conexao realizada
'''
def programa(ip, port, con): 
    while True : 
        mensagem = con.recv(3)

        # Tipo da mensagem
        messageType = int(mensagem[0])

        # O indentificador do comando
        commandIdentif = int(mensagem[1])

        # Tamanho do nome do arquivo
        fileNameSize = int(mensagem[2])

        # Recebe o nome do arquivo
        nomeArquivo = b''
        for _ in range(fileNameSize):
            bytes = con.recv(1)
            nomeArquivo += bytes
        nomeArquivo = nomeArquivo.decode('utf-8')

        # ADDFILE
        if(messageType == 1 and commandIdentif == 1):
            tamanhoArquivo = int.from_bytes(con.recv(4), byteorder='big')
            # Recebe o arquivo
            arquivo = b''
            for _ in range(tamanhoArquivo):
                bytes = con.recv(1)
                arquivo += bytes
            
            # Salva o arquivo
            with open('./server_files/' + nomeArquivo, 'w+b') as file:
                file.write(arquivo)

            # Preparo a resposta
            resposta = bytearray(3)
            resposta[0] = 2
            resposta[1] = 1
            
            arquivos = os.listdir(path='./server_files')
            if nomeArquivo in arquivos:
                resposta[2] = 1
            else:
                resposta[2] = 2
            
            con.send(resposta)
        
        # DELETE
        if(messageType == 1 and commandIdentif == 2):
            arquivos = os.listdir(path='./server_files')
            
            resposta = bytearray(3)
            resposta[0] = 2
            resposta[1] = 1

            # caso o arquivo exista no diretorio
            if nomeArquivo in arquivos:
                os.remove('./server_files/' + nomeArquivo)
                
                # remove arquivo e verifica se realmente foi excluido
                arquivos = os.listdir(path='./server_files')
                if nomeArquivo in arquivos:
                    resposta[2] = 2
                else:
                    resposta[2] = 1
            else:
                resposta[2] = 2
            
            con.send(resposta)
        
        # GETFILESLIST
        if(messageType == 1 and commandIdentif == 3):
            resposta = bytearray(3)
            resposta[0] = 2
            resposta[1] = 3

            arquivos = os.listdir(path='./server_files')
            
            # caso nao ha nenhum arquivo no diretorio
            if(len(arquivos) == 0):
                print("Não há nenhum arquivo para ser listado")
            
            #Envia a resposta
            resposta[2] = 1
            con.send(resposta)
    
            #Envia a quantidade de arquivos
            con.send(len(arquivos).to_bytes(2, byteorder='big'))

            #Envia o tamanho do nome do arquivo e o nome do arquivo
            for arquivo in arquivos:
               if len(arquivo) < 256:
                    con.send((len(arquivo).to_bytes(1, byteorder='big')))
                    for nome in arquivo:
                        byte = str.encode(nome)
                        con.send(byte)

        # GETFILE
        if (messageType == 1 and commandIdentif == 4):
            arquivos = os.listdir(path='./server_files')

            resposta = bytearray(3)
            resposta[0] = 2
            resposta[1] = commandIdentif
            
            # se o arquivo existir
            if nomeArquivo in arquivos:
                resposta[2] = 1
                con.send(resposta)

                # envia o tamanho do arquivo
                tamanhoArquivo = (os.stat('./server_files/' + nomeArquivo).st_size).to_bytes(4, "big")
                con.send(tamanhoArquivo)
                
                # envia o arquivo byte a byte
                with open('./server_files/' + nomeArquivo, 'rb') as file:
                    byte = file.read(1)
                    while byte != b'':
                        con.send(byte)
                        byte = file.read(1)
            else:
                resposta[2] = 2
    
'''
### main() ###
# Metodo que realiza a conexão do cliente
# Params: 
    - none
'''
def main():
    vetorThreads = []

    while 1:
        # Limite de 20 conexões
        serv_socket.listen(20)

        # Servidor escuta as conexões
        (con, (ip,port) ) = serv_socket.accept()
        print('Sessão com o cliente: ', ip, ', na porta: ', port, ', foi estabelecida!')

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