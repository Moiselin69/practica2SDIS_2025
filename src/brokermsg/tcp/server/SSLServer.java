package brokermsg.tcp.server;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.io.FileInputStream;
import java.security.PrivateKey;

public class SSLServer {
    public static SSLContext createSSLContext() throws Exception {
        // Cargar el KeyStore (almacén de claves)
        KeyStore keyStore = KeyStore.getInstance("JKS"); // O el tipo de tu almacén (ej. PKCS12)
        char[] keyStorePassword = "000416".toCharArray();
        try (FileInputStream keyStoreFile = new FileInputStream("src/sdis/config/servidor_keystore.jks")) {
            keyStore.load(keyStoreFile, keyStorePassword);
        }

        // Cargar el KeyManager
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword);

        // Crear y configurar un SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);  // No estamos usando un TrustManager por ahora

        return sslContext;
    }
}