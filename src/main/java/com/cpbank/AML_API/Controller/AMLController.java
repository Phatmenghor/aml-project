package com.cpbank.AML_API.Controller;

import com.cpbank.AML_API.models.AMLRequest;
import com.cpbank.AML_API.models.AMLResponse;
import com.cpbank.AML_API.services.AMLService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class AMLController {

    @Autowired
    private AMLService amlService;

    @PostMapping("/aml")
    public ResponseEntity<AMLResponse> getRiskLevel(@RequestBody AMLRequest request,
                                                    HttpServletRequest httpRequest) {
        try {
            // Log authentication info
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("AML API accessed by user: {}", auth.getName());
            log.info("Request received from IP: {}", getClientIpAddress(httpRequest));

            AMLResponse response = amlService.PostCustomer(request);
            log.info("AML response generated successfully for customer: {}", request.getCUSTOMER_ID());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing AML request for customer: {}, Error: {}",
                    request.getCUSTOMER_ID(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("service", "AML API");
        return ResponseEntity.ok(health);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception occurred: {}", e.getMessage(), e);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred while processing your request");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("method", request.getMethod());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e,
                                                                  HttpServletRequest request) {
        log.warn("Access denied for request to: {}", request.getRequestURI());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", "Access denied: You don't have permission to access this resource");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("method", request.getMethod());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException e,
                                                                HttpServletRequest request) {
        log.warn("Bad request: {}", e.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("method", request.getMethod());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}