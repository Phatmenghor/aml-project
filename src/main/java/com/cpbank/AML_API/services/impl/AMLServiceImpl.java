package com.cpbank.AML_API.services.impl;

import com.cpbank.AML_API.dto.*;
import com.cpbank.AML_API.exception.AmlApiException;
import com.cpbank.AML_API.mapper.AmlResponseMapper;
import com.cpbank.AML_API.models.AmlLog;
import com.cpbank.AML_API.repository.AmlLogRepository;
import com.cpbank.AML_API.services.AMLService;
import com.cpbank.AML_API.utils.ConfigUtils;
import com.cpbank.AML_API.utils.DateTimeUtils;
import com.cpbank.AML_API.utils.HttpUtils;
import com.cpbank.AML_API.utils.XmlUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AMLServiceImpl implements AMLService {

    private final RestTemplate restTemplate;
    private final AmlLogRepository amlLogRepository;
    private final ObjectMapper objectMapper;
    private final ConfigUtils configUtils;

    @Value("${aml.org.url}") private String amlApiUrl;
    @Value("${aml.org.token}") private String bearerToken;
    @Value("${aml.soap.url}") private String soapServiceUrl;
    @Value("${aml.downstream.oao.url}") private String oaoDownstreamUrl;
    @Value("${aml.downstream.los.url}") private String losDownstreamUrl;
    @Value("${aml.downstream.oao.api-key}") private String oaoApiKey;
    @Value("${aml.downstream.oao.secret-key}") private String oaoSecretKey;
    @Value("${aml.company}") private String company;
    @Value("${aml.username}") private String username;
    @Value("${aml.password}") private String password;

    @Override
    public AMLResponse screenCustomer(AMLRequest request, HttpServletRequest httpRequest) {
        // Check bypass
        if (configUtils.getByPassAml()) {
            log.info("Bypassed enabled for customer: {}", request.getCUSTOMER_ID());
            return createBypassResponse("BYPASS-AML");
        }

        // Call AML API
        log.info("Calling Aml API for customer: {}", request.getCUSTOMER_ID());
        HttpHeaders headers = HttpUtils.createAuthHeaders(bearerToken);
        HttpEntity<AMLRequest> entity = new HttpEntity<>(request, headers);
        String responseBody = restTemplate.exchange(amlApiUrl, HttpMethod.POST, entity, String.class).getBody();
        
        return parseJson(responseBody, AMLResponse.class);
    }

    @Override
    public AMLResponse checkCustomer(AMLRequest request, String authHeader, HttpServletRequest httpRequest) {
        // Validate token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing Bearer token");
        }
        String token = authHeader.substring(7);
        if (!token.equals(bearerToken)) {
            throw new IllegalArgumentException("Invalid Bearer token");
        }

        // Check bypass
        if (configUtils.getByPassAmlCheck()) {
            log.info("Bypass enabled for customer: {}", request.getCUSTOMER_ID());
            return createBypassResponse(DateTimeUtils.generateTransactionId());
        }

        // Call AML API
        log.info("Calling AML API for customer: {}", request.getCUSTOMER_ID());
        HttpHeaders headers = HttpUtils.createAuthHeaders(token);
        HttpEntity<AMLRequest> entity = new HttpEntity<>(request, headers);
        String responseBody = restTemplate.exchange(amlApiUrl, HttpMethod.POST, entity, String.class).getBody();
        
        return parseJson(responseBody, AMLResponse.class);
    }

    @Override
    public AmlUpdateResponse updateCustomer(AmlUpdateRequest request) {
        AmlLog amlLog = new AmlLog();
        amlLog.setRequestJson(toJsonString(request));
        AmlUpdateResponse response = new AmlUpdateResponse();

        // Step 1: Call SOAP
        String soapResponse = callSoap(request);
        Map<String, Object> parsedXml = XmlUtils.parseSoapXml(soapResponse);
        response.setResult(AmlResponseMapper.mapToAmlUpdateResult(parsedXml));

        // Step 2: Determine app type
        String appType = getAppType(request.getCustomerId());
        response.setAppType(appType);

        // Step 3: Process by app type
        if ("T24".equals(appType)) {
            response.setStatus("Success");
            response.setMessage("Updated via T24");
            response.setAppResponse("Success");
        } else {
            String url = "OAO".equals(appType) ? oaoDownstreamUrl : losDownstreamUrl;
            String downstreamResponse = callDownstreamApi(url, request, appType);
            
            response.setStatus("Success");
            response.setMessage("Updated successfully");
            response.setAppResponse("Success");
            amlLog.setResponseJson("Downstream: " + downstreamResponse + " | SOAP: " + soapResponse);
        }

        // Step 4: Save log
        amlLog.setStatus("Success");
        saveLog(amlLog, response);

        return response;
    }

    // Simple helper methods
    
    private AMLResponse createBypassResponse(String txnId) {
        AMLResponse response = new AMLResponse();
        response.setServiceName("Intuition");
        response.setTrxnID(txnId);
        response.setActionTaken(null);
        response.setTotalRulesScore(0);
        response.setRiskLevel("Low");
        response.setRulesTriggered(Collections.emptyList());
        return response;
    }

    private String callSoap(AmlUpdateRequest request) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(soapServiceUrl);
            String payload = XmlUtils.buildSoapPayload(request, company, username, password);
            post.setEntity(new StringEntity(payload, ContentType.TEXT_XML));
            
            return client.execute(post, response ->
                new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                    .lines().collect(Collectors.joining("\n"))
            );
        } catch (Exception e) {
            log.error("SOAP failed: {}", e.getMessage());
            throw new AmlApiException("SOAP call failed", e);
        }
    }

    private String callDownstreamApi(String url, AmlUpdateRequest request, String appType) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            
            if ("OAO".equals(appType)) {
                HttpUtils.addOaoHeaders(post, oaoApiKey, oaoSecretKey);
            }
            
            String json = "OAO".equals(appType)
                ? toJsonString(new OaoDownstreamRequest(request.getCustomerId(), "UpdateFromSpringboot"))
                : toJsonString(new LosDownstreamRequest(request.getCustomerId(), "Processed"));
            
            post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            
            return client.execute(post, response ->
                new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                    .lines().collect(Collectors.joining("\n"))
            );
        } catch (Exception e) {
            log.error("Downstream failed: {}", e.getMessage());
            throw new AmlApiException("Downstream call failed", e);
        }
    }

    private String getAppType(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) return "T24";
        String id = customerId.toUpperCase().trim();
        if (id.startsWith("OAO")) return "OAO";
        if (id.startsWith("LOS")) return "LOS";
        return "T24";
    }

    private void saveLog(AmlLog amlLog, AmlUpdateResponse response) {
        try {
            if (amlLog.getResponseJson() == null) {
                amlLog.setResponseJson(toJsonString(response));
            }
            amlLogRepository.save(amlLog);
        } catch (Exception e) {
            log.error("Log save failed: {}", e.getMessage(), e);
        }
    }

    private <T> T parseJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.error("JSON parse failed: {}", e.getMessage());
            throw new AmlApiException("Invalid JSON format", e);
        }
    }

    private String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON stringify failed: {}", e.getMessage());
            return "{}";
        }
    }
}