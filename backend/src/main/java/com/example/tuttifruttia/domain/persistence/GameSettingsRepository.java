package com.example.tuttifruttia.domain.persistence;

import com.example.tuttifruttia.domain.core.GameSettings;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameSettingsRepository {

    void save(GameSettings settings);

    Optional<GameSettings> findById(UUID id);

    List<GameSettings> findAll();
}
