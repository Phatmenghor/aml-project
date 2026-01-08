package com.cpbank.AML_API.services;

import com.cpbank.AML_API.dto.AmlUpdateRequest;
import com.cpbank.AML_API.constant.AppConstant;
import com.cpbank.AML_API.dto.AmlUpdateResponse;
import com.cpbank.AML_API.dto.AmlUpdateResult;
import com.cpbank.AML_API.dto.AMLRequest;
import com.cpbank.AML_API.dto.AMLResponse;
import com.cpbank.AML_API.dto.OaoDownstreamRequest;
import com.cpbank.AML_API.dto.LosDownstreamRequest;
import com.cpbank.AML_API.helper.XmlBuilderHelper;
import com.cpbank.AML_API.helper.XmlParserHelper;
import com.cpbank.AML_API.models.AmlLog;
import com.cpbank.AML_API.repository.AmlLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
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
import java.util.stream.Collectors;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AMLService {
    private final RestTemplate restTemplate;
    private final AmlLogRepository amlLogRepository;
    private final XmlBuilderHelper xmlBuilderHelper;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
            String json = objectMapper.writeValueAsString(request);
            log.info("AML original Request : {}", json);

            HttpHeaders headers = createHeaders(bearerToken);
            HttpEntity<AMLRequest> entity = new HttpEntity<>(request,headers);

            String res_str = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class)
                    .getBody();

            response = objectMapper.readValue(res_str, AMLResponse.class);
            log.info("AML original Response : {}", res_str);
            log.info("Middle Response Mapping : {} ", response);
            return response;
        }catch (Exception e){
            log.info("error exception : {} ", e);
            System.out.println(e.getMessage());
            return null;
        }
    }

    public AmlUpdateResponse PutCustomer(AmlUpdateRequest request) {
        AmlLog amlLog = initLog(request);
        AmlUpdateResponse response = new AmlUpdateResponse();

        try {
            // 1. Call SOAP Service (Soup API) - Always happens
            String soapResponse = sendToSoapService(request);
            Map<String, Object> rawMap = XmlParserHelper.parseSoapXml(soapResponse);
            
            // Map to DTO
            AmlUpdateResult resultDto = mapToAmlUpdateResult(rawMap);
            response.setResult(resultDto);

            // 2. Determine App Type
            String appType = determineAppType(request.getCustomerId());
            response.setAppType(appType);

            // 3. Process Downstream (if not T24)
            processDownstreamLogic(appType, request, response, amlLog, soapResponse);

            amlLog.setStatus(AppConstant.STATUS_SUCCESS);
        } catch (Exception e) {
            amlLog.setStatus("FAILED");
            amlLog.setResponseJson("Error: " + e.getMessage());
            log.error("Error processing AML update", e);
            throw e; 
        } finally {
            saveLog(amlLog, response);
        }

        return response;
    }

    private String determineAppType(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return AppConstant.APP_TYPE_T24; // Default fallback
        }
        
        String upperId = customerId.toUpperCase().trim();
        
        if (upperId.startsWith(AppConstant.APP_TYPE_OAO)) {
            return AppConstant.APP_TYPE_OAO;
        }
        if (upperId.startsWith("C")) {
            return AppConstant.APP_TYPE_LOS;
        }
        // Covers numeric IDs (e.g., 9027533223) and any unformatted IDs
        return AppConstant.APP_TYPE_T24; 
    }

    private String sendToSoapService(AmlUpdateRequest request) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(amlSoapUrl);
            httpPost.setEntity(new StringEntity(xmlBuilderHelper.constructSoapPayload(request), ContentType.TEXT_XML));

            return httpClient.execute(httpPost, response -> {
                return new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                        .lines().collect(Collectors.joining("\n"));
            });
        } catch (Exception e) {
            return "Error calling SOAP: " + e.getMessage();
        }
    }

    private String getDownstreamUrl(String appType) {
        if (AppConstant.APP_TYPE_OAO.equals(appType)) {
            return oaoUrl;
        }
        return losUrl;
    }

    private String executeDownstreamCall(String url, AmlUpdateRequest request) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            String jsonBody = buildDownstreamJsonBody(url, request);

            isAccountOnline(url, httpPost);

            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            return httpClient.execute(httpPost, response -> {
                return new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                        .lines().collect(Collectors.joining("\n"));
            });
        } catch (Exception e) {
            return "Error calling downstream: " + e.getMessage();
        }
    }

    private void isAccountOnline(String url, HttpPost httpPost) {
        if (isAccountOnlineUrl(url)) {
            if (oaoApiKey == null || oaoApiKey.trim().isEmpty() || oaoSecretKey == null || oaoSecretKey.trim().isEmpty()) {
                throw new IllegalStateException("OAO Configuration Error: Missing API Key or Secret Key");
            }
            httpPost.addHeader(AppConstant.HEADER_X_API_KEY, oaoApiKey);
            httpPost.addHeader(AppConstant.HEADER_X_SECRET_KEY, oaoSecretKey);
        }
    }

    private String buildDownstreamJsonBody(String url, AmlUpdateRequest request) {
        try {
            if (isAccountOnlineUrl(url)) {
                OaoDownstreamRequest oaoRequest = new OaoDownstreamRequest(request.getCustomerId(), "aml-springboot");
                return objectMapper.writeValueAsString(oaoRequest);
            } else {
                LosDownstreamRequest losRequest = new LosDownstreamRequest(request.getCustomerId(), "Processed");
                return objectMapper.writeValueAsString(losRequest);
            }
        } catch (Exception e) {
            log.error("Error building downstream JSON body", e);
            return "{}"; // Should ideally throw or handle upstream
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

    private AmlLog initLog(AmlUpdateRequest request) {
        AmlLog amlLog = new AmlLog();
        try {
            amlLog.setRequestJson(objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            log.error("Error standardizing request json", e);
        }
        return amlLog;
    }

    private void processDownstreamLogic(String appType, AmlUpdateRequest request, AmlUpdateResponse response, AmlLog amlLog, String soapResponse) {
        if (AppConstant.APP_TYPE_T24.equals(appType)) {
             response.setStatus(AppConstant.STATUS_SUCCESS);
             response.setMessage("Processed successfully via SOAP (T24).");
             response.setAppResponse(AppConstant.STATUS_SUCCESS);
        } else {
             // OAO or LOS - Call Downstream
             String downstreamUrl = getDownstreamUrl(appType);
             String downstreamResp = executeDownstreamCall(downstreamUrl, request); 
             
             response.setStatus(AppConstant.STATUS_SUCCESS);
             response.setMessage("Processed successfully.");
             response.setAppResponse(AppConstant.STATUS_SUCCESS); 
             
             amlLog.setResponseJson("Downstream: " + downstreamResp + " | SOAP: " + soapResponse);
        }
    }

    private void saveLog(AmlLog amlLog, AmlUpdateResponse response) {
        try {
            if (amlLog.getResponseJson() == null) amlLog.setResponseJson(objectMapper.writeValueAsString(response));
            amlLogRepository.save(amlLog);
        } catch (Exception e) {
            log.error("Error saving log", e);
        }
    }

    private AmlUpdateResult mapToAmlUpdateResult(Map<String, Object> map) {
       AmlUpdateResult result = new AmlUpdateResult();
        
        try {
            // Traverse to find TUWSAMLSRVHNDLType
            // The structure is typically Envelope -> Body -> WSAMLUPDResponse -> TUWSAMLSRVHNDLType
            // But XmlParserHelper might have unwrapped Envelope/Body if they were single children
            
            Map<String, Object> targetMap = map;

            // Navigate down if needed
            if (targetMap.containsKey("WSAMLUPDResponse")) {
                 Object val = targetMap.get("WSAMLUPDResponse");
                 if(val instanceof Map) targetMap = (Map<String, Object>) val;
            }
            
            if (targetMap.containsKey("TUWSAMLSRVHNDLType")) {
                 Object val = targetMap.get("TUWSAMLSRVHNDLType");
                 if(val instanceof Map) targetMap = (Map<String, Object>) val;
            }

            // Map fields
            if (targetMap.containsKey("id")) result.setId((String) targetMap.get("id"));
            if (targetMap.containsKey("CUSTID")) result.setCUSTID((String) targetMap.get("CUSTID"));
            if (targetMap.containsKey("SRVNAME")) result.setSRVNAME((String) targetMap.get("SRVNAME"));
            if (targetMap.containsKey("RISKLVL")) result.setRISKLVL((String) targetMap.get("RISKLVL"));
            if (targetMap.containsKey("ACTNTAKN")) result.setACTNTAKN((String) targetMap.get("ACTNTAKN"));
            
            if (targetMap.containsKey("gRULETRG")) {
                Object grule = targetMap.get("gRULETRG");
                if (grule instanceof Map) {
                    Map<String, Object> gMap = (Map<String, Object>) grule;
                    com.cpbank.AML_API.dto.AmlUpdateResult.GRULETRG gObj = new com.cpbank.AML_API.dto.AmlUpdateResult.GRULETRG();
                    if (gMap.containsKey("RULETRG")) {
                        Object ruleContent = gMap.get("RULETRG");
                        if (ruleContent instanceof String) {
                            gObj.setRULETRG(java.util.Collections.singletonList((String) ruleContent));
                        } else if (ruleContent instanceof java.util.List) {
                            gObj.setRULETRG((java.util.List<String>) ruleContent);
                        }
                    }
                    result.setGRULETRG(gObj);
                }
            }

            if (targetMap.containsKey("TXNID")) result.setTXNID((String) targetMap.get("TXNID"));
            if (targetMap.containsKey("TOTRSCR")) result.setTOTRSCR((String) targetMap.get("TOTRSCR"));
            if (targetMap.containsKey("RETCODE")) result.setRETCODE((String) targetMap.get("RETCODE"));
            if (targetMap.containsKey("RETDESC")) result.setRETDESC((String) targetMap.get("RETDESC"));

        } catch (Exception e) {
            log.error("Error mapping XML to DTO", e);
        }
        
        return result;
    }
}
