package com.cpbank.AML_API.services;

import com.cpbank.AML_API.models.AMLRequest;
import com.cpbank.AML_API.models.AMLResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class AMLService {
    Logger logger = LoggerFactory.getLogger(AMLService.class);
    @Autowired
    private RestTemplate restTemplate;

    @Value("${aml.org.url}")
    private String url;

    @Value("${aml.org.token}")
    private String bearerToken;

    public AMLResponse PostCustomer(AMLRequest request) throws IOException,NullPointerException {
        AMLResponse response = new AMLResponse();
        try {
            logger.info("Hi 01");
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(request);
            logger.info("AML original Request : {}", json);

            logger.info("Hi 02");

            HttpHeaders headers = createHeaders(bearerToken);

            logger.info("Hi 03");

            HttpEntity<AMLRequest> entity = new HttpEntity<>(request,headers);

            logger.info("Hi 04");


            String res_str = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class)
                    .getBody();

            logger.info("Hi 05");
            response = objectMapper.readValue(res_str, AMLResponse.class);
            logger.info("AML original Response : {}", res_str);
            logger.info("Middle Response Mapping : {} ", response);
            return response;
        }catch (Exception e){
            logger.info("Hourng 02 : {} ", e);

            System.out.println(e.getMessage());

            return null;
        }

    }
    private HttpHeaders createHeaders(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        if (bearerToken != null && !bearerToken.trim().isEmpty()) {
            headers.set("Authorization", "Bearer " + bearerToken);
        }

        return headers;
    }
}
