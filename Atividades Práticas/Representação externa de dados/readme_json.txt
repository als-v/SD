Compilar e executar:
    Entrar na pasta 'javacode'
    - cd javacode/

    Rodar o gson primeiro
    - javac -cp ./gson-2.8.6.jar *.java

    Rodar o server
    - java -cp ".;sqlite-jdbc-3.27.2.1.jar .;gson-2.8.6.jar" server

    Link para o download do gson-2.8.6.jar
    - https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar

Bibliotecas:
    - Gson: Este pacote fornece a classe Gson que possibilita converter Json em String e vice-versa.
    - protobuf-java-3.17.3.jar: É um método de serialização de dados estruturados

Exemplo de uso:
    - Inserir uma nova nota para o aluno: 