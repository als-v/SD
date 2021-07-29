# entrar na pasta
cd javaCode/

# rodar o protobuf first
javac -cp ./protobuf-java-3.17.3.jar *.java

# rodar o server
java -cp ".;protobuf-java-3.17.3.jar .;sqlite-jdbc-3.27.2.1.jar" server

# sqlite jar
https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.27.2.1/sqlite-jdbc-3.27.2.1.jar










 // tem disciplina?
            resultadoQuery = statement.executeQuery("SELECT * FROM disciplina WHERE (codigo = '" + String.valueOf(codigoDisciplina) + "')");
            if(!resultadoQuery.isBeforeFirst()){
                res.setMessage("Disciplina inexistente");
                return 1;
            }

            // matricula
            resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (ra_aluno = " + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "+ String.valueOf(ano) +" AND semestre = "+ String.valueOf(semestre) +");");
            if(!resultadoQuery.isBeforeFirst()){
                res.setMessage("Matricula do aluno em " + String.valueOf(ano) + "/" + String.valueOf(semestre) + " inexistente");
                return 1;
            }

            // atualiza a nota
            statement.execute("UPDATE matricula SET nota = " + String.valueOf(nota) + " WHERE (ra_aluno = " + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "+ String.valueOf(ano) +" AND semestre = "+ String.valueOf(semestre) +");");
            res.setMessage("OK");