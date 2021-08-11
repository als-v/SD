import grpc
import service_pb2
import service_pb2_grpc

#configura o canal de comunicacao
channel = grpc.insecure_channel('localhost:7777')

def inserirRemoverConsultarNota(alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, alunoNota, flag):
  #inicializa e configura o stub
  stub = service_pb2_grpc.ServiceDatabaseStub(channel)

  #chamada remota
  response = stub.GerenciaNotas(service_pb2.Request(opt = int(flag), ra = int(alunoRA), cod_disciplina = str(disciplinaCodigo), ano = int(disciplinaAno), nota = float(alunoNota), semestre = int(disciplinaSemestre)))
  print("\nRecebido: " + response.message + '\n')

  if flag == 4 and response.status == 2:
    print(response.aluno[0], '\n')

def consultarDisciplina(opt, disciplinaCodigo, disciplinaAno, disciplinaSemestre):
  #inicializa e configura o stub
  stub = service_pb2_grpc.ServiceDatabaseStub(channel)

  #chamada remota
  response = stub.GerenciaNotas(service_pb2.Request(opt = int(opt), cod_disciplina = str(disciplinaCodigo), ano = int(disciplinaAno), semestre = int(disciplinaSemestre)))
  print("\nRecebido: " + response.message + '\n')
  
  for aluno in response.aluno:
    print(aluno, '\n')

def main():

  # laco infinito
  while True:

    print('1 - Adicionar nota a um aluno\n2 - Remover nota de um aluno\n3 - Alterar nota de um aluno\n4 - Consultar nota de um aluno\n5 - Consulta de nota e faltas de uma disciplina pelo ano\n6 - Consulta de nota e faltas de uma disciplina pelo semestre\n7 - Consulta alunos\n8 - sair')

    comando = input('\nSelecione uma operacao: ')

    if (comando == str(1) or comando == str(2) or comando == str(3) or comando == str(4)):
      alunoRA = input('> Digite o RA do aluno: ')
      disciplinaCodigo = input('> Digite o codigo da disciplina: ')
      disciplinaAno = input('> Digite o ano da disciplina: ')
      disciplinaSemestre = input('> Digite o semestre da disciplina: ')
      alunoNota = -1
      
      if comando != str(2) and comando != str(4):
        alunoNota = input('> Digite a nota do aluno: ')
      
      if (alunoRA != '' and disciplinaCodigo != '' and disciplinaAno != '' and disciplinaSemestre != '') or (alunoNota < 0):
        inserirRemoverConsultarNota(alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, alunoNota, int(comando))
      else:
        print('\nERRO: preencha os valores corretamente\n')
    elif comando == str(5) or comando == str(6):
        disciplinaCodigo = input('> Digite o codigo da disciplina: ')
        disciplinaAno = 0
        disciplinaSemestre = 0

        if comando == str(5):
          disciplinaAno = input('> Digite o ano da disciplina: ')
        else:
          disciplinaSemestre = input('> Digite o semestre da disciplina: ')

        consultarDisciplina(int(comando), disciplinaCodigo, disciplinaAno, disciplinaSemestre)
    elif comando == str(7):
      pass
    elif comando == str(8):
        break


if __name__ == '__main__':
  main()
