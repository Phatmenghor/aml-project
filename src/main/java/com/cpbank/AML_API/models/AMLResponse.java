package com.cpbank.AML_API.models;

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
}
