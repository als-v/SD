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
            ResultSet resultadoQuery = statement.executeQuery("SELECT * FROM aluno WHERE ra = " + String.valueOf(ra) + ");");
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

            // matricula
            resultadoQuery = statement.executeQuery("SELECT * FROM matricula WHERE (ra_aluno = " + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "+ String.valueOf(ano) +" AND semestre = "+ String.valueOf(semestre) +");");
            if(!resultadoQuery.isBeforeFirst()){
                res.setMessage("Matricula do aluno em " + String.valueOf(ano) + "/" + String.valueOf(semestre) + " inexistente");
                return 1;
            }

            // atualiza a nota
            statement.execute("UPDATE matricula SET nota = " + String.valueOf(nota) + " WHERE (ra_aluno = " + String.valueOf(ra) + " AND cod_disciplina = '" + String.valueOf(codigoDisciplina) + "' AND ano = "+ String.valueOf(ano) +" AND semestre = "+ String.valueOf(semestre) +");");
            res.setMessage("OK");

            return 0;
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
            return 1;
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
                Gerenciamentodenotas.requisicaoResponseNotas.Builder res = Gerenciamentodenotas.requisicaoResponseNotas.newBuilder();
                
                switch (p.getOpt()) {
                    case 1:
                    valueStr = inClient.readLine();
                    sizeBuffer = Integer.valueOf(valueStr);
                    buffer = new byte[sizeBuffer];
                    inClient.read(buffer);
                    
                        /* realiza o unmarshalling */
                        Gerenciamentodenotas.requisicaoNotas requisicaoNotas = Gerenciamentodenotas.requisicaoNotas.parseFrom(buffer);
                        
                        int statusCode = insertMatricula(conn, res, requisicaoNotas);

                        /* Serializa resposta */
                        byte[] msg = res.toString().getBytes();
                        
                        /* Manda tamanho da resposta */
                        String msgSize = String.valueOf(msg.length) + " \n";
                        byte[] size = msgSize.getBytes();
                        outClient.write(size);
                        
                        /* Manda resposta */
                        outClient.write(msg);
                        
                        break;
                    
                    case 2:

                        break;
                }
                
            } //while
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        } //catch

        // try {
        //     if(null != conn){
        //         conn.resultadoQuery();
        //     }
        // } catch (SQLException e) {
        //     System.out.println(e.getMessage());
        // }
    } //main
} //class    