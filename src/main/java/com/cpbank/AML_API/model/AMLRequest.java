package com.cpbank.AML_API.model;

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

    public String getCUSTOMER_ID() { return CUSTOMER_ID; }
    public void setCUSTOMER_ID(String CUSTOMER_ID) { this.CUSTOMER_ID = CUSTOMER_ID; }

    public String getCUST_CREATE_DATE() { return CUST_CREATE_DATE; }
    public void setCUST_CREATE_DATE(String CUST_CREATE_DATE) { this.CUST_CREATE_DATE = CUST_CREATE_DATE; }

    public String getCUSTOMER_TYPE() { return CUSTOMER_TYPE; }
    public void setCUSTOMER_TYPE(String CUSTOMER_TYPE) { this.CUSTOMER_TYPE = CUSTOMER_TYPE; }

    public String getCUST_NAME() { return CUST_NAME; }
    public void setCUST_NAME(String CUST_NAME) { this.CUST_NAME = CUST_NAME; }

    public String getGIVEN_NAME() { return GIVEN_NAME; }
    public void setGIVEN_NAME(String GIVEN_NAME) { this.GIVEN_NAME = GIVEN_NAME; }

    public String getFAMILY_NAME() { return FAMILY_NAME; }
    public void setFAMILY_NAME(String FAMILY_NAME) { this.FAMILY_NAME = FAMILY_NAME; }

    public String getGENDER() { return GENDER; }
    public void setGENDER(String GENDER) { this.GENDER = GENDER; }

    public String getDATE_OF_BIRTH() { return DATE_OF_BIRTH; }
    public void setDATE_OF_BIRTH(String DATE_OF_BIRTH) { this.DATE_OF_BIRTH = DATE_OF_BIRTH; }

    public String getNATIONALITY() { return NATIONALITY; }
    public void setNATIONALITY(String NATIONALITY) { this.NATIONALITY = NATIONALITY; }

    public String getADDRESS() { return ADDRESS; }
    public void setADDRESS(String ADDRESS) { this.ADDRESS = ADDRESS; }

    public String getCUST_DISTRICT() { return CUST_DISTRICT; }
    public void setCUST_DISTRICT(String CUST_DISTRICT) { this.CUST_DISTRICT = CUST_DISTRICT; }

    public String getCUST_PROVINCE() { return CUST_PROVINCE; }
    public void setCUST_PROVINCE(String CUST_PROVINCE) { this.CUST_PROVINCE = CUST_PROVINCE; }

    public String getCOUNTRY() { return COUNTRY; }
    public void setCOUNTRY(String COUNTRY) { this.COUNTRY = COUNTRY; }

    public String getSMS_1() { return SMS_1; }
    public void setSMS_1(String SMS_1) { this.SMS_1 = SMS_1; }

    public String getPHONE_1() { return PHONE_1; }
    public void setPHONE_1(String PHONE_1) { this.PHONE_1 = PHONE_1; }

    public String getOFF_PHONE() { return OFF_PHONE; }
    public void setOFF_PHONE(String OFF_PHONE) { this.OFF_PHONE = OFF_PHONE; }

    public String getLEGAL_ID() { return LEGAL_ID; }
    public void setLEGAL_ID(String LEGAL_ID) { this.LEGAL_ID = LEGAL_ID; }

    public String getMARITAL_STATUS() { return MARITAL_STATUS; }
    public void setMARITAL_STATUS(String MARITAL_STATUS) { this.MARITAL_STATUS = MARITAL_STATUS; }

    public String getBUSINESS_SECTOR() { return BUSINESS_SECTOR; }
    public void setBUSINESS_SECTOR(String BUSINESS_SECTOR) { this.BUSINESS_SECTOR = BUSINESS_SECTOR; }

    public String getTARGET() { return TARGET; }
    public void setTARGET(String TARGET) { this.TARGET = TARGET; }

    public Double getINCOME() { return INCOME; }
    public void setINCOME(Double INCOME) { this.INCOME = INCOME; }

    public Integer getDOBYear() { return DOBYear; }
    public void setDOBYear(Integer DOBYear) { this.DOBYear = DOBYear; }

    public Integer getDOBMonth() { return DOBMonth; }
    public void setDOBMonth(Integer DOBMonth) { this.DOBMonth = DOBMonth; }

    public Integer getDOBDay() { return DOBDay; }
    public void setDOBDay(Integer DOBDay) { this.DOBDay = DOBDay; }

    public String getLEGAL_DOC_NAME() { return LEGAL_DOC_NAME; }
    public void setLEGAL_DOC_NAME(String LEGAL_DOC_NAME) { this.LEGAL_DOC_NAME = LEGAL_DOC_NAME; }

    public String getLEGAL_EXP_DATE() { return LEGAL_EXP_DATE; }
    public void setLEGAL_EXP_DATE(String LEGAL_EXP_DATE) { this.LEGAL_EXP_DATE = LEGAL_EXP_DATE; }

    public String getCUSTOMER_RATING() { return CUSTOMER_RATING; }
    public void setCUSTOMER_RATING(String CUSTOMER_RATING) { this.CUSTOMER_RATING = CUSTOMER_RATING; }
}
