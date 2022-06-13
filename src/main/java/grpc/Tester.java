package grpc;

import io.netty.handler.ssl.PemX509Certificate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class Tester {
    public static void main(String[] args) throws Exception
    {

        // File path is passed as parameter
        File file = new File(
                "/Users/sperumalla/Documents/Learning/mtls/caasserver.pem");

        // Note:  Double backquote is to avoid compiler
        // interpret words
        // like \test as \t (ie. as a escape sequence)

        // Creating an object of BufferedReader class
        BufferedReader br
                = new BufferedReader(new FileReader(file));

        // Declaring a string variable
        String st;
        StringBuilder certificate = new StringBuilder();
        // Condition holds true till
        // there is character in a string
        while ((st = br.readLine()) != null) {
            certificate.append(st);
            certificate.append("\n");
        }

        System.out.println(certificate.toString());
        byte[] certBytes = certificate.toString().getBytes(StandardCharsets.UTF_8);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        InputStream in = new ByteArrayInputStream(certBytes);
        X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);

        System.out.println(cert);


    }

}
