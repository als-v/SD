import os
import threading 
import socket 

host = ""
port = 7000
addr = (host,port) 

serv_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serv_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) 
serv_socket.bind(addr)

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

            if nomeArquivo in arquivos:
                os.remove('./server_files/' + nomeArquivo)
                
                arquivos = os.listdir(path='./server_files')
                if nomeArquivo in arquivos:
                    resposta[2] = 2
                else:
                    resposta[2] = 1

            else:
                resposta[2] = 2
            
            con.send(resposta)
        
        if commandIdentif == 3:
            pass
        
        if commandIdentif == 4:

def main():
    vetorThreads = []

    while 1:
        # Limite de 5 conexões
        serv_socket.listen(5)

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