package com.cpbank.AML_API.config;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    @Value("${ssl.keystore.path:classpath:aml-keystore.p12}")
    private Resource keystoreResource;

    @Value("${ssl.keystore.password:123456}")
    private String keystorePassword;

    @Value("${ssl.keystore.type:PKCS12}")
    private String keystoreType;

    @Bean
    public RestTemplate restTemplate() {
        try {
            // Load keystore (for client certificates)
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            try (InputStream keystoreStream = keystoreResource.getInputStream()) {
                keyStore.load(keystoreStream, keystorePassword.toCharArray());
            }

            // Load truststore (for trusting external servers)
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (InputStream trustStoreStream = getClass().getClassLoader()
                    .getResourceAsStream("custom-truststore.jks")) {

                if (trustStoreStream == null) {
                    throw new RuntimeException("custom-truststore.jks not found in classpath");
                }
                trustStore.load(trustStoreStream, "123456".toCharArray());
            }

            // Build SSL context with both keystore and truststore
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore, keystorePassword.toCharArray())
                    .loadTrustMaterial(trustStore, null)
                    .build();

            HttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .build();

            HttpComponentsClientHttpRequestFactory factory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);
            factory.setConnectTimeout(30000); // 30 seconds
            factory.setReadTimeout(60000);    // 60 seconds

            System.out.println("? SSL RestTemplate configured successfully!");
            return new RestTemplate(factory);

        } catch (Exception e) {
            System.err.println("? Failed to configure SSL for RestTemplate: " + e.getMessage());
            e.printStackTrace();
            System.out.println("?? Falling back to default RestTemplate (no SSL)");
            return new RestTemplate();
        }
    }
}