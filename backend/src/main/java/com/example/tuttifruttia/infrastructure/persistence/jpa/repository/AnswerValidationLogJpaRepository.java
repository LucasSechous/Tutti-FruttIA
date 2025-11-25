package com.example.tuttifruttia.infrastructure.persistence.jpa.repository;

import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.AnswerValidationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnswerValidationLogJpaRepository extends JpaRepository<AnswerValidationLogEntity, UUID> {

    List<AnswerValidationLogEntity> findByRound_Id(UUID roundId);

    List<AnswerValidationLogEntity> findByAnswer_Id(UUID answerId);
}
