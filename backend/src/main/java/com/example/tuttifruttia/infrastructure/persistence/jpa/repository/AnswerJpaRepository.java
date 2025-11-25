package com.example.tuttifruttia.infrastructure.persistence.jpa.repository;

import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnswerJpaRepository extends JpaRepository<AnswerEntity, UUID> {

    // Ej: respuestas por ronda
    List<AnswerEntity> findByRound_Id(UUID roundId);
}
