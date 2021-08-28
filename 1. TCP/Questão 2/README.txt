Compilar e executar:
	Para iniciar o servidor:
	- python3 server.py
	Para iniciar o cliente:
	- python3 client.py

Bibliotecas
  - socket:     Este módulo fornece acesso à interface de soquete BSD. Ele está disponível em todos os sistemas Unix modernos, Windows, MacOS e provavelmente em plataformas adicionais.
  - os:         Este módulo fornece uma maneira simples de usar funcionalidades que são dependentes de sistema operacional.
  - threading:  Este módulo constrói interfaces de alto nível para threading usando o módulo _thread, de mais baixo nível.

Exemplos de uso: 
	Baixar o arquivo text.txt do servidor
  		Inicie o servidor em um console
  		- python3 server.py
  		Inicie o cliente em outro console
  		- python3 client.py

  		Agora, no console do cliente você digita GETFILE juntamente com o nome do arquivo que quer baixar, e em seguida pressione a tecla Enter
  		- GETFILE text.txt

  		O cliente irá receber o arquivo byte a byte e salvar na pasta "client_files/"

	Adicionar o arquivo textando.txt no servidor
  		Inicie o servidor em um console
  		- python3 server.py
  		Inicie o cliente em outro console
  		- python3 client.py

 		 Agora, no console do cliente você digita ADDFILE juntamente com o nome do arquivo que quer adicionar, e em seguida pressione a tecla Enter
  		- ADDFILE textando.txt

  		O servidor irá receber o arquivo em partes e salvar na pasta "server_files/"