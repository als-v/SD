
import io.grpc.stub.StreamObserver;
import java.sql.*;
import java.net.*;
import java.io.*;

/**
 *
 * @author rodrigo
 */

public class ServiceDatabaseImpl extends ServiceDatabaseGrpc.ServiceDatabaseImplBase {

    @Override
    public void gerenciaNotas(Request request, StreamObserver<Response> responseObserver) {
        System.out.println("Recebido: ");
        Response response = Response.newBuilder()
                .setMessage("FOIIIIIIIIIIII.")
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
