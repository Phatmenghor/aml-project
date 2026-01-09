package com.cpbank.AML_API.repository;

import com.cpbank.AML_API.models.AmlConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmlConfigRepository extends JpaRepository<AmlConfig, Long> {
    Optional<AmlConfig> findByConfigKey(String configKey);
}