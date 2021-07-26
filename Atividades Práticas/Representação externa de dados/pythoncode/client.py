import socket
import gerenciamentodenotas_pb2

ip = "127.0.0.1"
port = 7000

addr = (ip, port) 

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
client_socket.connect(addr)

def todo():
    aluno = gerenciamentodenotas_pb2.Aluno()
    aluno.ra = 234
    aluno.nome = "ola"
    aluno.periodo = 3
    aluno.cod_curso = 34

    # marshalling
    msg = aluno.SerializeToString()
    size = len(msg)

    client_socket.send((str(size) + "\n").encode())
    client_socket.send(msg)

    client_socket.send((str(size) + "\n").encode())
    client_socket.send(msg)

    client_socket.close()

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

    client_socket.send((str(size) + "\n").encode())
    client_socket.send(msg)

    # marshalling
    msg = requisicao.SerializeToString()
    size = len(msg)

    client_socket.send((str(size) + "\n").encode())
    client_socket.send(msg)

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