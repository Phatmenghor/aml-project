package com.cpbank.AML_API.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class AMLResponse {
    @JsonProperty("RiskLevel")
    private String RiskLevel;
    @JsonProperty("ActionTaken")
    private String ActionTaken;
    @JsonProperty("RulesTriggered")
    private List<String> RulesTriggered;
    @JsonProperty("ServiceName")
    private String ServiceName;
    @JsonProperty("TotalRulesScore")
    private int TotalRulesScore;
    @JsonProperty("TrxnID")
    private String TrxnID;

    public String getRiskLevel() { return RiskLevel; }
    public void setRiskLevel(String riskLevel) { RiskLevel = riskLevel; }

    public String getActionTaken() { return ActionTaken; }
    public void setActionTaken(String actionTaken) { ActionTaken = actionTaken; }

    public List<String> getRulesTriggered() { return RulesTriggered; }
    public void setRulesTriggered(List<String> rulesTriggered) { RulesTriggered = rulesTriggered; }

    public String getServiceName() { return ServiceName; }
    public void setServiceName(String serviceName) { ServiceName = serviceName; }

    public int getTotalRulesScore() { return TotalRulesScore; }
    public void setTotalRulesScore(int totalRulesScore) { TotalRulesScore = totalRulesScore; }

    public String getTrxnID() { return TrxnID; }
    public void setTrxnID(String trxnID) { TrxnID = trxnID; }
}
