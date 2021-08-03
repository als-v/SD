Compilar e executar:
	Primeiramente é necessário rodar os dois servidores antes de rodar o client, com os comandos:
		Para iniciar o servidor que atende a comunicação por JSON:
		- cd javacode_json
		- javac -cp ./gson-2.8.6.jar *.java
		- java -cp ".;sqlite-jdbc-3.27.2.1.jar .;gson-2.8.6.jar" server

		Para iniciar o servidor que atende a comunicação por Protobuf:
		- cd javacode_protobuf/
 		- javac -cp ./protobuf-java-3.17.3.jar *.java
    		- java -cp ".;protobuf-java-3.17.3.jar .;sqlite-jdbc-3.27.2.1.jar" server
	E para rodar o client:
	- cd pythoncode/
	- pip install jsonpickle
	- python client.py

Bibliotecas Server:   
    - Sqlite JDBC: Funciona através de drivers que são responsáveis pela conexão com o banco e execução das queries SQL
    Link para o download do sqlite-jar
    	- https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.27.2.1/sqlite-jdbc-3.27.2.1.jar
    
    - Gson: Este pacote fornece a classe Gson que possibilita converter Json em String e vice-versa.
    Link para o download do gson-2.8.6.jar
    	- https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar

Bibliotecas Client:   
    - socket: Possilita a criação e a conexão a um socket
    - jsonpickle: É uma biblioteca para a conversão bidirecional de objetos Python complexos e JSON. 
    - json: É um formato de troca de dados leve inspirado na sintaxe literal do objeto JavaScript.

Compilar e executar:
Entrar na pasta 'javacode_protobuf'
    - cd javacode_protobuf/

    Rodar o protobuf first
    - javac -cp ./protobuf-java-3.17.3.jar *.java

    Rodar o server
    - java -cp ".;protobuf-java-3.17.3.jar .;sqlite-jdbc-3.27.2.1.jar" server



Exemplos de uso: 
	Exemplo de uso 1: Cadastrar uma nota a um aluno no banco de dados usando a comunicação JSON

	Em um terminal, inicie o server.java localizado na pasta javacode_protobuf
	- cd javacode_protobuf
	- javac -cp ./protobuf-java-3.17.3.jar *.java
        - java -cp ".;protobuf-java-3.17.3.jar .;sqlite-jdbc-3.27.2.1.jar" server

	Agora, em um outro terminal, inicie o server.java localizado na pasta javacode_json
	- cd javacode_json
	- javac -cp ./gson-2.8.6.jar *.java
	- java -cp ".;sqlite-jdbc-3.27.2.1.jar .;gson-2.8.6.jar" server
	
	E por fim, inicie o client.py localizado na pasta pythoncode
	- cd pythoncode
	- python3 client.py

	Agora, no console do client irá aparecer um menu, e iremos escolher a opção 1.
	Em seguida iremos digitar as informações necessárias:
	> Digite o RA do aluno: 2046326
	> Digite o codigo da disciplina: BCC36C
	> Digite o ano da disciplina: 2021
	> Digite o semestre da disciplina: 6
	> Digite a nota do aluno: 10
	
	E o client recebera uma mensagem contendo o status da operação.

	Exemplo de uso 2: Cadastrar uma nota a um aluno no banco de dados usando a comunicação Protobuf

	Em um terminal, inicie o server.java localizado na pasta javacode_protobuf
	- cd javacode_protobuf
	- javac -cp ./protobuf-java-3.17.3.jar *.java
        - java -cp ".;protobuf-java-3.17.3.jar .;sqlite-jdbc-3.27.2.1.jar" server

	Agora, em um outro terminal, inicie o server.java localizado na pasta javacode_json
	- cd javacode_json
	- javac -cp ./gson-2.8.6.jar *.java
	- java -cp ".;sqlite-jdbc-3.27.2.1.jar .;gson-2.8.6.jar" server
	
	E por fim, inicie o client.py localizado na pasta pythoncode
	- cd pythoncode
	- python3 client.py
	
	Agora, no console do client irá aparecer um menu, e iremos mudar a comunicação que por padrão é JSON, selecionando a opção 4.
	E para realizar o cadastro de uma nota, iremos escolher a opção 1.
	Em seguida iremos digitar as informações necessárias:
	> Digite o RA do aluno: 2046326
	> Digite o codigo da disciplina: BCC36C
	> Digite o ano da disciplina: 2021
	> Digite o semestre da disciplina: 6
	> Digite a nota do aluno: 10
	
	E o client recebera uma mensagem contendo o status da operação.


