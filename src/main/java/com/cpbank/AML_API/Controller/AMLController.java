package com.cpbank.AML_API.Controller;

import com.cpbank.AML_API.constant.AppConstant;
import com.cpbank.AML_API.dto.AmlUpdateRequest;
import com.cpbank.AML_API.dto.AmlUpdateResponse;
import com.cpbank.AML_API.dto.AMLRequest;
import com.cpbank.AML_API.dto.AMLResponse;
import com.cpbank.AML_API.services.AMLService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AMLController {

    private final AMLService amlService;

    @PostMapping("/aml")
    public ResponseEntity<AMLResponse> getRiskLevel(@RequestBody AMLRequest request,
                                                    HttpServletRequest httpRequest) {
        AMLResponse response = amlService.screenCustomer(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/aml-update")
    public ResponseEntity<AmlUpdateResponse> amlUpdate(@RequestBody AmlUpdateRequest request) {
        AmlUpdateResponse response = amlService.updateCustomer(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/aml-check")
    public ResponseEntity<AMLResponse> checkAml(@RequestBody AMLRequest request,
                                                @RequestHeader(value = "Authorization", required = false) String authHeader,
                                                HttpServletRequest httpRequest) {
        AMLResponse response = amlService.checkCustomer(request, authHeader, httpRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("service", AppConstant.SERVICE_NAME_AML);
        return ResponseEntity.ok(health);
    }
}