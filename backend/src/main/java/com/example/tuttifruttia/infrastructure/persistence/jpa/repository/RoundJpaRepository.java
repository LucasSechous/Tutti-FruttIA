package com.example.tuttifruttia.infrastructure.persistence.jpa.repository;

import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.RoundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoundJpaRepository extends JpaRepository<RoundEntity, UUID> {

    // Ej: todas las rondas de un juego
    List<RoundEntity> findByGame_Id(UUID gameId);
}
