Compilar e executar:
    Entrar na pasta 'javacode'
    - cd javacode/

    Rodar o protobuf first
    - javac -cp ./protobuf-java-3.17.3.jar *.java

    Rodar o server
    - java -cp ".;protobuf-java-3.17.3.jar .;sqlite-jdbc-3.27.2.1.jar" server

    Link para o download do sqlite-jar
    - https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.27.2.1/sqlite-jdbc-3.27.2.1.jar

Bibliotecas:   
    - Sqlite JDBC: Funciona através de drivers que são responsáveis pela conexão com o banco e execução das queries SQL

Exemplo de uso:
    - Inserir uma nova nota para o aluno: 