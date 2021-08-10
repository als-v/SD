/*
 
 ### Programação com Representação Externa de Dados ###
 # Autores: Juan e Alisson
 # Data de criação:      26/07/2021
 # Data de modificação:  02/08/2021
 # Este serviço promove a comunicação utilizando Protocol Buffer, recebendo como parâmetro um mensagem do client.py através de um socket TCP, realizando
 a requisição no banco e retornando por fim uma mensagem para o ciente, notificando se a operação foi bem sucedida ou não. 
 Este serviço oferece as seguintes funcionalidades remotas:
 - Inserção e remoção de nota
 - Consulta de alunos em uma disciplina específica, dado um ano ou semestre
 Utilizamos o SQLite para que fosse possível gerenciar o banco de dados (gerenciamento_notas.db) 

*/


import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Map;

import java.sql.*;

public class server {

    /**
     * Método para se conectar ao banco
     * @return
     */
    public static Connection connect() {
        Connection conn = null;

        try {
            // db parameters
            String url = "jdbc:sqlite:../database/gerenciamento_notas.db";

            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            // caso não consiga se conectar
            System.out.println(e.getMessage());
        }

        return conn;
    }

    // adiciona ou remove nota
    public static int insertMatricula(Connection conn, Gerenciamentodenotas.requisicaoResponseNotas.Builder res,
            Gerenciamentodenotas.requisicaoNotas requisicaoNotas) {
        // pego os valores
        int ra = requisicaoNotas.getRa();
        String codigoDisciplina = requisicaoNotas.getCodDisciplina();
        int ano = requisicaoNotas.getAno();
        int semestre = requisicaoNotas.getSemestre();
        float nota = requisicaoNotas.getNota();
        res.setMessage("");

        try {

            // crio o statement que será usado para as querys
            Statement statement = conn.createStatement();

            // procuro pelo ra do aluno
            ResultSet resultadoQuery = statement
                    .executeQuery("SELECT * FROM aluno WHERE ra = " + String.valueOf(ra) + ";");
            if (!resultadoQuery.isBeforeFirst()) {
                res.setMessage("RA nao encontrado");
                return 0;
            }

            // procuro pelo codigo da disciplina
            resultadoQuery = statement.executeQuery(
                    "SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(codigoDisciplina) + "';");
            if (!resultadoQuery.isBeforeFirst()) {
                res.setMessage("Disciplina inexistente");
                return 0;
            }

            // procuro se o aluno está matriculado
            resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (ra_aluno = " + String.valueOf(ra)
                    + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "
                    + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
            if (!resultadoQuery.isBeforeFirst()) {
                statement.execute(
                        "INSERT INTO matricula (ano, semestre, cod_disciplina, ra_aluno, nota, faltas) VALUES ("
                                + String.valueOf(ano) + ", " + String.valueOf(semestre) + ", '"
                                + String.valueOf(codigoDisciplina) + "', " + String.valueOf(ra) + ", ''" + ", '');");

                res.setMessage("Cadastrado a matricula do aluno no ano de " + String.valueOf(ano) + ", no "
                        + String.valueOf(semestre) + " semestre.");
            }

            if (nota == -1) {
                // excluir a nota
                statement.execute("UPDATE matricula SET nota = '' WHERE (ra_aluno = " + String.valueOf(ra)
                        + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "
                        + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
            } else {
                // atualiza a nota
                statement.execute("UPDATE matricula SET nota = " + String.valueOf(nota) + " WHERE (ra_aluno = "
                        + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina)
                        + "' AND ano = " + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
            }

            if (res.getMessage().equals("")) {
                res.setMessage("OK");
            }

            return 0;
        } catch (SQLException e) {
            // erro ao realizar a ação
            res.setMessage(String.valueOf(e.getMessage()));
            return 0;
        }
    }

    /**
     * Lista os alunos
     * @param conn, utilizada para realizar a conexão com o banco de dados
     * @param res, resposta que será enviada para o cliente
     * @param requisicaoAlunos, variável que contém todos os campos necessários para realizar a requisição para o banco
     */
    public static int listAlunos(Connection conn, Gerenciamentodenotas.requisicaoResponseConsultaAlunos.Builder res,
            Gerenciamentodenotas.requisicaoConsultaAlunos requisicaoAlunos) {
        // pego os valores
        String discCode = requisicaoAlunos.getCodDisciplina();
        int ano = requisicaoAlunos.getAno();
        int semestre = requisicaoAlunos.getSemestre();

        try {
            // crio o statement para criar as querys
            Statement statement = conn.createStatement();

            // vejo se a disciplina existe
            ResultSet resultSet = statement
                    .executeQuery("SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(discCode) + "';");
            if (!resultSet.isBeforeFirst()) {
                res.setMessage("Disciplina inexistente");
                return 0;
            }

            // pego todos os alunos daquela disciplina
            resultSet = statement.executeQuery("SELECT * FROM aluno WHERE (select ra_aluno FROM matricula WHERE ano = "
                    + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + " AND cod_disciplina = '"
                    + String.valueOf(discCode) + "' AND matricula.ra_aluno = aluno.ra);");
            if (!resultSet.isBeforeFirst()) {
                res.setMessage("Nao ha alunos matriculados no ano de " + String.valueOf(ano) + ", no "
                        + String.valueOf(semestre) + " semestre, nessa disciplina.");
            }

            //salvo cada um dos alunos
            while (resultSet.next()) {
                Gerenciamentodenotas.Aluno.Builder aluno = Gerenciamentodenotas.Aluno.newBuilder();

                aluno.setRa(resultSet.getInt("ra"));
                aluno.setNome(resultSet.getString("nome"));
                aluno.setPeriodo(resultSet.getInt("periodo"));
                res.addAluno(aluno);
            }

            // defino a mensagem que será retornada como resposta
            res.setMessage("Resultado consulta:");
            return 0;
        } catch (SQLException e) {
            res.setMessage(String.valueOf(e.getMessage()));
            return 0;
        }
    }

    public static void main(String args[]) {
        Connection conn = connect();

        try {
            // se conecta ao socket na porta 7000
            int serverPort = 7000;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            Socket clientSocket = listenSocket.accept();

            // receber msg
            String valueStr;
            int sizeBuffer;
            byte[] buffer;

            // enviar msg
            byte[] msg;
            String msgSize;
            byte[] size;

            int result = 0;
            
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        } // catch

        try {
            if (null != conn) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } // main
} // class