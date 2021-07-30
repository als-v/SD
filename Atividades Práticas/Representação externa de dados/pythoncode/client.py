import socket
import gerenciamentodenotas_pb2

ip = "127.0.0.1"
port = 7000

addr = (ip, port) 

clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
clientSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
clientSocket.connect(addr)

labelProtocolBuffer = '\n4 - Alterar a comunicacao para: Protocol Buffer'
labelJson = '\n4- Alterar a comunicacao para JSON'

def inserirRemoverNota(alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, alunoNota, flag = 1):
    # crio a primeira mensagem
    requisicaoOpt = gerenciamentodenotas_pb2.requisicaoOpt()
    requisicaoOpt.opt = flag

    # crio a segunda mensagem
    requisicao = gerenciamentodenotas_pb2.requisicaoNotas()
    requisicao.ra = int(alunoRA)
    requisicao.cod_disciplina = disciplinaCodigo
    requisicao.ano = int(disciplinaAno)
    requisicao.semestre = int(disciplinaSemestre)
    requisicao.nota = float(alunoNota)

    # marshalling
    msg = requisicaoOpt.SerializeToString()
    size = len(msg)

    # envio a primeira mensagem
    clientSocket.send((str(size) + "\n").encode())
    clientSocket.send(msg)

    # marshalling
    msg = requisicao.SerializeToString()
    size = len(msg)

    # envio a segunda mensagem
    clientSocket.send((str(size) + "\n").encode())
    clientSocket.send(msg)

    # recebo a resposta do servidor
    tamanhoResponse = clientSocket.recv(512).decode()
    response = clientSocket.recv(int(tamanhoResponse)).decode()

    # mostro na tela
    print('\n=========================\n', response, '\n=========================\n')

def consultaAluno(disciplinaCodigo, disciplinaAno, disciplinaSemestre):
    # crio a primeira mensagem
    requisicaoOpt = gerenciamentodenotas_pb2.requisicaoOpt()
    requisicaoOpt.opt = 3

    # crio a segunda mensagem
    requisicao = gerenciamentodenotas_pb2.requisicaoConsultaAlunos()
    requisicao.cod_disciplina = disciplinaCodigo
    requisicao.ano = int(disciplinaAno)
    requisicao.semestre = int(disciplinaSemestre)

    # marshalling
    msg = requisicaoOpt.SerializeToString()
    size = len(msg)

    # envio a primeira mensagem
    clientSocket.send((str(size) + "\n").encode())
    clientSocket.send(msg)

    # marshalling
    msg = requisicao.SerializeToString()
    size = len(msg)

    # envio a segunda mensagem
    clientSocket.send((str(size) + "\n").encode())
    clientSocket.send(msg)

    # recebo a resposta
    tamanhoResponse = int(clientSocket.recv(1024).decode())
    response = clientSocket.recv(tamanhoResponse).decode()
    
    # mostro na tela
    print('\n=========================\n', response, '\n=========================\n')

def main():
    # protocolBuffer (0) ou json (1)
    bufferJsonFlag = 0

    while True:
        if bufferJsonFlag == 0:
            print('1 - Adicionar nota a um aluno\n2 - Remover nota de um aluno\n3 - Consultar alunos de uma disciplina' + labelJson + '\n5 - Sair')
        else:
            print('1 - Adicionar nota a um aluno\n2 - Remover nota de um aluno\n3 - Consultar alunos de uma disciplina' + labelProtocolBuffer + '\n5 - Sair')

        comando = input('\nSelecione uma operacao: ')
        
        if (comando == str(1) or comando == str(2)):
            alunoRA = input('> Digite o RA do aluno: ')
            disciplinaCodigo = input('> Digite o codigo da disciplina: ')
            disciplinaAno = input('> Digite o ano da disciplina: ')
            disciplinaSemestre = input('> Digite o semestre da disciplina: ')

            if alunoRA != '' and disciplinaCodigo != '' and disciplinaAno != '' and disciplinaSemestre != '':
                if comando == str(1):
                    alunoNota = input('> Digite a nota do aluno: ')
                    
                    if int(alunoNota) > 0:
                        inserirRemoverNota(alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, alunoNota, 1)
                    else:
                        print('ERRO: Nota negativa...')
                else:
                    inserirRemoverNota(alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, 0, 2)
            else:
                print('\nERRO: preencha os valores corretamente\n')

        elif comando == str(3):
            disciplinaCodigo = input('> Digite o codigo da disciplina: ')
            disciplinaAno = input('> Digite o ano da disciplina: ')
            disciplinaSemestre = input('> Digite o semestre da disciplina: ')
            
            if disciplinaCodigo != '' and disciplinaAno != '' and disciplinaSemestre != '':
                consultaAluno(disciplinaCodigo, disciplinaAno, disciplinaSemestre)
            else:
                print('\nERRO: preencha os valores corretamente\n')
    
        elif comando == str(4):
            if bufferJsonFlag == 0:
                bufferJsonFlag = 1
            else:
                bufferJsonFlag = 0
        
        elif comando == str(5):
            break

main()