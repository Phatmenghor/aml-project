package com.cpbank.AML_API.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class AmlUpdateResult {
    private String id;
    
    @JsonProperty("CUSTID")
    private String CUSTID;
    
    @JsonProperty("SRVNAME")
    private String SRVNAME;
    
    @JsonProperty("RISKLVL")
    private String RISKLVL;
    
    @JsonProperty("ACTNTAKN")
    private String ACTNTAKN;
    
    @JsonProperty("gRULETRG")
    private GRULETRG gRULETRG;
    
    @JsonProperty("TXNID")
    private String TXNID;
    
    @JsonProperty("TOTRSCR")
    private String TOTRSCR;
    
    @JsonProperty("RETCODE")
    private String RETCODE;
    
    @JsonProperty("RETDESC")
    private String RETDESC;

    @Data
    public static class GRULETRG {
        @JsonProperty("RULETRG")
        private List<String> RULETRG;
    }
}
