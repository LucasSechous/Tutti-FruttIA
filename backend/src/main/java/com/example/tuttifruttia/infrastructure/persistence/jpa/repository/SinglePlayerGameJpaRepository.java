package com.example.tuttifruttia.infrastructure.persistence.jpa.repository;

import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.SinglePlayerGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SinglePlayerGameJpaRepository extends JpaRepository<SinglePlayerGameEntity, UUID> {

    // Ejemplo: buscar por estado
    List<SinglePlayerGameEntity> findByState(String state);
}
