package com.example.tuttifruttia.domain.persistence;

import com.example.tuttifruttia.domain.core.SinglePlayerGame;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository {

    void save(SinglePlayerGame game);

    Optional<SinglePlayerGame> findById(UUID id);
}
