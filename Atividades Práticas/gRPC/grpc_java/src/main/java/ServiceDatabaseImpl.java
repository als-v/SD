/*
 ### Programação com Representação Externa de Dados ###
 # Autores: Juan e Alisson
 # Data de criação:      09/08/2021
 # Data de modificação:  12/08/2021
 # Este serviço promove a comunicação utilizando gRPC, sendo que seus métodos são invocados remotamente pelo client.py, recebendo uma mensagem do
 tipo Request com os parâmetros necessários para utilizar cada método especificamente, enviando como resposta uma mensagem do tipo Response, a qual
 tem como campos o status da operação (2 se a operação foi bem sucedida e 1 caso aconteça algum problema com a execução da operação), uma mensagem, 
 a qual descreve o status da operação, e uma lista de alunos que serão enviados de volta para o cliente com todos os campos preenchidos caso
o cliente realize alguma operação de consulta.   
*/

import io.grpc.stub.StreamObserver;
import java.sql.*;
import java.net.*;
import java.io.*;

/**
 *
 * @author juan
 * @author alisson
 */

public class ServiceDatabaseImpl extends ServiceDatabaseGrpc.ServiceDatabaseImplBase {

    public static Connection connect() {
        Connection conn = null;

        try {
            // db parameters
            String url = "jdbc:sqlite:../database/gerenciamento_notas.db";

            // create a connection to the database
            conn = DriverManager.getConnection(url);

        } catch (SQLException e) {
            // caso não consiga se conectar
            System.out.println(e.getMessage());
        }

        return conn;
    }

