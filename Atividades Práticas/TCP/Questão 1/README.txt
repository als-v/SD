Compilar e executar:
	Para iniciar o servidor:
	- python3 server.py
	Para iniciar o cliente:
	- python3 client.py

Bibliotecas
	- threading:  Este módulo constrói interfaces de alto nível para threading usando o módulo _thread, de mais baixo nível.
  	- socket:     Este módulo fornece acesso à interface de soquete BSD. Ele está disponível em todos os sistemas Unix modernos, Windows, MacOS e provavelmente em plataformas adicionais.
	- datetime:   O módulo datetime fornece as classes para manipulação de datas e horas.    
	- os:         Este módulo fornece uma maneira simples de usar funcionalidades que são dependentes de sistema operacional.
 	- time:       Este módulo fornece várias funções relacionadas ao tempo. 

Exemplos de uso: 
	Exemplo de uso: Baixar o arquivo img1.png do servidor

  	Inicie o servidor em um console
  	- python3 server.py

  	Inicie o cliente em outro console
  	- python3 client.py

  	Agora, no console do cliente digite DOWN e o nome do arquivo que quer baixar, e em seguida pressione Enter
  	- DOWN img1.png

  	O cliente irá receber o arquivo em partes e salvar na pasta "client_files/"