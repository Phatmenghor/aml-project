package com.cpbank.AML_API.model;

import lombok.Data;

@Data
public class AmlUpdateResponse {
    private String status;
    private String message;
    private String appResponse;
    private String appType;
    private Object result;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAppResponse() { return appResponse; }
    public void setAppResponse(String appResponse) { this.appResponse = appResponse; }

    public String getAppType() { return appType; }
    public void setAppType(String appType) { this.appType = appType; }

    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }
}
