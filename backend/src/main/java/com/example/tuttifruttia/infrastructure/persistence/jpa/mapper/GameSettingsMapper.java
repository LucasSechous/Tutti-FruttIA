package com.example.tuttifruttia.infrastructure.persistence.jpa.mapper;

import com.example.tuttifruttia.domain.core.GameSettings;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.GameSettingsEntity;

public interface GameSettingsMapper {

    GameSettingsEntity toEntity(GameSettings domain);

    GameSettings toDomain(GameSettingsEntity entity);
}
