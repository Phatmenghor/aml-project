package com.cpbank.AML_API.config;

import com.cpbank.AML_API.constant.AppConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RestTemplateConfig {

    @Value("${ssl.keystore.path:aml-keystore.p12}")
    private Resource keystoreResource;

    @Value("${ssl.keystore.password:P@ssw0rd}")
    private String keystorePassword;

    @Value("${ssl.keystore.type:PKCS12}")
    private String keystoreType;

    @Bean
    public RestTemplate restTemplate() {
        try {
            KeyStore keyStore = loadKeyStore();
            SSLContext sslContext = buildSslContext(keyStore);
            HttpClient httpClient = createHttpClient(sslContext);
            
            HttpComponentsClientHttpRequestFactory factory = createRequestFactory(httpClient);
            
            log.info("RestTemplate configured with SSL");
            return new RestTemplate(factory);

        } catch (Exception e) {
            log.error("Failed to configure SSL for RestTemplate: {}", e.getMessage());
            log.warn("Using RestTemplate without SSL");
            return new RestTemplate();
        }
    }

    private KeyStore loadKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keystoreType);
        keyStore.load(keystoreResource.getInputStream(), keystorePassword.toCharArray());
        return keyStore;
    }

    private SSLContext buildSslContext(KeyStore keyStore) throws Exception {
        return SSLContextBuilder.create()
                .loadKeyMaterial(keyStore, keystorePassword.toCharArray())
                .loadTrustMaterial(keyStore, null)
                .build();
    }

    private HttpClient createHttpClient(SSLContext sslContext) {
        return HttpClients.custom()
                .setSSLContext(sslContext)
                .build();
    }

    private HttpComponentsClientHttpRequestFactory createRequestFactory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(AppConstant.CONNECT_TIMEOUT_MS);
        factory.setReadTimeout(AppConstant.READ_TIMEOUT_MS);
        return factory;
    }
}
