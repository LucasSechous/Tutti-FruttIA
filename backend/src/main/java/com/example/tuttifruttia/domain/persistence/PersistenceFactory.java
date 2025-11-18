package com.example.tuttifruttia.domain.persistence;

public interface PersistenceFactory {

    GameRepository gameRepo();

    ValidationLogRepository logRepo();

}
