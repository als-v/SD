
import io.grpc.stub.StreamObserver;
import java.sql.*;
import java.net.*;
import java.io.*;

/**
 *
 * @author rodrigo
 */

public class ServiceDatabaseImpl extends ServiceDatabaseGrpc.ServiceDatabaseImplBase {

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
    public static Response alunoFunction(Connection conn, int opt, int ra, String codigoDisciplina, int ano, int semestre, float nota) {

        try {

            // crio o statement que será usado para as querys
            Statement statement = conn.createStatement();

            // procuro pelo ra do aluno
            ResultSet resultadoQuery = statement
                    .executeQuery("SELECT * FROM aluno WHERE ra = " + String.valueOf(ra) + ";");
            if (!resultadoQuery.isBeforeFirst()) {
                Response response = Response.newBuilder()
                .setMessage("RA nao encontrado")
                .setStatus(1)
                .build();

                return response;
            }

            // procuro pelo codigo da disciplina
            resultadoQuery = statement.executeQuery(
                    "SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(codigoDisciplina) + "';");
            if (!resultadoQuery.isBeforeFirst()) {
                Response response = Response.newBuilder()
                .setMessage("Disciplina inexistente")
                .setStatus(1)
                .build();

                return response;
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
            }

            if(opt == 1 || opt == 3){
                // adiciona/altera nota ao aluno
                statement.execute("UPDATE matricula SET nota = " + String.valueOf(nota) + " WHERE (ra_aluno = "
                + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina)
                + "' AND ano = " + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
            
                Response response = Response.newBuilder()
                .setMessage("Operacao realizada com sucesso!")
                .setStatus(2)
                .build();
    
                return response;
            } else if (opt == 2) {
                // excluir a nota
                statement.execute("UPDATE matricula SET nota = '' WHERE (ra_aluno = " + String.valueOf(ra)
                        + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "
                        + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
        
                Response response = Response.newBuilder()
                .setMessage("Operacao realizada com sucesso!")
                .setStatus(2)
                .build();
    
                return response;
            } 
            
            if (opt == 4) {
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
                    
                    Response response = Response.newBuilder()
                    .setMessage("Operacao realizada com sucesso!")
                    .addAluno(aluno)
                    .setStatus(2)
                    .build();

                    return response;
                }
    
            }

        } catch (SQLException e) {
            // erro ao realizar a ação
            Response response = Response.newBuilder()
                .setMessage(String.valueOf(e.getMessage()))
                .setStatus(1)
                .build();

            return response;
        }

        return null;
    }

    @Override
    public void gerenciaNotas(Request request, StreamObserver<Response> responseObserver) {
        Connection conn = connect();
        Response response = null;

        switch(request.getOpt()){
            case 1:
            case 2:
            case 3:
            case 4:
            // todo: adicionar/remover/alterar
            //todo: listar alunos
            response = alunoFunction(conn, request.getOpt(), request.getRa(), request.getCodDisciplina(), request.getAno(), request.getSemestre(), request.getNota());
            break;
        }

        responseObserver.onNext(response);
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
