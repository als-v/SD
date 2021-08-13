/*
 ### RPC ###
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
 * @author alisson
 * @author juan
 */

/**
 * Interface utilizada para que seja possível acessar seus métodos
 */
public class ServiceDatabaseImpl extends ServiceDatabaseGrpc.ServiceDatabaseImplBase {

    public static Connection connect() {
        Connection conn = null;

        try {
            // Parâmetros do banco de dados
            String url = "jdbc:sqlite:../database/gerenciamento_notas.db";

            // Cria uma conexão com o banco de dados
            conn = DriverManager.getConnection(url);

        } catch (SQLException e) {
            // Caso não consiga se conectar
            System.out.println(e.getMessage());
        }

        return conn;
    }

    /**
     * Este método realiza todas funcionalidades que envolvem lidar com alunos ( realiza as operações 1,2,3,4 e 7) 
     * @param conn, conexão estabelecida com o banco de dados
     * @param response, mensagem de resposta que será enviada para o cliente
     * @param opt, operação que será realizada
     * @param ra, parâmetro que representa o RA de um aluno
     * @param codigoDisciplina, parâmetro que representa o código de uma disciplina, utilizado para consulta 
     * @param ano, parâmetro que representa o ano que uma disciplina foi ofertada
     * @param semestre, parâmetro que representa o semestre que uma disciplina foi ofertada
     * @param nota, parâmetro que representa a nota que será atribuida a um aluno
     */
    public static void alunoFunction(Connection conn, Response.Builder response, int opt, int ra, String codigoDisciplina, int ano, int semestre, float nota) {
        // Flag para verifica do número de alunos
        int flag = 0;

        try {
            // Criando o statement que será usado para as querys
            Statement statement = conn.createStatement();

            // Procurando pelo codigo da disciplina
            ResultSet resultadoQuery = statement.executeQuery(
                "SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(codigoDisciplina) + "';");
            if (!resultadoQuery.isBeforeFirst()) {
                response.setMessage("Disciplina inexistente");
                response.setStatus(1);
                return;
            }

            // Caso eu queira apenas listar os alunos (opt = 7), não é necessário fazer algumas verificações
            if (opt != 7){
                // Procuro pelo ra do aluno
                resultadoQuery = statement
                        .executeQuery("SELECT * FROM aluno WHERE ra = " + String.valueOf(ra) + ";");
                if (!resultadoQuery.isBeforeFirst()) {
                    response.setMessage("RA nao encontrado");
                    response.setStatus(1);
                    return;
                }
    
                // Verifico se o aluno está matriculado
                resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (ra_aluno = " + String.valueOf(ra)
                        + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "
                        + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
                
                // Caso não esteja matriculado, realiza a matricula
                if (!resultadoQuery.isBeforeFirst()) {
                    statement.execute(
                            "INSERT INTO matricula (ano, semestre, cod_disciplina, ra_aluno, nota, faltas) VALUES ("
                                    + String.valueOf(ano) + ", " + String.valueOf(semestre) + ", '"
                                    + String.valueOf(codigoDisciplina) + "', " + String.valueOf(ra) + ", ''" + ", '');");
                }
            }

            if(opt == 1 || opt == 3){
                // Adiciona/altera nota ao aluno
                statement.execute("UPDATE matricula SET nota = " + String.valueOf(nota) + " WHERE (ra_aluno = "
                + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina)
                + "' AND ano = " + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
            
                response.setMessage("Operacao realizada com sucesso!");
                response.setStatus(2);

            } else if (opt == 2) {
                // Exclui a nota
                statement.execute("UPDATE matricula SET nota = '' WHERE (ra_aluno = " + String.valueOf(ra)
                        + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "
                        + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
        
                response.setMessage("Operacao realizada com sucesso!");
                response.setStatus(2);
            } else if (opt == 4) {
                // Pega nota de um aluno
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
                // Pega os alunos
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
            // Erro ao realizar a ação
            response.setMessage(String.valueOf(e.getMessage()));
            response.setStatus(1);
        }
    }

    // adiciona ou remove nota
    /**
     * Este método consulta notas e falta de uma disciplina dado um ano ou semestre
     * @param conn, conexão estabelecida com o banco de dados
     * @param response, mensagem de resposta que será enviada para o cliente
     * @param opt, operação que será realizada
     * @param codigoDisciplina, parâmetro que representa o código de uma disciplina, utilizado para consulta 
     * @param ano, parâmetro que representa o ano que uma disciplina foi ofertada
     * @param semestre, parâmetro que representa o semestre que uma disciplina foi ofertada
     */
    public static void consultaNotasFaltas(Connection conn, Response.Builder response, int opt, String codigoDisciplina, int ano, int semestre) {
        // Flag para verificação da quantidade de alunos
        int qtd = 0;

        try {

            // Criando o statement que será usado para as querys
            Statement statement = conn.createStatement();
            
            // Procuro pelo codigo da disciplina
            ResultSet resultadoQuery = statement.executeQuery(
                "SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(codigoDisciplina) + "';");
            if (!resultadoQuery.isBeforeFirst()) {
                response.setMessage("Disciplina inexistente");
                response.setStatus(1);

                return;
            }
            
            // Procuro pelo ano ou pelo semestre
            if (opt == 5) {
                resultadoQuery = statement.executeQuery(
                    "SELECT * FROM matricula WHERE cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = " + 
                    String.valueOf(ano) + ";");
            } else if (opt == 6) {
                resultadoQuery = statement.executeQuery(
                    "SELECT * FROM matricula WHERE cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND semestre = " +
                    String.valueOf(semestre) + ";");
            }

            // Enquanto houverem resultados
            while(resultadoQuery.next()){
                qtd += 1;
                Aluno.Builder aluno = Aluno.newBuilder();

                aluno.setNota(resultadoQuery.getFloat("nota"));
                aluno.setFalta(resultadoQuery.getInt("faltas"));
                
                response.addAluno(aluno);
            }
            
            // Caso nenhum aluno seja encontrado
            if (qtd == 0){
                response.setMessage("Nenhum registro encontrado:");
            } else {
                response.setMessage("Resultados:");
            }

            response.setStatus(2);

        } catch (SQLException e) {
            // Erro ao realizar a ação
            response.setMessage(String.valueOf(e.getMessage()));
            response.setStatus(1);
        }

    }

    @Override
    public void gerenciaNotas(Request request, StreamObserver<Response> responseObserver) {
        // Realiza conexão com o banco
        Connection conn = connect();

        // Resposta é criada
        Response.Builder response = Response.newBuilder();

        switch(request.getOpt()){
            case 1:
            case 2:
            case 3:
            case 4:
            case 7:
                // Método relacionado a alunos é chamado
                alunoFunction(conn, response, request.getOpt(), request.getRa(), request.getCodDisciplina(), request.getAno(), request.getSemestre(), request.getNota());
                break;
            case 5:
            case 6:
                // Método relacionado as notas e faltas é chamado
                consultaNotasFaltas(conn, response, request.getOpt(), request.getCodDisciplina(), request.getAno(), request.getSemestre());
                break;
            default:
                response.setMessage("ERRO: O servidor não consegue lidar com essa requisição!");
                response.setStatus(1);
                break;
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();

        // Caso não seja possível se conectar com o banco de dados
        try {
            if (null != conn) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
    }
}
