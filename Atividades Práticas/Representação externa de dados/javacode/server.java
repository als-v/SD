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

    public static Boolean insertMatricula(Connection conn, int ra, String codigoDiscplina, int ano, int semestre, float nota) {
        String sql = "INSERT INTO matricula(ano, semestre, cod_disciplina, ra_aluno, nota) VALUES(?,?,?,?,?)";  

        try{  
            PreparedStatement insert = conn.prepareStatement(sql);  
            insert.setInt(1, ano);  
            insert.setInt(2, semestre);  
            insert.setString(3, codigoDiscplina);
            insert.setInt(4, ra);  
            insert.setFloat(5, nota);  
            
            insert.executeUpdate();  

            return true;
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
            return false;
        }  

    }
    public static void main(String args[]) {
        Connection conn = connect();

        try {
            int serverPort = 7000; 
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                Socket clientSocket = listenSocket.accept();

                DataInputStream inClient = 
                    new DataInputStream(clientSocket.getInputStream());

                String valueStr = inClient.readLine();
                int sizeBuffer = Integer.valueOf(valueStr);
                byte[] buffer = new byte[sizeBuffer];
                inClient.read(buffer);
                
                /* realiza o unmarshalling */
                Gerenciamentodenotas.requisicaoOpt p = Gerenciamentodenotas.requisicaoOpt.parseFrom(buffer);
                
                /* exibe na tela */
                System.out.println("--\n" + p + "--\n");
                
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

                        if(insertMatricula(conn, ra, codigoDisciplina, ano, semestre, nota)){
                            System.out.println("Tudo certo");
                        } else {
                            System.err.println("Deu ruim");
                        }
                        
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