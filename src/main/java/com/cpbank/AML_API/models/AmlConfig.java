package com.cpbank.AML_API.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "aml_config")
@Data
public class AmlConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "config_key", unique = true, nullable = false)
    private String configKey;
    
    @Column(name = "config_value", nullable = false)
    private Boolean configValue;
    
    @Column(name = "description")
    private String description;
}


