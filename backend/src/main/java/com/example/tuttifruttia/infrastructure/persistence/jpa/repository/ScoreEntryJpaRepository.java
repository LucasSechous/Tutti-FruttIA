package com.example.tuttifruttia.infrastructure.persistence.jpa.repository;

import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.ScoreEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScoreEntryJpaRepository extends JpaRepository<ScoreEntryEntity, UUID> {

    List<ScoreEntryEntity> findByScoreboard_Id(UUID scoreboardId);

    List<ScoreEntryEntity> findByRound_Id(UUID roundId);
}
