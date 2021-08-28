Compilar e executar:
	Para iniciar o servidor:
	- python3 server.py

	Para iniciar o cliente:
	- python3 client.py

Bibliotecas
	- threading:  Este módulo constrói interfaces de alto nível para threading usando o módulo _thread, de mais baixo nível.
  	- socket:     Este módulo fornece acesso à interface de soquete BSD. Ele está disponível em todos os sistemas Unix modernos, Windows, MacOS e provavelmente em plataformas adicionais.
	- os:         Este módulo fornece uma maneira simples de usar funcionalidades que são dependentes de sistema operacional.
 	- hashlib:    Este módulo implementa uma interface comum para muitos diferentes algoritmos de hash que são seguros. 
 	- math:       Este módulo fornece acesso às funções matemáticas definidas pelo padrão C.

Exemplos de uso: 
	Exemplo de uso: Fazer upload do arquivo text.txt ao servidor

  	Inicie o servidor em um console
  	- python3 server.py

  	Inicie o cliente em outro console
  	- python3 client.py

  	Agora, no console do cliente digite o nome do arquivo que quer fazer upload, e em seguida pressione Enter
  	- Digite o nome do arquivo que deseja enviar: text.txt

  	O servidor irá receber o arquivo em partes (1024 bytes por vez) e salvar na pasta "server_files/"