package com.cpbank.AML_API.utils;

import com.cpbank.AML_API.dto.AmlUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class XmlUtils {

    public static String buildSoapPayload(AmlUpdateRequest request, String company, 
                                         String username, String password) {
        String rulesXml = buildRulesXml(request.getRuleTriggered());

        return String.format(
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:cpb=\"http://temenos.com/CPBAMLIntfWS\" " +
                "xmlns:tuw=\"http://temenos.com/TUWSAMLSRVHNDLTUAMLUPD\">\n" +
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
                company, password, username,
                request.getCustomerId(),
                request.getServiceName(),
                request.getRiskLevel(),
                request.getActionTaken(),
                rulesXml,
                request.getTransactionId(),
                request.getTotalScore()
        );
    }

    public static Map<String, Object> parseSoapXml(String xmlData) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlData)));

            Map<String, Object> resultMap = new HashMap<>();
            parseNode(doc.getDocumentElement(), resultMap);
            return unwrapSoapEnvelope(resultMap);

        } catch (Exception e) {
            log.error("Failed to parse XML", e);
            return createErrorResponse(xmlData, e);
        }
    }

    private static String buildRulesXml(List<String> rules) {
        if (rules == null || rules.isEmpty()) {
            return "";
        }

        StringBuilder rulesBuilder = new StringBuilder();
        for (String rule : rules) {
            rulesBuilder.append("<tuw:RULETRG>").append(rule).append("</tuw:RULETRG>");
        }
        return rulesBuilder.toString();
    }

    private static void parseNode(Node node, Map<String, Object> parentMap) {
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            return;
        }

        String nodeName = getNodeName(node);
        Map<String, Object> childMap = new HashMap<>();

        extractAttributes(node, childMap);

        if (hasChildElements(node)) {
            parseChildElements(node, childMap);
            addToParent(parentMap, nodeName, childMap);
        } else {
            parseTextContent(node, childMap, parentMap, nodeName);
        }
    }

    private static String getNodeName(Node node) {
        String localName = node.getLocalName();
        return localName != null ? localName : node.getNodeName();
    }

    private static void extractAttributes(Node node, Map<String, Object> childMap) {
        if (!node.hasAttributes()) {
            return;
        }

        org.w3c.dom.NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            String attrName = attr.getNodeName();
            if (!attrName.startsWith("xmlns:")) {
                childMap.put(attrName, attr.getNodeValue());
            }
        }
    }

    private static void parseChildElements(Node node, Map<String, Object> childMap) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            parseNode(children.item(i), childMap);
        }
    }

    private static void parseTextContent(Node node, Map<String, Object> childMap,
                                        Map<String, Object> parentMap, String nodeName) {
        String text = node.getTextContent();
        if (text != null) text = text.trim();

        if (!childMap.isEmpty()) {
            if (!text.isEmpty()) {
                childMap.put("value", text);
            }
            addToParent(parentMap, nodeName, childMap);
        } else {
            if (!text.isEmpty()) {
                addToParent(parentMap, nodeName, text);
            }
        }
    }

    private static boolean hasChildElements(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    private static void addToParent(Map<String, Object> map, String key, Object value) {
        if (map.containsKey(key)) {
            Object existing = map.get(key);
            if (existing instanceof List) {
                ((List<Object>) existing).add(value);
            } else {
                List<Object> list = new ArrayList<>();
                list.add(existing);
                list.add(value);
                map.put(key, list);
            }
        } else {
            map.put(key, value);
        }
    }

    private static Map<String, Object> unwrapSoapEnvelope(Map<String, Object> resultMap) {
        if (resultMap.size() != 1) {
            return resultMap;
        }

        Object envelope = resultMap.get("Envelope");
        if (envelope == null) {
            envelope = resultMap.values().iterator().next();
        }

        if (envelope instanceof Map) {
            Map<String, Object> envelopeMap = (Map<String, Object>) envelope;
            Object body = envelopeMap.get("Body");
            if (body instanceof Map) {
                return (Map<String, Object>) body;
            }
        }

        return resultMap;
    }

    private static Map<String, Object> createErrorResponse(String xmlData, Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Failed to parse XML: " + e.getMessage());
        error.put("originalXml", xmlData);
        return error;
    }

    private XmlUtils() {}
}
