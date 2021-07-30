import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.sql.*;

public class server {
    
    public static Connection connect(){
        Connection conn = null;

        try {
            // db parameters
            String url = "jdbc:sqlite:../database/gerenciamento_notas.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return conn;
    }
    
    public static int insertMatricula(Connection conn, Gerenciamentodenotas.requisicaoResponseNotas.Builder res, Gerenciamentodenotas.requisicaoNotas requisicaoNotas) {
        int ra = requisicaoNotas.getRa();
        String codigoDisciplina = requisicaoNotas.getCodDisciplina();
        int ano = requisicaoNotas.getAno();
        int semestre = requisicaoNotas.getSemestre();
        float nota = requisicaoNotas.getNota();
        
        try{  

            Statement statement = conn.createStatement();
            
            // tem aluno?
            ResultSet resultadoQuery = statement.executeQuery("SELECT * FROM aluno WHERE ra = " + String.valueOf(ra) + ";");
            if(!resultadoQuery.isBeforeFirst()){
                res.setMessage("RA nao encontrado");
                return 1;
            }

            // tem disciplina?
            resultadoQuery = statement.executeQuery("SELECT * FROM disciplina WHERE (codigo = '" + String.valueOf(codigoDisciplina) + "')");
            if(!resultadoQuery.isBeforeFirst()){
                res.setMessage("Disciplina inexistente");
                return 1;
            }

            // tem matricula?
            resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (ra_aluno = " + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "+ String.valueOf(ano) +" AND semestre = "+ String.valueOf(semestre) +");");
            if(!resultadoQuery.isBeforeFirst()){
                res.setMessage("Nao encontrado a matricula do aluno no ano de " + String.valueOf(ano) + ", no " + String.valueOf(semestre) + " semestre.");
                return 1;
            }

            if(nota == 0){
                // excluir a nota
                statement.execute("UPDATE matricula SET nota = '' WHERE (ra_aluno = " + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "+ String.valueOf(ano) +" AND semestre = "+ String.valueOf(semestre) +");");
                res.setMessage("OK");
            } else {
                // atualiza a nota
                statement.execute("UPDATE matricula SET nota = " + String.valueOf(nota) + " WHERE (ra_aluno = " + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "+ String.valueOf(ano) +" AND semestre = "+ String.valueOf(semestre) +");");
                res.setMessage("OK");
            }

            return 0;
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
            return 1;
        }  
    }

    public static int listAlunos(Connection conn, Gerenciamentodenotas.requisicaoResponseConsultaAlunos.Builder res, Gerenciamentodenotas.requisicaoConsultaAlunos requisicaoAlunos) {
        String discCode = requisicaoAlunos.getCodDisciplina();
        int ano = requisicaoAlunos.getAno();
        int semestre = requisicaoAlunos.getSemestre();
    
        try {
    
          Statement statement = conn.createStatement();
    
          // pego a disciplina
          ResultSet resultSet = statement.executeQuery("SELECT * FROM disciplina WHERE (codigo = '" + String.valueOf(discCode) + "');");
          if(!resultSet.isBeforeFirst()){
            res.setMessage("Disciplina inexistente");
            return 1;
        }
        
        // pego todos os alunos
        resultSet = statement.executeQuery("SELECT * FROM aluno WHERE (select ra_aluno FROM matricula WHERE ano = " + String.valueOf(ano) + " AND semestre = " + String.valueOf(semestre) + " AND cod_disciplina = '" + String.valueOf(discCode) + "' AND matricula.ra_aluno = aluno.ra);");
        if(!resultSet.isBeforeFirst()){
            res.setMessage("Nesta disciplina nao ha alunos matriculados no ano de " + String.valueOf(ano) + ", no " + String.valueOf(semestre) + " semestre.");
            return 1;
        }
          
        // para cada um deles
        while (resultSet.next()) {
            Gerenciamentodenotas.Aluno.Builder aluno = Gerenciamentodenotas.Aluno.newBuilder();
            
            aluno.setRa(resultSet.getInt("ra"));
            aluno.setNome(resultSet.getString("nome"));
            aluno.setPeriodo(resultSet.getInt("periodo"));
            res.addAluno(aluno);
        }
        
        // mando OK
        res.setMessage("Resultado consulta:");
        
          return 1;
        } catch (SQLException e) {
          res.setMessage(String.valueOf(e.getMessage()));
          return 0;
        }

      }

    public static void main(String args[]) {
        Connection conn = connect();

        try {
            int serverPort = 7000; 
            ServerSocket listenSocket = new ServerSocket(serverPort);

            Socket clientSocket = listenSocket.accept();
            while (true) {

                DataInputStream inClient = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outClient = new DataOutputStream(clientSocket.getOutputStream());
                
                String valueStr = inClient.readLine();
                int sizeBuffer = Integer.valueOf(valueStr);
                byte[] buffer = new byte[sizeBuffer];
                inClient.read(buffer);
                
                /* realiza o unmarshalling */
                Gerenciamentodenotas.requisicaoOpt p = Gerenciamentodenotas.requisicaoOpt.parseFrom(buffer);
                
                /* exibe na tela */
                System.out.println("--\n" + p + "--\n");
                
                /* Instancia a resposta */
                Gerenciamentodenotas.requisicaoResponseNotas.Builder resNota = Gerenciamentodenotas.requisicaoResponseNotas.newBuilder();
                Gerenciamentodenotas.requisicaoResponseConsultaAlunos.Builder resAluno = Gerenciamentodenotas.requisicaoResponseConsultaAlunos.newBuilder();
                int statusCode = 1;

                switch (p.getOpt()) {
                    case 1:
                        valueStr = inClient.readLine();
                        sizeBuffer = Integer.valueOf(valueStr);
                        buffer = new byte[sizeBuffer];
                        inClient.read(buffer);
                    
                        /* realiza o unmarshalling */
                        Gerenciamentodenotas.requisicaoNotas requisicaoAddNota = Gerenciamentodenotas.requisicaoNotas.parseFrom(buffer);
                        
                        statusCode = insertMatricula(conn, resNota, requisicaoAddNota);

                        /* Serializa resposta */
                        byte[] msgAddNota = resNota.toString().getBytes();
                        
                        /* Manda tamanho da resposta */
                        String msgSizeAddNota = String.valueOf(msgAddNota.length) + " \n";
                        byte[] sizeAddNota = msgSizeAddNota.getBytes();
                        outClient.write(sizeAddNota);
                        
                        /* Manda resposta */
                        outClient.write(msgAddNota);
                        
                        break;
                    
                    case 2:
                        valueStr = inClient.readLine();
                        sizeBuffer = Integer.valueOf(valueStr);
                        buffer = new byte[sizeBuffer];
                        inClient.read(buffer);
                        
                        /* realiza o unmarshalling */
                        Gerenciamentodenotas.requisicaoNotas requisicaoRemoveNotas = Gerenciamentodenotas.requisicaoNotas.parseFrom(buffer);

                        statusCode = insertMatricula(conn, resNota, requisicaoRemoveNotas);

                        /* Serializa resposta */
                        byte[] msgRemoveNota = resNota.toString().getBytes();
                        
                        /* Manda tamanho da resposta */
                        String msgSizeRemoveNota = String.valueOf(msgRemoveNota.length) + " \n";
                        byte[] sizeRemoveNota = msgSizeRemoveNota.getBytes();
                        outClient.write(sizeRemoveNota);
                        
                        /* Manda resposta */
                        outClient.write(msgRemoveNota);

                        break;
                    
                    case 3:
                        valueStr = inClient.readLine();
                        sizeBuffer = Integer.valueOf(valueStr);
                        buffer = new byte[sizeBuffer];
                        inClient.read(buffer);
                        
                        /* realiza o unmarshalling */
                        Gerenciamentodenotas.requisicaoConsultaAlunos requisicaoListAlunos = Gerenciamentodenotas.requisicaoConsultaAlunos.parseFrom(buffer);

                        statusCode = listAlunos(conn, resAluno, requisicaoListAlunos);

                        /* Serializa resposta */
                        byte[] msgAlunoList = resAluno.toString().getBytes();
                        
                        /* Manda tamanho da resposta */
                        String msgSizeAlunoList = String.valueOf(msgAlunoList.length) + " \n";
                        byte[] sizeAlunoList = msgSizeAlunoList.getBytes();
                        outClient.write(sizeAlunoList);
                        
                        /* Manda resposta */
                        outClient.write(msgAlunoList);

                        break;
                }
                
            } //while
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        } //catch

        try {
            if(null != conn){
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } //main
} //class    