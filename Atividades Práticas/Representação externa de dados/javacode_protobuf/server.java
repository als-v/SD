import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Map;

import java.sql.*;

public class server {

    // metodo para se conectar ao banco
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
    public static void insertMatricula(Connection conn, Gerenciamentodenotas.requisicaoResponseNotas.Builder res,
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
                return;
            }

            // procuro pelo codigo da disciplina
            resultadoQuery = statement.executeQuery(
                    "SELECT * FROM disciplina WHERE codigo = '" + String.valueOf(codigoDisciplina) + "';");
            if (!resultadoQuery.isBeforeFirst()) {
                res.setMessage("Disciplina inexistente");
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

        } catch (SQLException e) {
            // erro ao realizar a ação
            res.setMessage(String.valueOf(e.getMessage()));
        }
    }

    // lista os alunos
    public static void listAlunos(Connection conn, Gerenciamentodenotas.requisicaoResponseConsultaAlunos.Builder res,
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
            }

            // pego todos os alunos daquela disciplina
            resultSet = statement.executeQuery("SELECT * FROM aluno WHERE (select ra_aluno FROM matricula WHERE ano = "
                    + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + " AND cod_disciplina = '"
                    + String.valueOf(discCode) + "' AND matricula.ra_aluno = aluno.ra);");
            if (!resultSet.isBeforeFirst()) {
                res.setMessage("Nao ha alunos matriculados no ano de " + String.valueOf(ano) + ", no "
                        + String.valueOf(semestre) + " semestre, nessa disciplina.");
            }

            // para cada um dos alunos, salvo eles
            while (resultSet.next()) {
                Gerenciamentodenotas.Aluno.Builder aluno = Gerenciamentodenotas.Aluno.newBuilder();

                aluno.setRa(resultSet.getInt("ra"));
                aluno.setNome(resultSet.getString("nome"));
                aluno.setPeriodo(resultSet.getInt("periodo"));
                res.addAluno(aluno);
            }

            // tudo certo
            res.setMessage("Resultado consulta:");
        } catch (SQLException e) {
            res.setMessage(String.valueOf(e.getMessage()));
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

            while (true) {

                // enviar e receber pelo socket
                DataInputStream inClient = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outClient = new DataOutputStream(clientSocket.getOutputStream());

                // pega a primeira mensagem (Json ou buffer)
                valueStr = inClient.readLine();
                sizeBuffer = Integer.valueOf(valueStr);
                buffer = new byte[sizeBuffer];
                inClient.read(buffer);

                String bufferJsonFlag = new String(buffer, "UTF-8");

                // variaveis para as requisicoes
                Gerenciamentodenotas.requisicaoOpt p = null;
                
                // apenas espero comunicacao protobuf
                if (bufferJsonFlag.equals("0")) {

                    // pega a proxima mensagem: requisicaoOpt: qual operacao usar
                    valueStr = inClient.readLine();
                    sizeBuffer = Integer.valueOf(valueStr);
                    buffer = new byte[sizeBuffer];
                    inClient.read(buffer);

                    // realiza o unmarshalling
                    p = Gerenciamentodenotas.requisicaoOpt.parseFrom(buffer);

                    // leio a mensagem
                    valueStr = inClient.readLine();
                    sizeBuffer = Integer.valueOf(valueStr);
                    buffer = new byte[sizeBuffer];
                    inClient.read(buffer);

                    switch (p.getOpt()) {
                        case 1:
                            // realiza o unmarshalling
                            Gerenciamentodenotas.requisicaoResponseNotas.Builder resNota = Gerenciamentodenotas.requisicaoResponseNotas.newBuilder();
                            Gerenciamentodenotas.requisicaoNotas requisicaoAddNota = Gerenciamentodenotas.requisicaoNotas
                                    .parseFrom(buffer);
                            insertMatricula(conn, resNota, requisicaoAddNota);

                            // serializo a resposta
                            msg = resNota.toString().getBytes();

                            // manda o tamanho da resposta
                            msgSize = String.valueOf(msg.length) + " \n";
                            size = msgSize.getBytes();
                            outClient.write(size);

                            // mando a resposta
                            outClient.write(msg);

                            break;

                        case 2:
                            // realiza o unmarshalling
                            Gerenciamentodenotas.requisicaoResponseNotas.Builder resNota2 = Gerenciamentodenotas.requisicaoResponseNotas.newBuilder();
                            Gerenciamentodenotas.requisicaoNotas requisicaoRemoveNotas = Gerenciamentodenotas.requisicaoNotas
                            .parseFrom(buffer);
                            insertMatricula(conn, resNota2, requisicaoRemoveNotas);

                            // serializo a resposta
                            msg = resNota2.toString().getBytes();

                            // manda o tamanho da resposta
                            msgSize = String.valueOf(msg.length) + " \n";
                            size = msgSize.getBytes();
                            outClient.write(size);

                            // mando a resposta
                            outClient.write(msg);
                            break;
                            
                        case 3:
                            /* realiza o unmarshalling */
                            Gerenciamentodenotas.requisicaoResponseConsultaAlunos.Builder resAluno = Gerenciamentodenotas.requisicaoResponseConsultaAlunos.newBuilder();
                            Gerenciamentodenotas.requisicaoConsultaAlunos requisicaoListAlunos = Gerenciamentodenotas.requisicaoConsultaAlunos
                                    .parseFrom(buffer);
                            listAlunos(conn, resAluno, requisicaoListAlunos);

                            // serializo a resposta
                            msg = resAluno.toString().getBytes();

                            // manda o tamanho da resposta
                            msgSize = String.valueOf(msg.length) + " \n";
                            size = msgSize.getBytes();
                            outClient.write(size);

                            // mando a resposta
                            outClient.write(msg);
                            
                            break;
                    }
                }
            } // while
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