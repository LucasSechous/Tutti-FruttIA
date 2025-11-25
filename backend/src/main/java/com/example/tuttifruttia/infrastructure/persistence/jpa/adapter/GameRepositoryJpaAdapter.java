package com.example.tuttifruttia.infrastructure.persistence.jpa.adapter;

import com.example.tuttifruttia.domain.core.GameSettings;
import com.example.tuttifruttia.domain.core.SinglePlayerGame;
import com.example.tuttifruttia.domain.persistence.GameRepository;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.GameSettingsEntity;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.SinglePlayerGameEntity;
import com.example.tuttifruttia.infrastructure.persistence.jpa.repository.GameSettingsJpaRepository;
import com.example.tuttifruttia.infrastructure.persistence.jpa.repository.SinglePlayerGameJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class GameRepositoryJpaAdapter implements GameRepository {

    private final SinglePlayerGameJpaRepository gameJpaRepository;
    private final GameSettingsJpaRepository settingsJpaRepository;

    public GameRepositoryJpaAdapter(SinglePlayerGameJpaRepository gameJpaRepository,
                                    GameSettingsJpaRepository settingsJpaRepository) {
        this.gameJpaRepository = gameJpaRepository;
        this.settingsJpaRepository = settingsJpaRepository;
    }

    @Override
    public void save(SinglePlayerGame game) {

        // 1) Buscamos los settings en la BD usando el ID del GameSettings de dominio
        GameSettings domainSettings = game.getSettings();
        UUID settingsId = domainSettings.getId();

        GameSettingsEntity settingsEntity = settingsJpaRepository.findById(settingsId)
                .orElseThrow(() -> new IllegalStateException("GameSettings not found: " + settingsId));

        // 2) Buscamos si ya existe este juego en la BD
        UUID gameId = game.getId();
        SinglePlayerGameEntity entity = gameJpaRepository.findById(gameId)
                .orElseGet(() -> {
                    // si no existe, creamos uno nuevo y le seteamos el mismo id que el dominio
                    SinglePlayerGameEntity e = new SinglePlayerGameEntity(
                            game.getState().name(),
                            settingsEntity
                    );
                    e.setId(gameId);   // importantísimo: el ID viene del dominio
                    return e;
                });

        // 3) Actualizamos campos que pueden cambiar
        entity.setState(game.getState().name());
        entity.setSettings(settingsEntity);
        // TODO si querés más adelante: entity.setEndedAt(...) cuando el juego se termine, etc.

        // 4) Guardamos con Spring Data (insert o update según corresponda)
        gameJpaRepository.save(entity);
    }

    @Override
    public Optional<SinglePlayerGame> findById(UUID id) {
        // Por ahora lo dejamos sin implementar porque re-hidratar el agregado
        // requiere inyectar AIJudge, LetterStrategy, ScoreCalculator, PersistenceFactory, etc.
        return Optional.empty();
        // o:
        // throw new UnsupportedOperationException("findById not implemented yet");
    }
}
