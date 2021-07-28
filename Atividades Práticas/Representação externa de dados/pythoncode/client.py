import socket
import gerenciamentodenotas_pb2

ip = "127.0.0.1"
port = 7000

addr = (ip, port) 

clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
clientSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
clientSocket.connect(addr)

def todo():
    aluno = gerenciamentodenotas_pb2.Aluno()
    aluno.ra = 234
    aluno.nome = "ola"
    aluno.periodo = 3
    aluno.cod_curso = 34

    # marshalling
    msg = aluno.SerializeToString()
    size = len(msg)

    clientSocket.send((str(size) + "\n").encode())
    clientSocket.send(msg)

    clientSocket.send((str(size) + "\n").encode())
    clientSocket.send(msg)

    clientSocket.close()

def inserirNota():
    alunoRA = input('\nDigite o RA do aluno: ')
    disciplinaCodigo = input('Digite o codigo da disciplina: ')
    disciplinaAno = input('Digite o ano da disciplina: ')
    disciplinaSemestre = input('Digite o semestre da disciplina: ')
    alunoNota = input('Digite a nota do aluno: ')

    requisicaoOpt = gerenciamentodenotas_pb2.requisicaoOpt()
    requisicaoOpt.opt = 1

    requisicao = gerenciamentodenotas_pb2.requisicaoNotas()
    requisicao.ra = int(alunoRA)
    requisicao.cod_disciplina = disciplinaCodigo
    requisicao.ano = int(disciplinaAno)
    requisicao.semestre = int(disciplinaSemestre)
    requisicao.nota = float(alunoNota)

    # marshalling
    msg = requisicaoOpt.SerializeToString()
    size = len(msg)

    clientSocket.send((str(size) + "\n").encode())
    clientSocket.send(msg)

    # marshalling
    msg = requisicao.SerializeToString()
    size = len(msg)

    clientSocket.send((str(size) + "\n").encode())
    clientSocket.send(msg)

    tamanhoResponse = clientSocket.recv(512).decode()
    print("tamanhoResponse: ", tamanhoResponse)

    response = clientSocket.recv(int(tamanhoResponse)).decode()
    
    if response != "OK":
        print('Erro ao realizar acao:\n', response)

def consultaAluno():
    pass

def init():
    print('Bem vindo\nO servico apresenta as seguintes funcionalidades:\n1 - Inserir nota a um aluno\n2 - Consultar alunos de uma disciplina\n3 - Help\n4 - Sair')

def main():
    init()
    while True:
        comando = input('\nSelecione uma operacao: ')
        
        if comando == str(1):
            inserirNota()
        elif comando == str(2):
            consultaAluno()
        elif comando == str(3):
            init()
        elif comando == str(4):
            break
main()