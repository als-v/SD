# entrar na pasta
cd javaCode/

# rodar o protobuf first
javac -cp ./protobuf-java-3.17.3.jar *.java

# rodar o server
java -cp ".;protobuf-java-3.17.3.jar .;sqlite-jdbc-3.27.2.1.jar" server

# sqlite jar
https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.27.2.1/sqlite-jdbc-3.27.2.1.jar
