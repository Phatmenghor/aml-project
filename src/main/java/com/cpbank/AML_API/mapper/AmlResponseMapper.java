package com.cpbank.AML_API.mapper;

import com.cpbank.AML_API.dto.AmlUpdateResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class AmlResponseMapper {

    public static AmlUpdateResult mapToAmlUpdateResult(Map<String, Object> xmlMap) {
        AmlUpdateResult result = new AmlUpdateResult();
        
        try {
            Map<String, Object> targetMap = navigateToTargetNode(xmlMap);
            populateResultFields(result, targetMap);
        } catch (Exception e) {
            log.error("Error mapping XML to DTO", e);
        }
        
        return result;
    }

    private static Map<String, Object> navigateToTargetNode(Map<String, Object> map) {
        Map<String, Object> current = map;

        if (current.containsKey("WSAMLUPDResponse")) {
            Object val = current.get("WSAMLUPDResponse");
            if (val instanceof Map) {
                current = (Map<String, Object>) val;
            }
        }
        
        if (current.containsKey("TUWSAMLSRVHNDLType")) {
            Object val = current.get("TUWSAMLSRVHNDLType");
            if (val instanceof Map) {
                current = (Map<String, Object>) val;
            }
        }

        return current;
    }

    private static void populateResultFields(AmlUpdateResult result, Map<String, Object> map) {
        result.setId(getStringValue(map, "id"));
        result.setCUSTID(getStringValue(map, "CUSTID"));
        result.setSRVNAME(getStringValue(map, "SRVNAME"));
        result.setRISKLVL(getStringValue(map, "RISKLVL"));
        result.setACTNTAKN(getStringValue(map, "ACTNTAKN"));
        result.setGRULETRG(extractRuleTriggers(map));
        result.setTXNID(getStringValue(map, "TXNID"));
        result.setTOTRSCR(getStringValue(map, "TOTRSCR"));
        result.setRETCODE(getStringValue(map, "RETCODE"));
        result.setRETDESC(getStringValue(map, "RETDESC"));
    }

    private static AmlUpdateResult.GRULETRG extractRuleTriggers(Map<String, Object> map) {
        if (!map.containsKey("gRULETRG")) {
            return null;
        }

        Object gruleObj = map.get("gRULETRG");
        if (!(gruleObj instanceof Map)) {
            return null;
        }

        Map<String, Object> gruleMap = (Map<String, Object>) gruleObj;
        AmlUpdateResult.GRULETRG grule = new AmlUpdateResult.GRULETRG();

        if (gruleMap.containsKey("RULETRG")) {
            Object ruleContent = gruleMap.get("RULETRG");
            if (ruleContent instanceof String) {
                grule.setRULETRG(java.util.Collections.singletonList((String) ruleContent));
            } else if (ruleContent instanceof List) {
                grule.setRULETRG((List<String>) ruleContent);
            }
        }

        return grule;
    }

    private static String getStringValue(Map<String, Object> map, String key) {
        return map.containsKey(key) ? (String) map.get(key) : null;
    }

    private AmlResponseMapper() {}
}
