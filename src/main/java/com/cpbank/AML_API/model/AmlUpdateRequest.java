package com.cpbank.AML_API.model;

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

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }

    public List<String> getRuleTriggered() { return ruleTriggered; }
    public void setRuleTriggered(List<String> ruleTriggered) { this.ruleTriggered = ruleTriggered; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
}
