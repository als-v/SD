import java.sql.*;

public class db {
    /**
     * Método para se conectar ao banco
     * @return
     */
    public static Connection connect() {
        Connection conn = null;

        try {
            // db parameters
            String url = "jdbc:sqlite:./database/gerenciamento_notas.db";

            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            // caso não consiga se conectar
            System.out.println(e.getMessage());
        }

        return conn;
    }

    /**
     * Método para se desconectar do banco
     * @return
     */
    public static void disconnect(Connection conn) {
        try {
            if(conn != null){
                conn.close();
                System.out.println("Connection to SQLite has been closed.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
