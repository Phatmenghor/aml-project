package com.cpbank.AML_API.config;

import com.cpbank.AML_API.constant.AppConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        try {
            // Trust all certificates - for development/testing only
            TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;

            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();

            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    NoopHostnameVerifier.INSTANCE
            );

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            HttpComponentsClientHttpRequestFactory factory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);
            factory.setConnectTimeout(AppConstant.CONNECT_TIMEOUT_MS);
            factory.setReadTimeout(AppConstant.READ_TIMEOUT_MS);

            log.warn("RestTemplate configured to trust all certificates (for testing)");
            return new RestTemplate(factory);

        } catch (Exception e) {
            log.error("Failed to configure SSL for RestTemplate: {}", e.getMessage());
            log.warn("Using RestTemplate without SSL");
            return new RestTemplate();
        }
    }
}