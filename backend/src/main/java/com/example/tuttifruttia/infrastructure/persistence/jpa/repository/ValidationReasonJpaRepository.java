package com.example.tuttifruttia.infrastructure.persistence.jpa.repository;

import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.ValidationReasonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ValidationReasonJpaRepository extends JpaRepository<ValidationReasonEntity, UUID> {

    List<ValidationReasonEntity> findByValidationLog_IdOrderByPositionAsc(UUID validationLogId);
}
