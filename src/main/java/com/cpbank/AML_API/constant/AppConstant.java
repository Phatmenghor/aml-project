package com.cpbank.AML_API.constant;

public class AppConstant {
    public static final String APP_TYPE_LOS = "LOS";
    public static final String APP_TYPE_OAO = "OAO";
    public static final String APP_TYPE_T24 = "T24";
    
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_PROCESSED = "Processed";
    
    public static final String HEADER_X_API_KEY = "X-API-KEY";
    public static final String HEADER_X_SECRET_KEY = "X-SECRET-KEY";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String HEADER_ACCEPT = "Accept";
    
    public static final String PREFIX_LOS = "C";
    
    public static final String RISK_LEVEL_LOW = "Low";
    
    public static final String SERVICE_NAME_INTUITION = "Intuition";
    public static final String SERVICE_NAME_AML = "AML API";
    
    public static final String UPDATE_FROM_SPRINGBOOT = "aml-springboot";
    
    public static final String ERROR_MISSING_OAO_CONFIG = "OAO Configuration Error: Missing API Key or Secret Key";
    public static final String ERROR_PROCESSING_REQUEST = "An unexpected error occurred while processing your request";
    public static final String ERROR_ACCESS_DENIED = "Access denied: You don't have permission to access this resource";
    
    public static final String TIMEZONE_PHNOM_PENH = "Asia/Phnom_Penh";
    public static final String TRANSACTION_ID_FORMAT = "yyMMddHHmmssSSS";
    
    public static final String CLASSPATH_PREFIX = "classpath:";
    public static final String BYPASS_AML_KEY = "byPassAml";
    
    public static final int CONNECT_TIMEOUT_MS = 30000;
    public static final int READ_TIMEOUT_MS = 60000;
    
    private AppConstant() {}
}
