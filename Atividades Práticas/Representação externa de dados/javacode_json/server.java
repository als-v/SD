/*
    ### Programação com Representação Externa de Dados ###
    # Autores: Juan e Alisson
    # Data de criação:      26/07/2021
    # Data de modificação:  02/08/2021
    # Este serviço promove a  comunicação através de um JSON, recebendo como parâmetro um mensagem do client.py, realizando
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
import com.google.gson.Gson;
import java.sql.*;

public class server {

    /**
    * Método para se conectar ao banco
    */
    public static Connection connect() {
        Connection conn = null;

        try {
            // Parâmetro do banco de dados
            String url = "jdbc:sqlite:../database/gerenciamento_notas.db";

            //Cria uma conecção ao banco de dados
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            // caso não consiga se conectar
            System.out.println(e.getMessage());
        }

        return conn;
    }

    // adiciona ou remove nota
    /**
     * Insere uma nova matrícula ou atualiza o campo de notas
     * @param conn, conexão para que se possa manipular as tabelas do banco
     * @param requisicao, contém os campos necessários para realizar a inserção no banco
     * @param response, resposta a requisição que será retornada para o cliente
     */
    public static void insertMatricula(Connection conn, requisicaoNota requisicao, requisicaoNotaResponse response) {
        // pego os valores
        int ra = requisicao.getAlunoRa();
        String codigoDisciplina = requisicao.getDisciplinaCodigo();
        int ano = requisicao.getDisciplinaAno();
        int semestre = requisicao.getDisciplinaSemestre();
        float nota = requisicao.getAlunoNota();
        String mensagem = "";
        response.setMessage("");

        try {
            // crio o statement que será usado para as querys
            Statement statement = conn.createStatement();

            // procuro pelo ra do aluno
            ResultSet resultadoQuery = statement
                    .executeQuery("SELECT * FROM aluno WHERE ra = " + String.valueOf(ra) + ";");
            if (!resultadoQuery.isBeforeFirst()) {
                mensagem = "RA nao encontrado";
                response.setMessage(mensagem);
                return;
            }
            
            // procuro pelo codigo da disciplina
            resultadoQuery = statement.executeQuery(
                "SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(codigoDisciplina) + "';");
                if (!resultadoQuery.isBeforeFirst()) {
                    mensagem = "Disciplina inexistente";
                    response.setMessage(mensagem);
                    return;
            }

            // procuro se o aluno está matriculado
            resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (ra_aluno = " + String.valueOf(ra)
                    + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "
                    + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + ");");
            if (!resultadoQuery.isBeforeFirst()) {
                statement.execute(
                        "INSERT INTO matricula (ano, semestre, cod_disciplina, ra_aluno, nota, faltas) VALUES ("
                                + String.valueOf(ano) + ", " + String.valueOf(semestre) + ", "
                                + String.valueOf(codigoDisciplina) + ", " + String.valueOf(ra) + ", ''" + ", '');");

                mensagem = "Cadastrado a matricula do aluno no ano de " + String.valueOf(ano) + ", no "
                        + String.valueOf(semestre) + " semestre.";
                response.setMessage(mensagem);
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

            if (response.getMessage().equals("")) {
                mensagem = "OK";
                response.setMessage(mensagem);
            }

        } catch (SQLException e) {
            // erro ao realizar a ação
            response.setMessage(String.valueOf(e.getMessage()));
        }
    }

    /**
     * Lista os alunos
     * @param conn, conexão para que se possa manipular as tabelas do banco
     * @param requisicao, contém os campos necessários para realizar a listagem de alunos no banco
     * @param response, resposta a requisição feita ao banco de dados
     */
    public static void listAlunos(Connection conn, requisicaoListAlunos res, requisicaoListAlunosResponse response) {
        // pego os valores
        String discCode = res.getDisciplinaCodigo();
        int ano = res.getDisciplinaAno();
        int semestre = res.getDisciplinaSemestre();
        String message = "";
        response.setMessage("");

        try {
            // crio o statement para criar as querys
            Statement statement = conn.createStatement();

            // vejo se a disciplina existe
            ResultSet resultSet = statement
                    .executeQuery("SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(discCode) + "';");
            if (!resultSet.isBeforeFirst()) {
                message = "Disciplina inexistente";
                response.setMessage(message);
            }

            // pego todos os alunos daquela disciplina
            resultSet = statement.executeQuery("SELECT * FROM aluno WHERE (select ra_aluno FROM matricula WHERE ano = "
                    + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + " AND cod_disciplina = '"
                    + String.valueOf(discCode) + "' AND matricula.ra_aluno = aluno.ra);");
            if (!resultSet.isBeforeFirst()) {
                message = "Nao ha alunos matriculados no ano de " + String.valueOf(ano) + ", no "
                        + String.valueOf(semestre) + " semestre, nessa disciplina.";
                response.setMessage(message);
            }

            // para cada um dos alunos, salvo eles
            while (resultSet.next()) {
                aluno aluno = new aluno();

                aluno.setRa(resultSet.getInt("ra"));
                aluno.setNome(resultSet.getString("nome"));
                aluno.setPeriodo(resultSet.getInt("periodo"));

                response.setAlunos(aluno);
            }

            // tudo certo
            message = "Resultado consulta:";
            response.setMessage(message);

        } catch (SQLException e) {
            response.setMessage(String.valueOf(e.getMessage()));
        }
    }

    public static void main(String args[]) {
        Connection conn = connect();

        try {
            // se conecta ao socket na porta 7001
            int serverPort = 7001;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            Socket clientSocket = listenSocket.accept();

            // variáveis utilizadas para receber msg
            String valueStr;
            int sizeBuffer;
            byte[] buffer;

            // variáveis utilizadas para enviar msg
            byte[] msg;
            String msgSize;
            byte[] size;
            byte[] msgEncode;

            while (true) {

                // enviar e receber pelo socket
                DataInputStream inClient = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outClient = new DataOutputStream(clientSocket.getOutputStream());

                // pega a primeira mensagem se é json ou protocolBuffer
                valueStr = inClient.readLine();
                sizeBuffer = Integer.valueOf(valueStr);
                buffer = new byte[sizeBuffer];
                inClient.read(buffer);

                String bufferJsonFlag = new String(buffer, "UTF-8");

                Gson gson = new Gson();
                requisicao requisicao = null;
                String json = null;

                // apenas espero comunicação via json
                if (bufferJsonFlag.equals("1")) {

                    // pega a proxima mensagem: requisicaoOpt: qual operacao usar
                    valueStr = inClient.readLine();
                    sizeBuffer = Integer.valueOf(valueStr);
                    buffer = new byte[sizeBuffer];
                    inClient.read(buffer);
                    json = new String(buffer, "UTF-8");

                    // realiza o unmarshalling
                    requisicao = gson.fromJson(json, requisicao.class);

                    // pega o json da requisicao
                    valueStr = inClient.readLine();
                    sizeBuffer = Integer.valueOf(valueStr);
                    buffer = new byte[sizeBuffer];
                    inClient.read(buffer);
                    json = new String(buffer, "UTF-8");

                    // executo as funcoes com base na requisicao
                    switch (requisicao.getRequisicaoOpt()) {
                        case 1:
                        case 2:
                            requisicaoNotaResponse requisicaoNotaResponse = new requisicaoNotaResponse();

                            // realiza o unmarshalling
                            requisicaoNota requisicaoNota = gson.fromJson(json, requisicaoNota.class);
                            insertMatricula(conn, requisicaoNota, requisicaoNotaResponse);

                            // formata a resposta para json
                            json = gson.toJson(requisicaoNotaResponse);

                            break;

                        case 3:
                            requisicaoListAlunosResponse requisicaoListAlunosResponse = new requisicaoListAlunosResponse();

                            // realiza o unmarshalling
                            requisicaoListAlunos requisicaoListAlunos = gson.fromJson(json, requisicaoListAlunos.class);
                            listAlunos(conn, requisicaoListAlunos, requisicaoListAlunosResponse);

                            // formata a resposta para Json
                            json = gson.toJson(requisicaoListAlunosResponse);

                            break;
                    }

                    // Codifica a mensagem para UTF-8
                    msgEncode = json.getBytes("UTF-8");

                    // Manda tamanho da resposta
                    msgSize = String.valueOf(msgEncode.length) + " \n";
                    size = msgSize.getBytes();
                    outClient.write(size);

                    // Manda resposta
                    outClient.write(msgEncode);
                } // while
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        } // catch

        // fechar a conexao do banco
        try {
            if (null != conn) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } // main
} // class