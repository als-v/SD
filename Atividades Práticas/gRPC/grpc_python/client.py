'''
    ### RPC ###
    # Autores: Juan e Alisson
    # Data de criação:      09/08/2021
    # Data de modificação:  12/08/2021
    # Este cliente é responsável pela comunicação com o Servidor, sendo que o envio desses dados para comunicação podem ser feitos utilizando gRPC, ou seja,
    utilizando métodos remotos criados na interface ServerDatabaseImpl. O cliente pode realizar as seguintes operações listadas abaixo enviando uma mensagem do
    tipo Request:
      - 1.Adicionar uma nota para um aluno: Passando como parâmetro a operação que deseja realizar, o RA de um aluno específico, o código da disciplina na qual 
      será adicionada a nota do aluno, o semestre da disciplina, o ano da disciplina, o semestre da disciplina e por fim a nota que deseja atribuir ao RA enviado 
      como parâmetro.
      - 2.Remover nota de um aluno: Passando como parâmetro a operação que deseja realizar, o RA de um aluno específico, o código da disciplina da qual 
      a nota do aluno será removida, o ano da disciplina e por fim o semestre da disciplina, resultando então em uma nota vazia na disciplina, em um semestre específico
      , para o aluno enviado como parâmetro.
      - 3.Alterar nota de um aluno: Para alterar a nota de um aluno, os mesmo parâmetros utilizados para operação 1 são enviados para o Servidor.
      - 4.Consultar a nota de um aluno: Envia como parâmetro para o servidor a operação que está sendo realizada (operação 4), o RA do aluno o qual a nota será consultada,
      o código da disciplina, o ano da disciplina e o semestre da disciplina, os quais serão utilizados para consultar a nota do aluno nesta disciplina específica.
      - 5.Consultar notas e faltas de uma disciplina pelo ano: Para que seja possível consultar as notas e faltas de uma disciplina dado um ano, é necessário enviar como parâmetro
      o código da disciplina e o ano o qual se deseja realizar a consulta.
      - 6.Consulta da nota e falta de uma disciplina dado um semestre: Para que seja possível consultar as notas e faltas de uma disciplina dado um semestre, é necessário enviar como parâmetro
      o código da disciplina e o semestre o qual se deseja realizar a consulta.
      - 7.Consulta alunos: Para que seja possível listar todos alunos de uma disciplina é necessário enviar como parâmetro o código da disciplina que gostaria de listar os alunos cursando, 
      o ano da disciplina e o semestre desejado. 
      - 8.Sair: Encerra a execução do cliente

      Como mensagem de resposta do Servidor, o cliente recebe uma mensagem do tipo Response, tendo como campos dessa mensagem o status da operação que foi realizada, sendo 2 caso a operação tenha
      sido realizada com sucesso, e 1 caso tenha acontecido algum problema durante a execução da query. Os outros campos da mensagem de resposta são a mensagem em sí, que varia dependendo da operação realizada
      e do status da operação, e o último campo é uma lista de mensagens do tipo Aluno.
'''

import grpc
import service_pb2
import service_pb2_grpc

#configura o canal de comunicacao
channel = grpc.insecure_channel('localhost:7777')

# Realiza a insersao, atualizacao, remoção e consulta de alunos
def inserirRemoverConsultarNota(alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, alunoNota, flag):
  # Flag é um str que precisa ser convertido para int
  # Utilizado para sinalizar qual tipo de resposta é esperada
  flag = int(flag)

  # Inicializa e configura o stub
  stub = service_pb2_grpc.ServiceDatabaseStub(channel)

  # Chamada remota para o método GerenciaNotas
  response = stub.GerenciaNotas(service_pb2.Request(opt = flag, ra = int(alunoRA), cod_disciplina = str(disciplinaCodigo), ano = int(disciplinaAno), nota = float(alunoNota), semestre = int(disciplinaSemestre)))
  
  # Exibe a mensagem de resposta
  print('\n==============================\n', response.message, '\n==============================')

  # Verificação do tipo de resposta esperada
  if flag == 4  and response.status == 2:
    # Exibe um único aluno
    print('\nAluno:\n------------\n')
    print(response.aluno[0]) 
    print('------------')
  elif flag == 7 and response.status == 2:
    # Exibe uma lista de alunos
    print('\n')
    for data in response.aluno:
      print('------------\n')
      print(data) 
      print('------------')
  print('\n')

