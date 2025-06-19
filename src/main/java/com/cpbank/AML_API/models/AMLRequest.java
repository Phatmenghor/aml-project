package com.cpbank.AML_API.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class AMLRequest {
    @JsonProperty("CUSTOMER_ID")
    private String CUSTOMER_ID;
    @JsonProperty("CUST_CREATE_DATE")
    private String CUST_CREATE_DATE;
    @JsonProperty("CUSTOMER_TYPE")
    private String CUSTOMER_TYPE;
    @JsonProperty("CUST_NAME")
    private String CUST_NAME;
    @JsonProperty("GIVEN_NAME")
    private String GIVEN_NAME;
    @JsonProperty("FAMILY_NAME")
    private String FAMILY_NAME;
    @JsonProperty("GENDER")
    private String GENDER;
    @JsonProperty("DATE_OF_BIRTH")
    private String DATE_OF_BIRTH;
    @JsonProperty("NATIONALITY")
    private String NATIONALITY;
    @JsonProperty("ADDRESS")
    private String ADDRESS;
    @JsonProperty("CUST_DISTRICT")
    private String CUST_DISTRICT;
    @JsonProperty("CUST_PROVINCE")
    private String CUST_PROVINCE;
    @JsonProperty("COUNTRY")
    private String COUNTRY;
    @JsonProperty("SMS_1")
    private String SMS_1;
    @JsonProperty("PHONE_1")
    private String PHONE_1;
    @JsonProperty("OFF_PHONE")
    private String OFF_PHONE;
    @JsonProperty("LEGAL_ID")
    private String LEGAL_ID;
    @JsonProperty("MARITAL_STATUS")
    private String MARITAL_STATUS;
    @JsonProperty("BUSINESS_SECTOR")
    private String BUSINESS_SECTOR;
    @JsonProperty("TARGET")
    private String TARGET;
    @JsonProperty("INCOME")
    private Double INCOME;
    @JsonProperty("DOBYear")
    private Integer DOBYear;
    @JsonProperty("DOBMonth")
    private Integer DOBMonth;
    @JsonProperty("DOBDay")
    private Integer DOBDay;
    @JsonProperty("LEGAL_DOC_NAME")
    private String LEGAL_DOC_NAME;
    @JsonProperty("LEGAL_EXP_DATE")
    private String LEGAL_EXP_DATE;
    @JsonProperty("CUSTOMER_RATING")
    private String CUSTOMER_RATING;
}
