package grpc.client;

import com.sandeep.grpc.HelloRequest;
import com.sandeep.grpc.HelloResponse;
import com.sandeep.grpc.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;

public class GrpcClient {
    public static void main(String[] args)  {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        HelloServiceGrpc.HelloServiceBlockingStub stub
                = HelloServiceGrpc.newBlockingStub(channel);

        HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
                .setFirstName("Sandeep")
                .setLastName("Perumalla")
                .build());
        System.out.println("Greeting: " + helloResponse.getGreeting());

        channel.shutdown();
    }
}