    // adiciona ou remove nota
    public static void alunoFunction(Connection conn, Response.Builder response, int opt, int ra, String codigoDisciplina, int ano, int semestre, float nota) {
        // flag para verificacao de quantos alunos tenho
        int flag = 0;

        try {
            // crio o statement que será usado para as querys
            Statement statement = conn.createStatement();

            // procuro pelo codigo da disciplina
            ResultSet resultadoQuery = statement.executeQuery(
                "SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(codigoDisciplina) + "';");
            if (!resultadoQuery.isBeforeFirst()) {
                response.setMessage("Disciplina inexistente");
                response.setStatus(1);
                return;
            }

            // caso eu queira apenas listar os alunos (opt = 7), nao preciso fazer algumas verificacoes
            if (opt != 7){
                // procuro pelo ra do aluno
                resultadoQuery = statement
                        .executeQuery("SELECT * FROM aluno WHERE ra = " + String.valueOf(ra) + ";");
                if (!resultadoQuery.isBeforeFirst()) {
                    response.setMessage("RA nao encontrado");
                    response.setStatus(1);
                    return;
                }
    
                // procuro se o aluno está matriculado
                resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (ra_aluno = " + String.valueOf(ra)
                        + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "
                        + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
                // se nao tiver matriculado, realiza a matricula
                if (!resultadoQuery.isBeforeFirst()) {
                    statement.execute(
                            "INSERT INTO matricula (ano, semestre, cod_disciplina, ra_aluno, nota, faltas) VALUES ("
                                    + String.valueOf(ano) + ", " + String.valueOf(semestre) + ", '"
                                    + String.valueOf(codigoDisciplina) + "', " + String.valueOf(ra) + ", ''" + ", '');");
                }
            }

            if(opt == 1 || opt == 3){
                // adiciona/altera nota ao aluno
                statement.execute("UPDATE matricula SET nota = " + String.valueOf(nota) + " WHERE (ra_aluno = "
                + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina)
                + "' AND ano = " + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
            
                response.setMessage("Operacao realizada com sucesso!");
                response.setStatus(2);

            } else if (opt == 2) {
                // exclui a nota
                statement.execute("UPDATE matricula SET nota = '' WHERE (ra_aluno = " + String.valueOf(ra)
                        + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "
                        + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
        
                response.setMessage("Operacao realizada com sucesso!");
                response.setStatus(2);
            } else if (opt == 4) {
                // pegar nota de um aluno
                resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (ra_aluno = " + String.valueOf(ra)
                + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "
                + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
                
                while(resultadoQuery.next()){
                    
                    Aluno.Builder aluno = Aluno.newBuilder();

                    
                    aluno.setRa(resultadoQuery.getInt("ra_aluno"));
                    aluno.setPeriodo(resultadoQuery.getInt("semestre"));
                    aluno.setNota(resultadoQuery.getFloat("nota"));
                    aluno.setFalta(resultadoQuery.getInt("faltas"));
                    
                    response.setMessage("Operacao realizada com sucesso!");
                    response.addAluno(aluno);
                    response.setStatus(2);
                }
    
            } else if (opt == 7) {
                // pegar os alunos
                resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (cod_disciplina = '" 
                + String.valueOf(codigoDisciplina) + "' AND ano = "
                + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
                
                while(resultadoQuery.next()){
                    flag += 1;

                    Aluno.Builder aluno = Aluno.newBuilder();
                    
                    aluno.setRa(resultadoQuery.getInt("ra_aluno"));
                    aluno.setPeriodo(resultadoQuery.getInt("semestre"));
                    aluno.setNota(resultadoQuery.getFloat("nota"));
                    aluno.setFalta(resultadoQuery.getInt("faltas"));

                    response.addAluno(aluno);
                }

                if (flag == 0){
                    response.setMessage("Nenhum aluno encontrado!");
                } else {
                    response.setMessage("Listagem de alunos:");
                }

                response.setStatus(2);
            }

        } catch (SQLException e) {
            // erro ao realizar a ação
            response.setMessage(String.valueOf(e.getMessage()));
            response.setStatus(1);
        }
    }

    // adiciona ou remove nota
    public static void consultaNotasFaltas(Connection conn, Response.Builder response, int opt, String codigoDisciplina, int ano, int semestre) {
        // flag para verificacao de quantos alunos tenho
        int qtd = 0;

        try {

            // crio o statement que será usado para as querys
            Statement statement = conn.createStatement();
            
            // procuro pelo codigo da disciplina
            ResultSet resultadoQuery = statement.executeQuery(
                "SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(codigoDisciplina) + "';");
            if (!resultadoQuery.isBeforeFirst()) {
                response.setMessage("Disciplina inexistente");
                response.setStatus(1);

                return;
            }
            
            // procuro pelo ano ou pelo semestre
            if (opt == 5) {
                resultadoQuery = statement.executeQuery(
                    "SELECT * FROM matricula WHERE cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = " + 
                    String.valueOf(ano) + ";");
            } else if (opt == 6) {
                resultadoQuery = statement.executeQuery(
                    "SELECT * FROM matricula WHERE cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND semestre = " +
                    String.valueOf(semestre) + ";");
            }


            while(resultadoQuery.next()){
                qtd += 1;
                Aluno.Builder aluno = Aluno.newBuilder();

                aluno.setNota(resultadoQuery.getFloat("nota"));
                aluno.setFalta(resultadoQuery.getInt("faltas"));
                
                response.addAluno(aluno);
            }

            if (qtd == 0){
                response.setMessage("Nenhum registro encontrado:");
            } else {
                response.setMessage("Resultados:");
            }

            response.setStatus(2);

        } catch (SQLException e) {
            // erro ao realizar a ação
            response.setMessage(String.valueOf(e.getMessage()));
            response.setStatus(1);
        }

    }

    @Override
    public void gerenciaNotas(Request request, StreamObserver<Response> responseObserver) {
        // conecao com o banco
        Connection conn = connect();

        // crio a resposta
        Response.Builder response = Response.newBuilder();

        switch(request.getOpt()){
            case 1:
            case 2:
            case 3:
            case 4:
            case 7:
                // chamo a funcao relacionado ao aluno
                alunoFunction(conn, response, request.getOpt(), request.getRa(), request.getCodDisciplina(), request.getAno(), request.getSemestre(), request.getNota());
                break;
            case 5:
            case 6:
                // chamo a funcao relacionado as notas e faltas
                consultaNotasFaltas(conn, response, request.getOpt(), request.getCodDisciplina(), request.getAno(), request.getSemestre());
                break;
            default:
                response.setMessage("ERRO: O servidor não consegue lidar com essa requisição!");
                response.setStatus(1);
                break;
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();

        try {
            if (null != conn) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
    }
}
