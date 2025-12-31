package com.cpbank.AML_API.helper;

import com.cpbank.AML_API.dto.AmlUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class XmlBuilderHelper {

    @Value("${aml.company}")
    private String company;

    @Value("${aml.username}")
    private String username;

    @Value("${aml.password}")
    private String password;

    public String constructSoapPayload(AmlUpdateRequest request) {
        String rulesXml = "";
        if (request.getRuleTriggered() != null) {
            StringBuilder rulesBuilder = new StringBuilder();
            for (String rule : request.getRuleTriggered()) {
                rulesBuilder.append("<tuw:RULETRG>").append(rule).append("</tuw:RULETRG>");
            }
            rulesXml = rulesBuilder.toString();
        }

        return String.format(
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cpb=\"http://temenos.com/CPBAMLIntfWS\" xmlns:tuw=\"http://temenos.com/TUWSAMLSRVHNDLTUAMLUPD\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <cpb:WSAMLUPD>\n" +
                        "         <WebRequestCommon>\n" +
                        "            <company>%s</company>\n" +
                        "            <password>%s</password>\n" +
                        "            <userName>%s</userName>\n" +
                        "         </WebRequestCommon>\n" +
                        "         <OfsFunction>\n" +
                        "            <noOfAuth>0</noOfAuth>\n" +
                        "         </OfsFunction>\n" +
                        "         <TUWSAMLSRVHNDLTUAMLUPDType id=\"\">\n" +
                        "            <tuw:CUSTID>%s</tuw:CUSTID>\n" +
                        "            <tuw:SRVNAME>%s</tuw:SRVNAME>\n" +
                        "            <tuw:RISKLVL>%s</tuw:RISKLVL>\n" +
                        "            <tuw:ACTNTAKN>%s</tuw:ACTNTAKN>\n" +
                        "            <tuw:gRULETRG g=\"1\">\n" +
                        "               %s\n" +
                        "            </tuw:gRULETRG>\n" +
                        "            <tuw:TXNID>%s</tuw:TXNID>\n" +
                        "            <tuw:TOTRSCR>%s</tuw:TOTRSCR>\n" +
                        "         </TUWSAMLSRVHNDLTUAMLUPDType>\n" +
                        "      </cpb:WSAMLUPD>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>",
                company,
                password,
                username,
                request.getCustomerId(),
                request.getServiceName(),
                request.getRiskLevel(),
                request.getActionTaken(),
                rulesXml,
                request.getTransactionId(),
                request.getTotalScore()
        );
    }
}