# Método para realizar a consulta da disciplina pelo ano ou pelo semestre
def consultarDisciplina(opt, disciplinaCodigo, disciplinaAno, disciplinaSemestre):
  # Inicializa e configura o stub
  stub = service_pb2_grpc.ServiceDatabaseStub(channel)

  # Chamada remota para o método GerenciaNotas
  response = stub.GerenciaNotas(service_pb2.Request(opt = int(opt), cod_disciplina = str(disciplinaCodigo), ano = int(disciplinaAno), semestre = int(disciplinaSemestre)))
  print('\n==============================\n', response.message, '\n==============================')
  
  # Para cada um das notas e faltas
  for data in response.aluno:
    print('------------\n')
    print(data) 
    print('------------')
  print('\n')

def main():

  # Laço infinito
  while True:

    # Listando operações que podem ser realizadas
    print('1 - Adicionar nota a um aluno\n2 - Remover nota de um aluno\n3 - Alterar nota de um aluno\n4 - Consultar nota de um aluno\n5 - Consulta de nota e faltas de uma disciplina pelo ano\n6 - Consulta de nota e faltas de uma disciplina pelo semestre\n7 - Consulta alunos\n8 - sair')
    comando = input('\nSelecione uma operacao: ')

    # Caso o comando seja do tipo 1, 2, 3, 4 ou 7
    if (comando == str(1) or comando == str(2) or comando == str(3) or comando == str(4) or comando == str(7)):
      alunoRA = ''

      # Verifica se é a operação 7, caso contrário não recebe o RA do aluno como parâmetro
      if comando != str(7): 
        alunoRA = input('> Digite o RA do aluno: ')
      else:
        alunoRA = 0

      # Inputs
      disciplinaCodigo = input('> Digite o codigo da disciplina: ')
      disciplinaAno = input('> Digite o ano da disciplina: ')
      disciplinaSemestre = input('> Digite o semestre da disciplina: ')
      
      # Não é necessário a nota para as funcionalidades 2, 4 e 7
      if comando != str(2) and comando != str(4) and comando != str(7):
        alunoNota = input('> Digite a nota do aluno: ')
      else:
        alunoNota = 1

      # Antes de mandar a requisicao, é verificado se os valores estão todos corretos
      if alunoRA != '' and disciplinaCodigo != '' and disciplinaAno != '' and disciplinaSemestre != '' and int(alunoNota) > 0:
        inserirRemoverConsultarNota(alunoRA, disciplinaCodigo, disciplinaAno, disciplinaSemestre, alunoNota, comando)
      else:
        print('\nERRO: preencha os valores corretamente\n')

    # Caso o comando seja do tipo 5 ou 6
    elif comando == str(5) or comando == str(6):
        disciplinaCodigo = input('> Digite o codigo da disciplina: ')
        disciplinaAno = 0
        disciplinaSemestre = 0

        # Pego o ano caso a operação realizada seja a operação 5
        if comando == str(5):
          disciplinaAno = input('> Digite o ano da disciplina: ')
        else:
          # Caso contrário recebo o semestre
          disciplinaSemestre = input('> Digite o semestre da disciplina: ')

        # Antes de mandar a requisicao, verifico se os valores estao todos corretos
        if disciplinaCodigo != '' and (disciplinaAno != 0 or disciplinaSemestre != 0):
          consultarDisciplina(comando, disciplinaCodigo, disciplinaAno, disciplinaSemestre)
        else:
          print('\nERRO: preencha os valores corretamente\n')

    # Caso o comando seja do tipo 8
    elif comando == str(8):
        break

if __name__ == '__main__':
  main()