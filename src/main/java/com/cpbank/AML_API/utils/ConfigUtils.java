package com.cpbank.AML_API.utils;

import com.cpbank.AML_API.models.AmlConfig;
import com.cpbank.AML_API.repository.AmlConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigUtils {
    
    private final AmlConfigRepository amlConfigRepository;
    
    public static final String CONFIG_KEY_BYPASS_AML = "byPassAml";
    public static final String CONFIG_KEY_BYPASS_AML_CHECK = "byPassAmlCheck";
    
    public boolean getByPassAml() {
        return getConfigValue(CONFIG_KEY_BYPASS_AML, false);
    }
    
    public boolean getByPassAmlCheck() {
        return getConfigValue(CONFIG_KEY_BYPASS_AML_CHECK, false);
    }
    
    private boolean getConfigValue(String configKey, boolean defaultValue) {
        try {
            return amlConfigRepository.findByConfigKey(configKey)
                    .map(AmlConfig::getConfigValue)
                    .orElse(defaultValue);
        } catch (Exception e) {
            log.error("Error reading config for key: {}, using default: {}", configKey, defaultValue, e);
            return defaultValue;
        }
    }
}