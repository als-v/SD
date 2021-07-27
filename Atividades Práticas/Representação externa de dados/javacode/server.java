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
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        return conn;
    }
    
    public static void insertMatricula(Connection conn, Gerenciamentodenotas.requisicaoResponseNotas.Builder res, int ra, String codigoDiscplina, int ano, int semestre, float nota) {
        String sql = "INSERT INTO matricula(ano, semestre, cod_disciplina, ra_aluno, nota) VALUES(?,?,?,?,?)";  

        try{  
            Statement statement = conn.createStatement();
            PreparedStatement insert = conn.prepareStatement(sql);  

            ResultSet resultSet = statement.executeQuery("SELECT * FROM aluno WHERE (ra = " + String.valueOf(ra) + ");");
            if(!resultSet.isBeforeFirst()){
                res.setMessage("RA não encontrado");
            }

            insert.setInt(1, ano);  
            insert.setInt(2, semestre);  
            insert.setString(3, codigoDiscplina);
            insert.setInt(4, ra);  
            insert.setFloat(5, nota);  
            
            insert.executeUpdate();  

        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
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
                        
                        int ra = requisicaoNotas.getRa();
                        String codigoDisciplina = requisicaoNotas.getCodDisciplina();
                        int ano = requisicaoNotas.getAno();
                        int semestre = requisicaoNotas.getSemestre();
                        float nota = requisicaoNotas.getNota();

                        insertMatricula(conn, res, ra, codigoDisciplina, ano, semestre, nota);

                        /* Serializa resposta */
                        byte[] msg = res.build().toByteArray();
                        
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

        try {
            if(null != conn){
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } //main
} //class    