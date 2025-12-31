package com.cpbank.AML_API.helper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

public class XmlParserHelper {

    public static Map<String, Object> parseSoapXml(String xmlData) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // Handle namespaces if needed
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlData)));

            Map<String, Object> resultMap = new HashMap<>();
            parseNode(doc.getDocumentElement(), resultMap);
            
            // Auto-unwrap SOAP Envelope and Body if present
            if (resultMap.size() == 1) {
                // Check for Envelope
                Object envelope = resultMap.get("Envelope");
                if (envelope == null) envelope = resultMap.values().iterator().next(); // fallback if name differs

                if (envelope instanceof Map) {
                    Map<String, Object> envMap = (Map<String, Object>) envelope;
                    Object body = envMap.get("Body");
                    if (body instanceof Map) {
                        return (Map<String, Object>) body;
                    }
                }
            }
            
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to parse XML: " + e.getMessage());
            error.put("originalXml", xmlData);
            return error;
        }
    }

    private static void parseNode(Node node, Map<String, Object> parentMap) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String nodeName = node.getLocalName(); // Ignore namespace prefix for JSON keys
            if(nodeName == null) nodeName = node.getNodeName();

            // Check if it has child elements
            if (hasChildElements(node)) {
                Map<String, Object> childMap = new HashMap<>();
                NodeList children = node.getChildNodes();
                
                // Handling lists (multiple children with same name) could be complex, 
                // but for this specific response, we can use a simpler approach or a robust one.
                // Here is a simple recursion:
                for (int i = 0; i < children.getLength(); i++) {
                    parseNode(children.item(i), childMap);
                }
                
                // Add to parent, handle collisions if key exists (turn into list)
                addToParent(parentMap, nodeName, childMap);

            } else {
                // Text content
                String text = node.getTextContent();
                if(text != null) text = text.trim();
                if(!text.isEmpty()){
                     addToParent(parentMap, nodeName, text);
                }
            }
        }
    }
    
    // Robust insertion to handle arrays/lists in XML
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

    private static boolean hasChildElements(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }
}
