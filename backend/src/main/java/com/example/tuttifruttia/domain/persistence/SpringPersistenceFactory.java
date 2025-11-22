package com.example.tuttifruttia.domain.persistence;

import org.springframework.stereotype.Component;

@Component
public class SpringPersistenceFactory implements PersistenceFactory {

    private final GameRepository gameRepository;
    private final ValidationLogRepository validationLogRepository;

    public SpringPersistenceFactory(
            GameRepository gameRepository,
            ValidationLogRepository validationLogRepository
    ) {
        this.gameRepository = gameRepository;
        this.validationLogRepository = validationLogRepository;
    }

    @Override
    public GameRepository gameRepo() {
        return gameRepository;
    }

    @Override
    public ValidationLogRepository logRepo() {
        return validationLogRepository;
    }
}
