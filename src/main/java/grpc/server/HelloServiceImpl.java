package grpc.server;

import com.sandeep.grpc.HelloRequest;
import com.sandeep.grpc.HelloResponse;
import com.sandeep.grpc.HelloServiceGrpc;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.stub.StreamObserver;

import static grpc.server.GreetingInterceptor.TEST_KEY;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void hello(
      HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String greeting = new StringBuilder()
          .append("Hello, ")
          .append(request.getFirstName())
          .append(" ")
          .append(request.getLastName())
          .toString();

        String clientName = (String) TEST_KEY.get();
        System.out.println("ClientName : " + clientName);

        System.out.println();
        HelloResponse response = HelloResponse.newBuilder()
          .setGreeting(greeting)
          .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}