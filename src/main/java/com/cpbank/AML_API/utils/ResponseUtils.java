package com.cpbank.AML_API.utils;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ResponseUtils {

    public static Map<String, Object> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        error.put("path", request.getRequestURI());
        error.put("method", request.getMethod());
        return error;
    }

    public static Map<String, Object> buildDetailedErrorResponse(HttpStatus status, String userMessage, 
                                                                 String details, String suggestion, 
                                                                 HttpServletRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", userMessage);
        error.put("details", details);
        error.put("suggestion", suggestion);
        error.put("path", request.getRequestURI());
        error.put("method", request.getMethod());
        return error;
    }

    public static Map<String, Object> buildUnauthorizedResponse(String message, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication failed: " + message, request);
    }

    public static Map<String, Object> buildInternalErrorResponse(String message, HttpServletRequest request) {
        return buildDetailedErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            message,
            "Please try again or contact support if the issue persists.",
            request
        );
    }

    private ResponseUtils() {}
}