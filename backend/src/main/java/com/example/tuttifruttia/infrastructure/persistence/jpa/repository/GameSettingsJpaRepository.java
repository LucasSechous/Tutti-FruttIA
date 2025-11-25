package com.example.tuttifruttia.infrastructure.persistence.jpa.repository;

import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.GameSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameSettingsJpaRepository extends JpaRepository<GameSettingsEntity, UUID> {
    // si querés después podés agregar métodos custom, ej:
    // List<GameSettingsEntity> findByRoundSeconds(int roundSeconds);
}
