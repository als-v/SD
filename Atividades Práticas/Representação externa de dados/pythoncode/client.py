'''
    ### Programação com Representação Externa de Dados ###
    # Autores: Juan e Alisson
    # Data de criação:      26/07/2021
    # Data de modificação:  02/08/2021
    # Este cliente é responsável pela comunicação com o Servidor, sendo que o envio desses dados para comunicação podem ser feitos utilizando Protocol Buffer ou JSON, sendo 
    possível alternar entre os dois tipos de comunicação. O cliente pode realizar inserção e remoção de um certo aluno em uma matrícula específica. Também é possível listar
    os alunos de uma disciplina em um ano/semestre. 
'''

import socket
import gerenciamentodenotas_pb2
import json
import jsonpickle

# ip e porta para o servidor que aceita requisicoes protobuf
ipProtobuf = "127.0.0.1"
portProtobuf = 7000
addrProtobuf = (ipProtobuf, portProtobuf)


# ip e porta para o servidor que aceita requisicoes json
ipJson = "127.0.0.1"
portJson = 7001
addrJson = (ipJson, portJson)

# se conecta ao servidor protobuf
clientSocketProtobuf = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
clientSocketProtobuf.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
clientSocketProtobuf.connect(addrProtobuf)

# se conecta ao servidor json
clientSocketJson = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
clientSocketJson.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
clientSocketJson.connect(addrJson)


def enviarComunicacao():
    size = len(bufferJsonFlag)

    # envio que a comunicação será pelo protocolbuffer
    if bufferJsonFlag == "0":
        clientSocketProtobuf.send((str(size) + "\n").encode())
        clientSocketProtobuf.send(bufferJsonFlag.encode())
    elif bufferJsonFlag == "1":
        clientSocketJson.send((str(size) + "\n").encode())
        clientSocketJson.send(bufferJsonFlag.encode())

# enviar a primeira mensagem Protobuf
def enviarOptProtobuf(flag):
    # crio a primeira mensagem
    requisicaoOpt = gerenciamentodenotas_pb2.requisicaoOpt()
    requisicaoOpt.opt = flag

    # marshalling
    msg = requisicaoOpt.SerializeToString()
    size = len(msg)

    # envio a mensagem
    clientSocketProtobuf.send((str(size) + "\n").encode())
    clientSocketProtobuf.send(msg)

# enviar a primeira mensagem Json
def enviarOptJson(flag):
    jsonRequisicao = {
        "requisicao_opt": flag,
    }

    # marshalling
    msg = json.dumps(jsonRequisicao)
    size = len(msg)

    # envia a mensagem
    clientSocketJson.send((str(size) + "\n").encode())
    clientSocketJson.send(msg.encode('utf-8'))



def inserirRemoverNota(alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, alunoNota, flag=1):
    enviarComunicacao()

    if bufferJsonFlag == "0":
        # preparo e envio a primeira mensagem
        enviarOptProtobuf(flag)

        # crio a segunda mensagem
        requisicao = gerenciamentodenotas_pb2.requisicaoNotas()
        requisicao.ra = int(alunoRA)
        requisicao.cod_disciplina = disciplinaCodigo
        requisicao.ano = int(disciplinaAno)
        requisicao.semestre = int(disciplinaSemestre)
        requisicao.nota = float(alunoNota)

        # marshalling
        msg = requisicao.SerializeToString()
        size = len(msg)

        # envio a segunda mensagem
        clientSocketProtobuf.send((str(size) + "\n").encode())
        clientSocketProtobuf.send(msg)

        # recebo a resposta do servidor
        tamanhoResponse = clientSocketProtobuf.recv(512).decode()
        response = clientSocketProtobuf.recv(int(tamanhoResponse)).decode()

        # mostro na tela
        print('\n=========================\n',
              response, '\n=========================\n')

    elif bufferJsonFlag == "1":
        enviarOptJson(flag)

        jsonNota = {
            "aluno_ra": alunoRA,
            "disciplina_codigo": disciplinaCodigo,
            "disciplina_ano": disciplinaAno,
            "disciplina_semestre": disciplinaSemestre,
            "aluno_nota": alunoNota
        }

        # marshalling
        msg = json.dumps(jsonNota)
        size = len(msg)

        # envio a segunda mensagem
        clientSocketJson.send((str(size) + "\n").encode())
        clientSocketJson.send(msg.encode('utf-8'))

        # recebo a resposta do servidor
        tamanhoResponse = clientSocketJson.recv(1024).decode()
        response = jsonpickle.decode(clientSocketJson.recv(
            int(tamanhoResponse)).decode('utf-8'))

        # mostro na tela
        print('\n=========================\n',
              response['message'], '\n=========================\n')


