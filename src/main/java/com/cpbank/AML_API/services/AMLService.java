package com.cpbank.AML_API.services;

import com.cpbank.AML_API.model.AmlUpdateRequest;
import com.cpbank.AML_API.model.AmlUpdateResponse;
import com.cpbank.AML_API.model.AMLRequest;
import com.cpbank.AML_API.model.AMLResponse;
import com.cpbank.AML_API.helper.XmlBuilderHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
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
import java.util.stream.Collectors;

@Service
public class AMLService {
    Logger logger = LoggerFactory.getLogger(AMLService.class);
    @Autowired
    private RestTemplate restTemplate;

    @Value("${aml.org.url}")
    private String url;

    @Value("${aml.org.token}")
    private String bearerToken;

    @Value("${aml.soap.url}")
    private String amlSoapUrl;

    @Value("${aml.downstream.oao.url}")
    private String oaoUrl;

    @Value("${aml.downstream.los.url}")
    private String losUrl;

    @Value("${aml.downstream.oao.api-key}")
    private String oaoApiKey;

    @Value("${aml.downstream.oao.secret-key}")
    private String oaoSecretKey;

    public AMLResponse PostCustomer(AMLRequest request) throws IOException,NullPointerException {
        AMLResponse response = new AMLResponse();
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(request);
            logger.info("AML original Request : {}", json);



            HttpHeaders headers = createHeaders(bearerToken);



            HttpEntity<AMLRequest> entity = new HttpEntity<>(request,headers);




            String res_str = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class)
                    .getBody();


            response = objectMapper.readValue(res_str, AMLResponse.class);
            logger.info("AML original Response : {}", res_str);
            logger.info("Middle Response Mapping : {} ", response);
            return response;
        }catch (Exception e){
            logger.info("error exception : {} ", e);

            System.out.println(e.getMessage());

            return null;
        }

    }

    public AmlUpdateResponse processAmlUpdate(AmlUpdateRequest request) {
        AmlUpdateResponse response = new AmlUpdateResponse();

        if (!sendToSoapService(request)) {
            response.setStatus("FAILED");
            response.setMessage("Failed to update Core Banking via SOAP.");
            return response;
        }

        String downstreamUrl = getDownstreamUrl(request.getCustomerId());
        String downstreamResp = executeDownstreamCall(downstreamUrl, request);

        response.setStatus("SUCCESS");
        response.setMessage("Processed successfully.");
        response.setDownstreamApp(isAccountOnlineUrl(downstreamUrl) ? "OAO/AccountOnline" : "LOS");
        response.setDownstreamResponse(downstreamResp);

        return response;
    }

    private boolean sendToSoapService(AmlUpdateRequest request) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(amlSoapUrl);
            httpPost.setEntity(new StringEntity(XmlBuilderHelper.constructSoapPayload(request), ContentType.TEXT_XML));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getCode() == 200) {
                    String responseBody = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                            .lines().collect(Collectors.joining("\n"));
                    return responseBody.contains("<successIndicator>Success</successIndicator>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    private String getDownstreamUrl(String customerId) {
        if (customerId != null && customerId.startsWith("OAO")) {
            return oaoUrl;
        }
        return losUrl;
    }

    private String executeDownstreamCall(String url, AmlUpdateRequest request) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            String jsonBody = buildDownstreamJsonBody(url, request);

            if (isAccountOnlineUrl(url)) {
                httpPost.addHeader("X-API-KEY", oaoApiKey);
                httpPost.addHeader("X-SECRET-KEY", oaoSecretKey);
            }

            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                return new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                        .lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            return "Error calling downstream: " + e.getMessage();
        }
    }

    private String buildDownstreamJsonBody(String url, AmlUpdateRequest request) {
        if (isAccountOnlineUrl(url)) {
            return String.format("{\"oao\": \"%s\", \"updateFrom\": \"AML\"}", request.getCustomerId());
        } else {
            return String.format("{\"customerId\": \"%s\", \"status\": \"Processed\"}", request.getCustomerId());
        }
    }

    private boolean isAccountOnlineUrl(String url) {
        return url.equals(oaoUrl);
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
