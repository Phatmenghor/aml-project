package com.cpbank.AML_API.model;

import lombok.Data;

@Data
public class AmlUpdateResponse {
    private String status;
    private String message;
    private String downstreamApp;
    private String downstreamResponse;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDownstreamApp() { return downstreamApp; }
    public void setDownstreamApp(String downstreamApp) { this.downstreamApp = downstreamApp; }

    public String getDownstreamResponse() { return downstreamResponse; }
    public void setDownstreamResponse(String downstreamResponse) { this.downstreamResponse = downstreamResponse; }
}
