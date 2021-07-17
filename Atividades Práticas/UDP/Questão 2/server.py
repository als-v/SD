import socket
import hashlib
import os

ip = '127.0.0.1'
port = 5001
addr = (ip, port)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(addr)

def recebeArquivo():
    while(True):
        arquivo = b''

        data, addr = sock.recvfrom(1024)
        msg = data.decode('utf-8').split(';')

        nomeArquivo = msg[0]
        tamanhoArquivo = msg[1]
        checksum = msg[2]
        quantidadeEnvio = msg[3]
        # print('tamanhoNomeArquivo: ', tamanhoNomeArquivo, 'tamanhoArquivo: ', tamanhoArquivo, 'checksum: ', checksum, 'quantidadeEnvio: ', quantidadeEnvio, '\n')
        
        for _ in range(int(quantidadeEnvio)):
            data, addr = sock.recvfrom(1024)
            arquivo += data
        
        md5_hash = hashlib.md5()
        novoChecksum = arquivo
        md5_hash.update(novoChecksum)
        novoChecksum = md5_hash.hexdigest()
        retorno = bytearray(1)
        retorno[0] = 2

        if novoChecksum == checksum:
            with open('./server_files/' + nomeArquivo, 'wb') as file:
                file.write(arquivo)
            
            arquivos = os.listdir(path='./server_files')
            
            if nomeArquivo in arquivos:
                retorno[0] = 1
            else:
                retorno[0] = 2
        else:
            retorno[0] = 2

        sock.sendto(retorno, addr)

def main():
    recebeArquivo()

main()