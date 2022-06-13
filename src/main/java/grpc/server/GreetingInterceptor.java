package grpc.server;

import io.grpc.*;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.security.cert.X509Certificate;

public class GreetingInterceptor implements ServerInterceptor {

    public static final Context.Key<Object> TEST_KEY = Context.key("testkey");
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
            Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {

        SSLSession sslSession = serverCall.getAttributes().get(Grpc.TRANSPORT_ATTR_SSL_SESSION);

        String clientName = null;

        try {
            if(sslSession.getPeerCertificates().length > 0 && sslSession.getPeerCertificates()[0] instanceof X509Certificate) {
                X509Certificate clientCertificate = (X509Certificate) sslSession.getPeerCertificates()[0];
                clientName = clientCertificate.getSubjectX500Principal().getName();
            }
        } catch (SSLPeerUnverifiedException e) {
            e.printStackTrace();
            return serverCallHandler.startCall(serverCall, metadata);
        }

        Context context = Context.current().withValue(TEST_KEY, clientName);
        return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
    }
}
