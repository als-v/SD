'''
    ### QUESTÃO 1 - TCP ###
    # Autores: Juan e Alisson
    # Data de criação:      10/07/2021
    # Data de modificação:  12/07/2021
    #Descrição: Envia mensagens para o servidor, podendo solicitar 5 operações. Essas operações são:
        - TIME: Recebe do servidor o horário do sistema
        - DATE: Recebe do servidor a data do sistema
        - FILES: Recebe a quantidade e o nome de arquivos presentes na pasta padrão do servidor.
        - DOWN: Faz o download de um arquivo presente na pasta do servidor, enviando como parâmetro o nome do arquivo desejado.
        - EXIT: Finaliza a conexão com o servidor  
'''

import socket 

ip = "127.0.0.1"
port = 7000

addr = (ip, port) 
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
client_socket.connect(addr)

'''
### main() ###
# Metodo que pega a entrada do cliente, e aguarda
# a resposta do servidor.
# Params: 
    - none
'''
def main():
    while True:
        comando = input("comando: ")
        entrada = ''

        # formatacao do primeiro comando para maiusculo
        if len(comando.split()) > 1:
            for index, comandos in enumerate(comando.split()):
                if index == 0:
                    entrada = comandos.upper()
                else:
                    entrada = entrada + ' ' + comandos
        else:
            entrada = comando.upper()

        # Envia mensagem
        client_socket.send(entrada.encode("utf-8"))
        
        # Lista os comandos
        if(entrada == "HELP"):
            comandos = client_socket.recv(1024).decode('utf-8')
            for comandos in comandos.split(';'):
                print(comandos)

        # Envia a mensagem e fecha a conexão
        if(entrada == "EXIT"):
            client_socket.close()
            break

        # Retorna o horario do sistema
        if(entrada == "TIME"):
            print(client_socket.recv(1024).decode("utf-8"))
        
        # Retorna a data do sistema
        if(entrada == "DATE"):
            print(client_socket.recv(1024).decode('utf-8'))

        # Recebe os arquivos da pasta padrão
        if(entrada == "FILES"):
            files = client_socket.recv(1024).decode('utf-8')
            print('Numero de arquivos encontrados: ', files)
            numFiles = int(files)
            listaArquivos = []
            posicaoLista = 0

            while posicaoLista < numFiles:
                arquivoNomes = client_socket.recv(1024).decode('utf-8')
                listaArquivos.append(arquivoNomes)
                print("Arquivos", listaArquivos[posicaoLista])
                posicaoLista += 1
            
            # for arq in listaArquivos:

        # Faz o download de um arquivo
        if((entrada.split())[0] == 'DOWN'):
            bytes = client_socket.recv(1024).decode('utf-8')  
            print('Total de bytes: ', bytes)
            
            # Verifica se foi encontrado um arquivo
            if int(bytes) > 0:
                nomeArquivo = entrada.split()[1]
                arquivo = b''

                # Recebo os bytes do servidor
                for i in range(int(bytes)):
                    fileBytes = client_socket.recv(1)
                    print(fileBytes)
                    arquivo += fileBytes

                # Salvo em um novo arquivo
                with open('./client_files/' + nomeArquivo, 'wb') as file:
                    file.write(arquivo)

            elif int(bytes) < 1:
                print('Arquivo não foi encontrado')
        
main()