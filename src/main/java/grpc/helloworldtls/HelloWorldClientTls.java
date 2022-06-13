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
import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.TlsChannelCredentials;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class HelloWorldClientTls {
    private static final Logger logger = Logger.getLogger(HelloWorldClientTls.class.getName());

    private final HelloServiceGrpc.HelloServiceBlockingStub blockingStub;

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public HelloWorldClientTls(Channel channel) {
        blockingStub = HelloServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Say hello to server.
     */
    public void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder()
                .setFirstName("s")
                .setLastName("p")
                .build();
        HelloResponse response;
        try {
            response = blockingStub.hello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getGreeting());
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) throws Exception {

//        if (args.length < 2 || args.length == 4 || args.length > 5) {
//            System.out.println("USAGE: HelloWorldClientTls host port [trustCertCollectionFilePath " +
//                    "[clientCertChainFilePath clientPrivateKeyFilePath]]\n  Note: clientCertChainFilePath and " +
//                    "clientPrivateKeyFilePath are only needed if mutual auth is desired.");
//            System.exit(0);
//        }

        // If only defaults are necessary, you can use TlsChannelCredentials.create() instead of
        // interacting with the Builder.
        TlsChannelCredentials.Builder tlsBuilder = TlsChannelCredentials.newBuilder();
        tlsBuilder.trustManager(new File("/Users/sperumalla/Documents/Learning/mtls/ca.crt"));
        tlsBuilder.keyManager(new File("/Users/sperumalla/Documents/Learning/mtls/client.crt"),
                new File("/Users/sperumalla/Documents/Learning/mtls/client.key2"));
//        switch (args.length) {
//            case 5:
//                tlsBuilder.keyManager(new File(args[3]), new File(args[4]));
//                // fallthrough
//            case 3:
//                tlsBuilder.trustManager(new File(args[2]));
//                // fallthrough
//            default:
//        }
        ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 8080, tlsBuilder.build())
                /* Only for using provided test certs. */
                //.overrideAuthority("foo.test.google.fr")
                .build();
        try {
            HelloWorldClientTls client = new HelloWorldClientTls(channel);
            client.greet("test");
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}