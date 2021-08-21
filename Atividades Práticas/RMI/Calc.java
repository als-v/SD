
/**
 * Implementacao do objeto remoto
 * autor: Rodrigo Campiolo
 * data: 22/11/2006
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class Calc extends UnicastRemoteObject implements Calculadora {

    public Calc() throws RemoteException {
        super();
        System.out.println("Objeto remoto instanciado");
    }

    /**
     * Este método realiza todas funcionalidades que envolvem lidar com alunos (
     * realiza as operações 1,2,3,4 e 7)
     * 
     * @param conn,             conexão estabelecida com o banco de dados
     * @param response,         mensagem de resposta que será enviada para o cliente
     * @param opt,              operação que será realizada
     * @param ra,               parâmetro que representa o RA de um aluno
     * @param codigoDisciplina, parâmetro que representa o código de uma disciplina,
     *                          utilizado para consulta
     * @param ano,              parâmetro que representa o ano que uma disciplina
     *                          foi ofertada
     * @param semestre,         parâmetro que representa o semestre que uma
     *                          disciplina foi ofertada
     * @param nota,             parâmetro que representa a nota que será atribuida a
     *                          um aluno
     */
    public response alunoFunction(int opt, int ra, String codigoDisciplina, int ano, int semestre, float nota) {
        // Flag para verifica do número de alunos
        int flag = 0;

        response response = new response();

        Connection conn = db.connect();

        try {
            // Criando o statement que será usado para as querys
            Statement statement = conn.createStatement();
            ResultSet resultadoQuery;

            // Procurando pelo codigo da disciplina
            resultadoQuery = statement.executeQuery(
                    "SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(codigoDisciplina) + "';");
            if (!resultadoQuery.isBeforeFirst()) {
                response.setMessage("Disciplina inexistente");
                response.setStatusCode(0);
                return response;
            }

            // Caso eu queira apenas listar os alunos (opt = 7), não é necessário fazer
            // algumas verificações
            if (opt != 7) {
                // Procuro pelo ra do aluno
                resultadoQuery = statement.executeQuery("SELECT * FROM aluno WHERE ra = " + String.valueOf(ra) + ";");
                if (!resultadoQuery.isBeforeFirst()) {
                    response.setMessage("RA nao encontrado");
                    response.setStatusCode(0);
                    return response;
                }

                // Verifico se o aluno está matriculado
                resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (ra_aluno = "
                        + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina)
                        + "' AND ano = " + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");

                // Caso não esteja matriculado, realiza a matricula
                if (!resultadoQuery.isBeforeFirst()) {
                    statement.execute(
                            "INSERT INTO matricula (ano, semestre, cod_disciplina, ra_aluno, nota, faltas) VALUES ("
                                    + String.valueOf(ano) + ", " + String.valueOf(semestre) + ", '"
                                    + String.valueOf(codigoDisciplina) + "', " + String.valueOf(ra) + ", ''"
                                    + ", '');");
                }
            }

            if (opt == 1 || opt == 3) {
                // Adiciona/altera nota ao aluno
                statement.execute("UPDATE matricula SET nota = " + String.valueOf(nota) + " WHERE (ra_aluno = "
                        + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina)
                        + "' AND ano = " + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");

                response.setMessage("Operacao realizada com sucesso!");
                response.setStatusCode(1);
            } else if (opt == 2) {
                // Exclui a nota
                statement.execute("UPDATE matricula SET nota = '' WHERE (ra_aluno = " + String.valueOf(ra)
                        + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "
                        + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");

                response.setMessage("Operacao realizada com sucesso!");
                response.setStatusCode(1);
            } else if (opt == 4) {
                // Pega nota de um aluno
                resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (ra_aluno = "
                        + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina)
                        + "' AND ano = " + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");

                while (resultadoQuery.next()) {

                    aluno aluno = new aluno();

                    aluno.setRa(resultadoQuery.getInt("ra_aluno"));
                    aluno.setPeriodo(resultadoQuery.getInt("semestre"));
                    aluno.setNota(resultadoQuery.getFloat("nota"));
                    aluno.setFalta(resultadoQuery.getInt("faltas"));

                    response.setMessage("Operacao realizada com sucesso!");
                    response.addAlunosResponse(aluno);
                    response.setStatusCode(1);

                }

            } else if (opt == 7) {
                // Pega os alunos
                resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (cod_disciplina = '"
                        + String.valueOf(codigoDisciplina) + "' AND ano = " + String.valueOf(ano) + " AND semestre = "
                        + String.valueOf(semestre) + ");");

                while (resultadoQuery.next()) {
                    flag += 1;

                    aluno aluno = new aluno();

                    aluno.setRa(resultadoQuery.getInt("ra_aluno"));
                    aluno.setPeriodo(resultadoQuery.getInt("semestre"));
                    aluno.setNota(resultadoQuery.getFloat("nota"));
                    aluno.setFalta(resultadoQuery.getInt("faltas"));

                    response.addAlunosResponse(aluno);
                }

                if (flag == 0) {
                    response.setMessage("Nenhum aluno encontrado!");
                    response.setStatusCode(1);
                } else {
                    response.setMessage("Listagem de alunos:");
                    response.setStatusCode(1);
                }

            }

            db.disconnect(conn);
            return response;

        } catch (SQLException e) {
            // Erro ao realizar a ação
            response.setMessage(String.valueOf(e.getMessage()));
            response.setStatusCode(0);
            db.disconnect(conn);
            return response;
        }
    }

    // adiciona ou remove nota
    /**
     * Este método consulta notas e falta de uma disciplina dado um ano ou semestre
     * 
     * @param conn,             conexão estabelecida com o banco de dados
     * @param response,         mensagem de resposta que será enviada para o cliente
     * @param opt,              operação que será realizada
     * @param codigoDisciplina, parâmetro que representa o código de uma disciplina,
     *                          utilizado para consulta
     * @param ano,              parâmetro que representa o ano que uma disciplina
     *                          foi ofertada
     * @param semestre,         parâmetro que representa o semestre que uma
     *                          disciplina foi ofertada
     */
    public response consultaNotasFaltas(int opt, String codigoDisciplina, int ano, int semestre) {
        // Flag para verificação da quantidade de alunos
        int qtd = 0;

        response response = new response();

        Connection conn = db.connect();

        try {

            // Criando o statement que será usado para as querys
            Statement statement = conn.createStatement();

            // Procuro pelo codigo da disciplina
            ResultSet resultadoQuery = statement.executeQuery(
                    "SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(codigoDisciplina) + "';");
            if (!resultadoQuery.isBeforeFirst()) {
                response.setMessage("Disciplina inexistente");
                response.setStatusCode(0);
                return response;
            }

            // Procuro pelo ano ou pelo semestre
            if (opt == 5) {
                resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE cod_disciplina = '"
                        + String.valueOf(codigoDisciplina) + "' AND ano = " + String.valueOf(ano) + ";");
            } else if (opt == 6) {
                resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE cod_disciplina = '"
                        + String.valueOf(codigoDisciplina) + "' AND semestre = " + String.valueOf(semestre) + ";");
            }

            // Enquanto houverem resultados
            while (resultadoQuery.next()) {
                qtd += 1;

                disciplina disciplina = new disciplina();

                disciplina.setNota(resultadoQuery.getFloat("nota"));
                disciplina.setFalta(resultadoQuery.getInt("faltas"));

                response.addDisciplinaResponse(disciplina);
            }

            // Caso nenhum aluno seja encontrado
            if (qtd == 0) {
                response.setMessage("Nenhum registro encontrado:");
                response.setStatusCode(1);
            } else {
                response.setMessage("Resultados:");
                response.setStatusCode(1);
            }

            db.disconnect(conn);
            return response;

        } catch (SQLException e) {
            // Erro ao realizar a ação
            response.setMessage(String.valueOf(e.getMessage()));
            db.disconnect(conn);
            return response;
        }
    }

} // Calc
