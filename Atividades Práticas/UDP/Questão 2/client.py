'''
    ### QUESTÃO 1 - UDP ###
    # Autores: Juan e Alisson
    # Data de criação:      15/07/2021
    # Data de modificação:  19/07/2021
    #Descrição: Neste sistema o cliente pode realizar uploads de arquivos via UDP para um servidor.
        Esses arquivo devem estar presentes na pasta padrão do cliente para que seja possível realizar o upload, 
        enviando como mensagem o nome do arquivo, tamanho do arquivo, a quantidade de vezes que o envio será realizado, 
        já que são enviados 1024 bytes de cada vez, e o checksum, para que seja possível verificar a integridade do arquivo
        recebido pelo server. Por fim o cliente recebe uma mensagem de resposta contendo 1 caso o upload tenha ocorrido com sucesso, 
        ou 2 caso tenha ocorrido algum problema com o upload.
'''

import socket
import threading
import os
import hashlib
import math

# Ip e Porta que será utilizada para comunicação entre cliente e servidor
ip = '127.0.0.1'
port = 5001
addr = (ip, port)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)


def enviaArquivo():
    while(True):
        # Recebe como input o nome do arquivo que deseja realizar o upload
        nomeArquivo = input('Digite o nome do arquivo que deseja enviar: ')
        # Todos os arquivos presentes na pasta padrão do cliente
        arquivos = os.listdir(path='./client_files')

        # Percorre todos os arquivos presentes na pasta padrão do cliente e verifica se o arquivo que deseja fazer o upload
        # existe na pasta padrão do cliente
        if nomeArquivo in arquivos:

            # Verifica se o nome do arquivo possui menos de 255 bytes
            if len(nomeArquivo) < 256:
                # Atribui a variavel tamanhoArquivo o tamanho do arquivo que será feito o upload
                tamanhoArquivo = os.stat(
                    './client_files/' + nomeArquivo).st_size
                # Divide o tamanho do arquivo por 1024 para saber quantas vezes terá que realizar o envio do arquivo para o server, já que mandamos 1024 bytes de cada vez
                quantidadeEnvio = math.ceil(tamanhoArquivo/1024)
                md5_hash = hashlib.md5()

                # Abre o arquivo desejado no modo de leitura de bytes
                with open("./client_files/" + nomeArquivo, "rb") as file:
                    # Realiza o checksum do arquivo
                    checksum = file.read()
                    md5_hash.update(checksum)
                    checksum = md5_hash.hexdigest()

                    # Concatena o nome do arquivo, o tamano do arquivo e o checksum do arquivo que será enviado em uma só mensagem
                    msg = nomeArquivo + ';' + \
                        str(tamanhoArquivo) + ';' + \
                        checksum + ';' + \
                        str(quantidadeEnvio)
                    # Envia a mensagem, o ip e a porta para o server, encodando a mensagem ao enviar
                    sock.sendto(msg.encode('utf-8'), addr)

                # Abre novamente o arquivo
                with open("./client_files/" + nomeArquivo, "rb") as file:
                    # Lê 1024 bytes
                    byte = file.read(1024)

                    # Lerá 1024 bytes de cada vez até que chegue ao final do arquivo
                    while byte != b'':
                        # Envia o arquivo, 1024 bytes de cada vez, junto com o ip e a porta
                        sock.sendto(byte, addr)
                        # Lê os próximos 1024 bytes do arquivo
                        byte = file.read(1024)

                # Recebe uma mensagem como resposta do server, contém o status do upload, assim como o ip e a porta.
                # Essa mensagem tem tamanho de um byte
                dataReceive, addrReceive = sock.recvfrom(1)

                # Verifica o status da mensagem
                # Caso a mensagem de resposta seja 1, a operação foi bem sucedida
                if dataReceive[0] == 1:
                    print('Operação realizada com sucesso!')
                else:
                    # Caso contrário, retornará 2 como resposta, indicando que ocorreu algum erro ao realizar o upload
                    print('Erro ao realizar a operação')

            else:
                # Se o tamanho do arquivo possui mais do que 255 bytes, será retornado uma mensagem de erro
                print('ERRO: O nome do arquivo exede o limite de caracteres')
        else:
            # Caso o nome do arquivo não esteja presente na lista de arquivos, uma mensagem será retornada avisando que o arquivo não foi encontrado
            print('ERRO: Arquivo não encontrado')


def main():
    # Cria uma thread para realizar a operação de envio do arquivo
    enviarThread = threading.Thread(target=enviaArquivo)
    enviarThread.start()


main()
