/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grpc.helloworldtls;

import com.sandeep.grpc.HelloRequest;
import com.sandeep.grpc.HelloResponse;
import com.sandeep.grpc.HelloServiceGrpc;
import grpc.server.GreetingInterceptor;
import grpc.server.HelloServiceImpl;
import io.grpc.Grpc;
import io.grpc.Server;
import io.grpc.ServerCredentials;
import io.grpc.TlsServerCredentials;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server with TLS enabled.
 */
public class HelloWorldServerTls {
    private static final Logger logger = Logger.getLogger(HelloWorldServerTls.class.getName());

    private Server server;

    private final int port;
    private final ServerCredentials creds;

    public HelloWorldServerTls(int port, ServerCredentials creds) {
        this.port = port;
        this.creds = creds;
    }

    private void start() throws IOException {
        server = Grpc.newServerBuilderForPort(port, creds)
                .addService(new HelloServiceImpl())
                .intercept(new GreetingInterceptor())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                HelloWorldServerTls.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {

//        if (args.length < 3 || args.length > 4) {
//            System.out.println(
//                    "USAGE: HelloWorldServerTls port certChainFilePath privateKeyFilePath " +
//                    "[trustCertCollectionFilePath]\n  Note: You only need to supply trustCertCollectionFilePath if you want " +
//                    "to enable Mutual TLS.");
//            System.exit(0);
//        }

        // If only providing a private key, you can use TlsServerCredentials.create() instead of
        // interacting with the Builder.
        TlsServerCredentials.Builder tlsBuilder = TlsServerCredentials.newBuilder()
            .keyManager(new File("/Users/sperumalla/Documents/Learning/mtls/server.crt"), new File("/Users/sperumalla/Documents/Learning/mtls/server.key2"));
        tlsBuilder.trustManager(new File("/Users/sperumalla/Documents/Learning/mtls/ca.crt"));
        tlsBuilder.clientAuth(TlsServerCredentials.ClientAuth.REQUIRE);
//        if (args.length == 4) {
//            tlsBuilder.trustManager(new File(args[3]));
//            tlsBuilder.clientAuth(TlsServerCredentials.ClientAuth.REQUIRE);
//        }
        final HelloWorldServerTls server = new HelloWorldServerTls(
                8080, tlsBuilder.build());
        server.start();
        server.blockUntilShutdown();
    }

    static class GreeterImpl extends HelloServiceGrpc.HelloServiceImplBase {

        @Override
        public void hello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
            HelloResponse response = HelloResponse.newBuilder().setGreeting("Hello  " + req.getFirstName() + req.getLastName()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}