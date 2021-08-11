import grpc
import service_pb2
import service_pb2_grpc

#configura o canal de comunicacao
channel = grpc.insecure_channel('localhost:7777')

# funcao que realiza a insersao, atualizacao, remocao e consulta de alunos
def inserirRemoverConsultarNota(alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, alunoNota, flag):
  # flag é um str que precisa ser convertido para int
  flag = int(flag)

  # inicializa e configura o stub
  stub = service_pb2_grpc.ServiceDatabaseStub(channel)

  # chamada remota
  response = stub.GerenciaNotas(service_pb2.Request(opt = flag, ra = int(alunoRA), cod_disciplina = str(disciplinaCodigo), ano = int(disciplinaAno), nota = float(alunoNota), semestre = int(disciplinaSemestre)))
  
  # mostra a resposta
  print('\n==============================\n', response.message, '\n==============================')

  # verifico qual tipo de resposta espero
  if flag == 4  and response.status == 2:
    # mostro um aluno so
    print('\nAluno:\n------------\n')
    print(response.aluno[0]) 
    print('------------')
  elif flag == 7 and response.status == 2:
    # mostro a listagem de alunos
    print('\n')
    for data in response.aluno:
      print('------------\n')
      print(data) 
      print('------------')
  print('\n')

# funcao para realizar a consulta da disciplina pelo ano ou pelo semestre
def consultarDisciplina(opt, disciplinaCodigo, disciplinaAno, disciplinaSemestre):
  #inicializa e configura o stub
  stub = service_pb2_grpc.ServiceDatabaseStub(channel)

  #chamada remota
  response = stub.GerenciaNotas(service_pb2.Request(opt = int(opt), cod_disciplina = str(disciplinaCodigo), ano = int(disciplinaAno), semestre = int(disciplinaSemestre)))
  print('\n==============================\n', response.message, '\n==============================')
  
  # para cada um das notas e faltas
  for data in response.aluno:
    print('------------\n')
    print(data) 
    print('------------')
  print('\n')

def main():

  # laco infinito
  while True:

    # opcoes
    print('1 - Adicionar nota a um aluno\n2 - Remover nota de um aluno\n3 - Alterar nota de um aluno\n4 - Consultar nota de um aluno\n5 - Consulta de nota e faltas de uma disciplina pelo ano\n6 - Consulta de nota e faltas de uma disciplina pelo semestre\n7 - Consulta alunos\n8 - sair')
    comando = input('\nSelecione uma operacao: ')

    # caso o comando seja do tipo 1, 2, 3, 4 ou 7
    if (comando == str(1) or comando == str(2) or comando == str(3) or comando == str(4) or comando == str(7)):
      alunoRA = ''

      # nao preciso do ra para a funcionalidade 7
      if comando != str(7): 
        alunoRA = input('> Digite o RA do aluno: ')
      else:
        # para 'burlar' a verificacao
        alunoRA = 0

      disciplinaCodigo = input('> Digite o codigo da disciplina: ')
      disciplinaAno = input('> Digite o ano da disciplina: ')
      disciplinaSemestre = input('> Digite o semestre da disciplina: ')
      
      # nao preciso da nota para a funcionalidade 2, 4 e 7
      if comando != str(2) and comando != str(4) and comando != str(7):
        alunoNota = input('> Digite a nota do aluno: ')
      else:
        # para 'burlar' a verificacao
        alunoNota = 1

      # antes de mandar a requisicao, verifico se os valores estao todos corretos
      if alunoRA != '' and disciplinaCodigo != '' and disciplinaAno != '' and disciplinaSemestre != '' and int(alunoNota) > 0:
        inserirRemoverConsultarNota(alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, alunoNota, comando)
      else:
        print('\nERRO: preencha os valores corretamente\n')

    # caso o comando seja do tipo 5 ou 6
    elif comando == str(5) or comando == str(6):
        disciplinaCodigo = input('> Digite o codigo da disciplina: ')
        disciplinaAno = ''
        disciplinaSemestre = ''

        # ou pego o ano ou o semestre
        if comando == str(5):
          disciplinaAno = input('> Digite o ano da disciplina: ')
        else:
          disciplinaSemestre = input('> Digite o semestre da disciplina: ')

        # antes de mandar a requisicao, verifico se os valores estao todos corretos
        if disciplinaCodigo != '' and (disciplinaAno != '' or disciplinaSemestre != ''):
          consultarDisciplina(comando, disciplinaCodigo, disciplinaAno, disciplinaSemestre)
        else:
          print('\nERRO: preencha os valores corretamente\n')

    # caso o comando seja do tipo 8
    elif comando == str(8):
        break

if __name__ == '__main__':
  main()