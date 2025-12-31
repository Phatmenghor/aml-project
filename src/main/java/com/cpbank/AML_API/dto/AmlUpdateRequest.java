package com.cpbank.AML_API.dto;

import lombok.Data;
import java.util.List;

@Data
public class AmlUpdateRequest {
    private String customerId;
    private String serviceName;
    private String riskLevel;
    private String actionTaken;
    private List<String> ruleTriggered;
    private String transactionId;
    private Integer totalScore;
}
