package com.cpbank.AML_API.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.InputStream;
@Component
public  class ReadJson {

    @Value("${config.json.path:classpath:conf.json}")
    private  String conf;



    public boolean readByPassAml() {
        ObjectMapper mapper = new ObjectMapper();
        boolean byPassAml = false;

        try {
            System.out.println("#conf file# " + conf);

            // If conf is a filesystem path
            if (!conf.startsWith("classpath:")) {
                File file = new File(conf);
                if (file.exists()) {
                    JsonNode node = mapper.readTree(file);
                    byPassAml = node.get("byPassAml").asBoolean();
                }
            } else {
                // Read from classpath
                String resourcePath = conf.replace("classpath:", "");
                try (InputStream inputStream = ReadJson.class.getResourceAsStream("/" + resourcePath)) {
                    if (inputStream != null) {
                        JsonNode node = mapper.readTree(inputStream);
                        byPassAml = node.get("byPassAml").asBoolean();
                    } else {
                        System.err.println("Classpath resource not found: " + resourcePath);
                    }
                }
            }

            System.out.println("by PassAml value: " + byPassAml);
        } catch (Exception e) {
            System.err.println("Error reading conf.json: " + e.getMessage());
        }

        return byPassAml;
    }



}