def consultaAluno(disciplinaCodigo, disciplinaAno, disciplinaSemestre):
    enviarComunicacao()

    if bufferJsonFlag == "0":
        enviarOptProtobuf(3)

        # crio a segunda mensagem
        requisicao = gerenciamentodenotas_pb2.requisicaoConsultaAlunos()
        requisicao.cod_disciplina = disciplinaCodigo
        requisicao.ano = int(disciplinaAno)
        requisicao.semestre = int(disciplinaSemestre)

        # marshalling
        msg = requisicao.SerializeToString()
        size = len(msg)

        # envio a segunda mensagem
        clientSocketProtobuf.send((str(size) + "\n").encode())
        clientSocketProtobuf.send(msg)

        # recebo a resposta
        tamanhoResponse = int(clientSocketProtobuf.recv(1024).decode())
        response = clientSocketProtobuf.recv(tamanhoResponse).decode()

        # mostro na tela
        print('\n=========================\n',
              response, '\n=========================\n')

    elif bufferJsonFlag == "1":
        enviarOptJson(3)

        jsonListAlunos = {
            "disciplina_codigo": disciplinaCodigo,
            "disciplina_ano": disciplinaAno,
            "disciplina_semestre": disciplinaSemestre,
        }

        # marshalling
        msg = json.dumps(jsonListAlunos)
        size = len(msg)

        # envio a segunda mensagem
        clientSocketJson.send((str(size) + "\n").encode())
        clientSocketJson.send(msg.encode('utf-8'))

        # recebo a resposta
        tamanhoResponse = int(clientSocketJson.recv(1024).decode())
        response = jsonpickle.decode(clientSocketJson.recv(
            int(tamanhoResponse)).decode('utf-8'))

        # mostro na tela
        print('\n=========================\n',
              response['message'], '\n=========================\n')
        for index, aluno in enumerate(response['alunos']):
            print('Aluno ', str(index+1)+':')
            print('--   Ra: ', aluno['ra'])
            print('--   Nome: ', aluno['nome'])
            print('--   Periodo: ', aluno['periodo'])
        print('\n')

def main():
    # protocolBuffer (0) ou json (1)
    global bufferJsonFlag
    bufferJsonFlag = "1"

    # labels para mostrar ao menu
    labelProtocolBuffer = '\n4 - Alterar a comunicacao para: Protocol Buffer'
    labelJson = '\n4 - Alterar a comunicacao para JSON'

    # laco infinito
    while True:
        
        # mostra a selecao atual (Json ou procolbuffer)
        if bufferJsonFlag == "0":
            print('1 - Adicionar nota a um aluno\n2 - Remover nota de um aluno\n3 - Consultar alunos de uma disciplina' +
                  labelJson + '\n5 - Sair')
        else:
            print('1 - Adicionar nota a um aluno\n2 - Remover nota de um aluno\n3 - Consultar alunos de uma disciplina' +
                  labelProtocolBuffer + '\n5 - Sair')

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
                        inserirRemoverNota(
                            alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, alunoNota, 1)
            
                else:
                    inserirRemoverNota(
                        alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, -1, 2)
            else:
                print('\nERRO: preencha os valores corretamente\n')

        elif comando == str(3):
            disciplinaCodigo = input('> Digite o codigo da disciplina: ')
            disciplinaAno = input('> Digite o ano da disciplina: ')
            disciplinaSemestre = input('> Digite o semestre da disciplina: ')

            if disciplinaCodigo != '' and disciplinaAno != '' and disciplinaSemestre != '':
                consultaAluno(disciplinaCodigo, disciplinaAno,
                              disciplinaSemestre)
            else:
                print('\nERRO: preencha os valores corretamente\n')

        elif comando == str(4):
            if bufferJsonFlag == "0":
                bufferJsonFlag = "1"
            else:
                bufferJsonFlag = "0"

        elif comando == str(5):
            break

main()
