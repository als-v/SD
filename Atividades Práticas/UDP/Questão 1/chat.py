'''
    ### QUESTÃO 1 - UDP ###
    # Autores: Juan e Alisson
    # Data de criação:      15/07/2021
    # Data de modificação:  16/07/2021
    #Descrição: O código abaixo possibilita que clientes troquem mensagem entre sí, ou seja, consiste basicamente de um chat P2P. Cada usuário possui um apelido, não podendo
    ultrapassar o tamanho de 255 bytes, asssim como as mensagens enviadas. No datagrama utilizado para comunicação o tamanho do apelido do usuário e o tamanho da mensagem devem
    possuir um byte, já as mensagens trocadas entre os clientes e os apelidos utilizados devem ter no máximo 255 bytes. Utilizamos um identificador (caractere ';') para que seja possível tratar
    todo o datagrama recebido, sendo possível identificar o que é a mensagem, o apelido, o tamanho do apelido ou o tamanho da mensagem.
'''

import threading
import socket
import sys

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

#Recebe como parâmetro o IP, Porta e Id do cliente
def envia(ip, port, idx_cliente):

    #Verifica se o id do cliente é par
    if((int(idx_cliente) % 2) == 0):
        #Caso seja ele escutará a próxima porta
        addr = ((ip, int(port) + 1))
    else:
        #Caso não seja escutará a porta anterior
        addr = ((ip, int(port) - 1))

    while(True):
        #Recebe a mensagem digitada como input
        msg = input('mensagem > ')
        
        #Verifica se a mensagem tem mais de 255 bytes
        if(len(msg) <= 255):
            #Concatena a tamanho do apelido, o apelido, o tamanho da mensagem e a própria mensagem em uma só mensagem
            #Cada parte da mensagem é divida por um identificador, neste caso o identificador é o ';'
            msg = str(len(apelido)) + ';' + apelido + ';' + str(len(msg)) + ';' + msg
            #Faz o encode para utf-8 e envia a mensagem, passando como parâmetro a mensagem e o addr, que contém a porta e o IP
            sock.sendto(msg.encode('utf-8'), addr)
        else:
            #Se a mensagem tiver um tamanho maior do que 255 bytes, uma mensagem de erro é retornada
            print('ERRO: O tamanho máximo da mensagem foi ultrapassado')

#Recebe como parâmetro o IP e a Porta
def recebe(ip, port):
    #Escuta o ip e a porta
    sock.bind((ip, int(port)))

    while(True):
        #Recebe a mensagem (data) e o addr (contém ip e porta)
        data, addr = sock.recvfrom(1024)
        #É realizado um decode, e realizamos um split (divisão) na mensagem onde o caracter ';' está presente
        msg = data.decode('utf-8').split(';')

        #Printamos primeiro o apelido de quem enviou a mensagem, e em seguida a mensagem recebida
        print ('\n', msg[1] + ' escreveu: ' + msg[3])

# Função principal
def main():
    
    idx_cliente = sys.argv[1]
    ip_cliente = sys.argv[2]
    port_cliente = sys.argv[3]
    
    global apelido

    # O tamanho do apelido não pode ultrapassar 255 bytes
    while(True):
        #Recebe o apelido
        apelido = input("Escreva seu apelido >> ")

        #Verifica se o apelido não tem mais do que 255 bytes
        if (len(apelido.encode('utf-8')) <= 255):
            break

        #Se o apelido for maior do que 255 bytes, retornamos uma mensagem de erro
        print('ERRO: O tamanho máximo do apelido foi ultrapassado!')

    try:
        #Declaração das threads utilizadas para enviar e receber mensagens
        enviarThread = threading.Thread(target=envia, args=(ip_cliente, port_cliente, idx_cliente, ))
        receberThread = threading.Thread(target=recebe, args=(ip_cliente, port_cliente, ))

        #Criamos uma thread para enviar e receber as mensagens
        enviarThread.start()
        receberThread.start()
    except:
        #Retornamos uma mensagem de erro caso não seja possível criar a thread
        print("ERRO: Erro ao criar thread!")

main()