package com.example.tuttifruttia.domain.persistence;

import com.example.tuttifruttia.domain.core.SinglePlayerGame;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {

    private final Map<UUID, SinglePlayerGame> store = new ConcurrentHashMap<>();

    @Override
    public void save(SinglePlayerGame game) {
        if (game == null) {
            throw new IllegalArgumentException("game is required");
        }
        store.put(game.getId(), game);
    }

    @Override
    public Optional<SinglePlayerGame> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }
}
