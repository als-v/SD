Compilar e executar:
	Para iniciar o chat:
	- python3 chat.py [indice] [ip] [porta]

Bibliotecas
	- threading:  Este módulo constrói interfaces de alto nível para threading usando o módulo _thread, de mais baixo nível.
  	- socket:     Este módulo fornece acesso à interface de soquete BSD. Ele está disponível em todos os sistemas Unix modernos, Windows, MacOS e provavelmente em plataformas adicionais.
	- sys:        Este módulo fornece acesso a algumas variáveis ​​usadas ou mantidas pelo interpretador e a funções que interagem fortemente com o interpretador.

Exemplos de uso: 
	Exemplo de uso: conversar no chat

  	Inicie o chat em um console
  	- python3 chat.py 0 127.0.0.1 5001

    Agora, digite um apelido no console
    - Digite seu apelido >> teste1

  	Inicie o cliente em outro console
  	- python3 chat.py 1 127.0.0.1 5002

    Agora, digite um apelido no console
    - Digite seu apelido >> teste2

  	Agora, basta enviar uma mensagem e ela irá ser recebida no outro console e vice versa.
    
    Por exemplo, no primeiro console, ao enviar a seguinte mensagem:
    mensagem > teste

    Irá ser recebida e mostrada no segundo console:
    teste1 escreveu: teste