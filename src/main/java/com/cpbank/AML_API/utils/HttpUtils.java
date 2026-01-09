package com.cpbank.AML_API.utils;

import com.cpbank.AML_API.constant.AppConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

public class HttpUtils {

    public static String getClientIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader(AppConstant.HEADER_X_FORWARDED_FOR);
        if (forwardedFor != null && !forwardedFor.trim().isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public static HttpHeaders createAuthHeaders(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AppConstant.HEADER_ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (bearerToken != null && !bearerToken.trim().isEmpty()) {
            headers.set(AppConstant.HEADER_AUTHORIZATION, "Bearer " + bearerToken);
        }

        return headers;
    }

    public static void addOaoHeaders(org.apache.hc.client5.http.classic.methods.HttpPost httpPost, 
                                     String apiKey, String secretKey) {
        validateOaoConfig(apiKey, secretKey);
        httpPost.addHeader(AppConstant.HEADER_X_API_KEY, apiKey);
        httpPost.addHeader(AppConstant.HEADER_X_SECRET_KEY, secretKey);
    }

    private static void validateOaoConfig(String apiKey, String secretKey) {
        if (isNullOrEmpty(apiKey) || isNullOrEmpty(secretKey)) {
            throw new IllegalStateException(AppConstant.ERROR_MISSING_OAO_CONFIG);
        }
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private HttpUtils() {}
}
