package com.cpbank.AML_API.services;

import com.cpbank.AML_API.dto.AMLRequest;
import com.cpbank.AML_API.dto.AMLResponse;
import com.cpbank.AML_API.helper.ReadJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
public class CheckAmlService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ReadJson readJson;
    @Value("${aml.org.url}")
    private String url;

    @Value("${aml.org.token}")
    private String bearerToken;
    Logger logger = LoggerFactory.getLogger(CheckAmlService.class);

    public AMLResponse PostCustomer(AMLRequest request, String bearerToken) throws IOException {
        AMLResponse response = new AMLResponse();
        try {
//            ReadJson hah=new ReadJson();
            String trxnId = ZonedDateTime.now(ZoneId.of("Asia/Phnom_Penh"))
                    .format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS"));
            System.out.println("b4 check");
            boolean isBypass = readJson.readByPassAml();
            System.out.println("after  check");
            if (isBypass) {
                logger.info("Bypass AML check is enabled. Returning mock response...");

                response.setServiceName("Intuition");
                response.setTrxnID(trxnId);
                response.setActionTaken(null);
                response.setTotalRulesScore(0);
                response.setRiskLevel("Low");
                response.setRulesTriggered(Collections.emptyList());

                return response;
            }


            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(request);

            HttpHeaders headers = createHeaders(bearerToken);
            HttpEntity<AMLRequest> entity = new HttpEntity<>(request, headers);

            String res_str = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            ).getBody();

            response = objectMapper.readValue(res_str, AMLResponse.class);

            logger.info("AML original Response : {}", res_str);
            logger.info("Middle Response Mapping : {}", response);

        } catch (Exception e) {
            logger.error("Error calling AML API: {}", e.getMessage(), e);
        }

        return response;
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
