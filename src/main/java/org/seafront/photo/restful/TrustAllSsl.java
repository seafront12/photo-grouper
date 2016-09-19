package org.seafront.photo.restful;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.ClientBuilder;
import java.security.cert.X509Certificate;

import static com.google.common.base.Throwables.propagate;

public class TrustAllSsl {
    public static ClientBuilder trustAllSslClientBuilder() {
        return ClientBuilder.newBuilder().hostnameVerifier(trustAllVerifier()).sslContext(trustAllSSLContext());
    }

    private static SSLContext trustAllSSLContext() {
        TrustManager[] trustAllCerts = trustManagers();
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {
            propagate(e);
        }
        return sc;
    }

    private static TrustManager[] trustManagers() {
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
    }

    private static HostnameVerifier trustAllVerifier() {
        return (s, sslSession) -> true;
    }
}
