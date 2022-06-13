package grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(8080)
                .addService(new HelloServiceImpl())
                .intercept(new GreetingInterceptor())
                .build();

        server.start();
        server.awaitTermination();
    }
}
