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