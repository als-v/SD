import socket
import threading
import os
import hashlib
import math
import time

ip = '127.0.0.1'
port = 5001
addr = (ip, port)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

def enviaArquivo():
    while(True):
        nomeArquivo = input('Digite o nome do arquivo que deseja enviar: ')
        arquivos = os.listdir(path='./client_files')

        if nomeArquivo in arquivos:
            if len(nomeArquivo) < 256:
                tamanhoArquivo = os.stat('./client_files/' + nomeArquivo).st_size
                quantidadeEnvio = math.ceil(tamanhoArquivo/1024)
                md5_hash = hashlib.md5()

                with open("./client_files/a.txt", "rb") as file:
                    checksum = file.read()
                    md5_hash.update(checksum)
                    checksum = md5_hash.hexdigest()

                    msg = nomeArquivo + ';' + str(tamanhoArquivo) + ';' + checksum + ';' + str(quantidadeEnvio)
                    # print(nomeArquivo, '\n', tamanhoArquivo, '\n', checksum, '\n', quantidadeEnvio)
                    sock.sendto(msg.encode('utf-8'), addr)

                with open("./client_files/a.txt", "rb") as file:
                    byte = file.read(1024)

                    while byte != b'':
                        sock.sendto(byte, addr)
                        byte = file.read(1024)
                
                dataReceive, addrReceive = sock.recvfrom(1)

                if dataReceive[0] == 1:
                    print('Operação realizada com sucesso!')
                else:
                    print('Erro ao realizar a operação')

            else:
                print('ERRO: O nome do arquivo exede o limite de caracteres')
        else:
            print('ERRO: Arquivo não encontrado')

def main():
    enviarThread = threading.Thread(target=enviaArquivo)
    enviarThread.start()

main()