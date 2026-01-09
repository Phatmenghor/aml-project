package com.cpbank.AML_API.exception;

import com.cpbank.AML_API.constant.AppConstant;
import com.cpbank.AML_API.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("Validation error: {} for path: {}", e.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseUtils.buildUnauthorizedResponse(e.getMessage(), request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        log.warn("Access denied for: {}", request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ResponseUtils.buildErrorResponse(HttpStatus.FORBIDDEN, AppConstant.ERROR_ACCESS_DENIED, request));
    }

    @ExceptionHandler(AmlApiException.class)
    public ResponseEntity<Map<String, Object>> handleAmlApiException(AmlApiException e, HttpServletRequest request) {
        log.error("AML API error: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ResponseUtils.buildDetailedErrorResponse(
                    HttpStatus.BAD_GATEWAY, 
                    "AML Service Error", 
                    e.getMessage(),
                    "Please check your network connection or contact support if the issue persists.",
                    request
                ));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAccessException(ResourceAccessException e, HttpServletRequest request) {
        log.error("Connection error: {}", e.getMessage(), e);
        
        String userMessage;
        String suggestion;
        
        if (e.getMessage().contains("SSL") || e.getMessage().contains("certificate")) {
            userMessage = "SSL Certificate Error: Unable to establish secure connection to AML service";
            suggestion = "The SSL certificate is not trusted. Please contact IT support to add the certificate to the keystore.";
        } else if (e.getMessage().contains("Connection refused")) {
            userMessage = "Connection Refused: AML service is not reachable";
            suggestion = "The AML service may be down. Please check if the service is running or contact support.";
        } else if (e.getMessage().contains("timeout")) {
            userMessage = "Connection Timeout: AML service did not respond in time";
            suggestion = "The service is taking too long to respond. Please try again later.";
        } else {
            userMessage = "Network Error: Unable to connect to AML service";
            suggestion = "Please check your network connection and try again.";
        }
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ResponseUtils.buildDetailedErrorResponse(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    userMessage,
                    extractRootCause(e),
                    suggestion,
                    request
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        
        String userMessage = "An unexpected error occurred";
        String details = e.getMessage();
        String suggestion = "Please try again. If the problem persists, contact support.";
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtils.buildDetailedErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    userMessage,
                    details,
                    suggestion,
                    request
                ));
    }

    private String extractRootCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause.getMessage();
    }
}