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
import java.security.KeyStore;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {


    @Value("${ssl.keystore.path:amluat-keystore.p12}")
    private Resource keystoreResource;

    @Value("${ssl.keystore.password:P@ssw0rd}")
    private String keystorePassword;

    @Value("${ssl.keystore.type:PKCS12}")
    private String keystoreType;

    @Bean
    public RestTemplate restTemplate() {
        try {
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(keystoreResource.getInputStream(), keystorePassword.toCharArray());
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore, keystorePassword.toCharArray())
                    .loadTrustMaterial(keyStore, null)
                    .build();
            HttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .build();

            HttpComponentsClientHttpRequestFactory factory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);
            factory.setConnectTimeout(30000); // 30 seconds
            factory.setReadTimeout(60000);    // 60 seconds

            return new RestTemplate(factory);

        } catch (Exception e) {
            System.err.println("Failed to configure SSL for RestTemplate: " + e.getMessage());
            return new RestTemplate();
        }
    }
}
