import threading
import socket
import sys

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

def envia(ip, port, idx_cliente):

    if((int(idx_cliente) % 2) == 0):
        addr = ((ip, int(port) + 1))
    else:
        addr = ((ip, int(port) - 1))

    while(True):
        msg = input('mensagem > ')
        
        if(len(msg) <= 255):
            msg = str(len(apelido)) + ';' + apelido + ';' + str(len(msg)) + ';' + msg
            sock.sendto(msg.encode('utf-8'), addr)
        else:
            print('ERRO: O tamanho máximo da mensagem foi ultrapassado')

def recebe(ip, port):
    sock.bind((ip, int(port)))

    while(True):
        data, addr = sock.recvfrom(1024)
        msg = data.decode('utf-8').split(';')

        print ('\n', msg[1] + ' escreveu: ' + msg[3])

# Função principal
def main():
    idx_cliente = sys.argv[1]
    ip_cliente = sys.argv[2]
    port_cliente = sys.argv[3]
    
    global apelido

    # O tamanho do apelido não pode ultrapassar 255 bytes
    while(True):
        apelido = input("Escreva seu apelido >> ")
        if (len(apelido.encode('utf-8')) <= 255):
            break

        print('ERRO: O tamanho máximo do apelido foi ultrapassado!')

    try:
        enviarThread = threading.Thread(target=envia, args=(ip_cliente, port_cliente, idx_cliente, ))
        receberThread = threading.Thread(target=recebe, args=(ip_cliente, port_cliente, ))

        enviarThread.start()
        receberThread.start()
    except:
        print("ERRO: Erro ao criar thread!")

main()