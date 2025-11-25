package com.example.tuttifruttia.infrastructure.persistence.jpa.adapter;

import com.example.tuttifruttia.domain.core.GameSettings;
import com.example.tuttifruttia.domain.persistence.GameSettingsRepository;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.GameSettingsEntity;
import com.example.tuttifruttia.infrastructure.persistence.jpa.mapper.GameSettingsMapper;
import com.example.tuttifruttia.infrastructure.persistence.jpa.repository.GameSettingsJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class GameSettingsRepositoryJpaAdapter implements GameSettingsRepository {

    private final GameSettingsJpaRepository jpaRepository;
    private final GameSettingsMapper mapper;

    public GameSettingsRepositoryJpaAdapter(GameSettingsJpaRepository jpaRepository,
                                            GameSettingsMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(GameSettings settings) {
        // Dominio -> Entity
        GameSettingsEntity entity = mapper.toEntity(settings);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<GameSettings> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<GameSettings> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
