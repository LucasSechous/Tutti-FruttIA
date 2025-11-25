package com.example.tuttifruttia.infrastructure.persistence.jpa.repository;

import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.ScoreBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScoreBoardJpaRepository extends JpaRepository<ScoreBoardEntity, UUID> {

    Optional<ScoreBoardEntity> findByGame_Id(UUID gameId);
}
