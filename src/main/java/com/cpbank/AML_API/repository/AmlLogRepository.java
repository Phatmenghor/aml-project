package com.cpbank.AML_API.repository;

import com.cpbank.AML_API.model.AmlLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmlLogRepository extends JpaRepository<AmlLog, Long> {
}
