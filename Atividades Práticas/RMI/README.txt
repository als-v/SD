Como executar:

1. Iniciar o servidor de nomes:
    - rmiregistry

2. Executar o servidor:
    - javac -cp ./sqlite-jdbc-3.27.2.1.jar *.java
    - java -cp ".;sqlite-jdbc-3.27.2.1.jar" Servidor

3. Executar o cliente:
    - javac Cliente.java
    - java Client

Bibliotecas:   
    Scanner: Realiza leitura de valores podendo ser números ou strings.
    Sqlite JDBC: Funciona através de drivers que são responsáveis pela conexão com o banco e execução das queries SQL
    Link para o download do sqlite-jar
    	- https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.27.2.1/sqlite-jdbc-3.27.2.1.jar

    JavaRMI: Permitir que um programa rodando em uma dada máquina efetue chamadas à objetos instanciados em outra máquina


Exemplos de uso: 
	Exemplo de uso 1: Consultar nota de um aluno ( operação 4 )

    Em um terminal inicie o servidor de nome:
        - rmiregistry

	Em um terminal, compile e execute o Server:
        - javac -cp ./sqlite-jdbc-3.27.2.1.jar *.java
        - java -cp ".;sqlite-jdbc-3.27.2.1.jar" Servidor
	
	E por fim, inicie e execute o Client.java:
        - javac Cliente.java
        - java Client

	Agora, no console do client irá aparecer um menu, e iremos escolher a opção 4 (Consulta nota de um aluno).
	Em seguida iremos digitar as informações necessárias:
	> Digite o RA do aluno: 2046326
	> Digite o codigo da disciplina: BCC36C
	> Digite o ano da disciplina: 2021
	> Digite o semestre da disciplina: 6
	
	o client recebera uma mensagem contendo o status da operação, listando em seguida os campos do aluno consultado:

Operacao realizada com sucesso!

==== Aluno ====


     RA: 2046229
     Periodo: 6
     Nota: 98,00
     Falta: 12