/*
 ### RPC ###
 # Autores: Juan e Alisson
 # Data de criação:      09/08/2021
 # Data de modificação:  12/08/2021
 # Este Server tem como função criar a conexão que será utilizada para comunicação, possibilitando a comunicação
 com a interface e os métodos remotos pertencentes a ela.  
*/

import io.grpc.ServerBuilder;
import java.io.IOException;

public class Server {
    public static void main(String args[]) {
        io.grpc.Server server = ServerBuilder
            .forPort(7777)
            .addService(new ServiceDatabaseImpl())
            .build();
        
        try {
            
            server.start();
            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro: " + e);
        }

    } // main
} // class