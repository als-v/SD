'''
    ### QUESTÃO 1 - UDP ###
    # Autores: Juan e Alisson
    # Data de criação:      15/07/2021
    # Data de modificação:  19/07/2021
    # Descrição: Neste sistema o servidor recebe arquivos, mais especificamente 1024 bytes de cada vez. O server recebe
        uma mensagem contendo o nome do arquivo, tamanho do arquivo, a quantidade de vezes que o envio será realizado, 
        já que são enviados 1024 bytes de cada vez, concatenando esses bytes recebidos em um arquivo único, e por fim o checksum,
        para que seja possível verificar a integridade do arquivo recebido do cliente, enviando uma mensagem de resposta contendo
        1 caso o checksum do arquivo recebido seja igual ao enviado pelo cliente, ou 2 caso ocorra alguma problema com o upload.
    
    # cabecalho requisição:
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                 NomeDoArquivo: 255 bytes              |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |               TamanhoDoArquivo: 1 byte                |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                   checksum: 1 byte                    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |               QuantidadeEnvio: 1 byte                 |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    - Para cada quantidade de envio:
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |               arquivoBytes: 1024 bytes                |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    # cabecalho da resposta:
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |               StatusRequisição: 1 byte                |     1: sucesso / 2: falha
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

'''

import socket
import hashlib
import os

ip = '127.0.0.1'
port = 5001
addr = (ip, port)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
# Escuta a porta e o IP definidos anteriormente
sock.bind(addr)


def recebeArquivo():
    while(True):
        arquivo = b''

        # Recebe a mensagem do cliente que possui o nome do arquivo, tamanho do arquivo, o checksum e quantidade de vezes que o envio será executado
        data, addr = sock.recvfrom(1024)
        # Realiza uma divisão da mensagem recebida, utilizando como identificador para realizar essa divisão o caractere ';'
        # E também decodifica a mensagem
        msg = data.decode('utf-8').split(';')

        nomeArquivo = msg[0]
        tamanhoArquivo = msg[1]
        checksum = msg[2]
        quantidadeEnvio = msg[3]

        # Realiza um for tendo como parâmetro a quantidade de vezes que ocorrerá o envio do arquivo
        for _ in range(int(quantidadeEnvio)):
            # Recebe 1024 bytes do arquivo de cada vez, recendo também o ip e a porta de quem enviou
            data, addr = sock.recvfrom(1024)
            # Concatena os 1024 bytes recebidos
            arquivo += data

        # Após receber todo arquivo é realizado um checksum
        md5_hash = hashlib.md5()
        novoChecksum = arquivo
        md5_hash.update(novoChecksum)
        novoChecksum = md5_hash.hexdigest()

        # Será retornado como mensagem 1 se o checksum do arquivo recebido for igual ao checksum recebido do cliente
        # Caso contrário será retornado 2
        retorno = bytearray(1)
        retorno[0] = 2

        # Verifica se o checksum do arquivo é igual ao checksum enviado pelo cliente
        if novoChecksum == checksum:
            # Caso seja igual, o arquivo recebido será criado na pasta padrão do server
            with open('./server_files/' + nomeArquivo, 'wb') as file:
                file.write(arquivo)

            arquivos = os.listdir(path='./server_files')

            # Caso ele existe na pasta padrão do server
            # Será retornado uma mensagem com valor igual a 1
            if nomeArquivo in arquivos:
                retorno[0] = 1
            else:
                # Caso não exista, será retornado uma mensagem com o valor igual a 2
                retorno[0] = 2
        else:
            # Se o checksum for diferente, significa que ocorreu algum problema no upload do arquivo
            # Retorna então uma mensagem com valor igual a 2
            retorno[0] = 2

        # Envia a mensagem de resposta para o cliente, contendo também o ip e a porta
        sock.sendto(retorno, addr)


def main():
    recebeArquivo()


main()
