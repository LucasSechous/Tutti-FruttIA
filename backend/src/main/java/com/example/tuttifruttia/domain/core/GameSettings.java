package com.example.tuttifruttia.domain.core;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.example.tuttifruttia.domain.scoring.PointsRule;
import com.example.tuttifruttia.domain.letter.Alphabet;

public class GameSettings {

    private final UUID id;                    // 👈 NUEVO
    private final List<Category> categories;
    private final int roundSeconds;
    private final PointsRule pointsRule;
    private final Alphabet alphabet;

    // 👇 Constructor que usabas hasta ahora: genera un id nuevo
    public GameSettings(List<Category> categories,
                        int roundSeconds,
                        PointsRule pointsRule,
                        Alphabet alphabet) {
        this(UUID.randomUUID(), categories, roundSeconds, pointsRule, alphabet);
    }

    // 👇 Constructor con id explícito (útil cuando lo traigas de BD)
    public GameSettings(UUID id,
                        List<Category> categories,
                        int roundSeconds,
                        PointsRule pointsRule,
                        Alphabet alphabet) {

        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("At least one category is required");
        }
        if (roundSeconds <= 0) {
            throw new IllegalArgumentException("roundSeconds must be > 0");
        }
        if (pointsRule == null) {
            throw new IllegalArgumentException("pointsRule is required");
        }
        if (alphabet == null) {
            throw new IllegalArgumentException("alphabet is required");
        }

        this.id = id;
        this.categories = List.copyOf(categories);
        this.roundSeconds = roundSeconds;
        this.pointsRule = pointsRule;
        this.alphabet = alphabet;
    }

    // 👈 NUEVO getter
    public UUID getId() {
        return id;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public int getRoundSeconds() {
        return roundSeconds;
    }

    public PointsRule getPointsRule() {
        return pointsRule;
    }

    public Alphabet getAlphabet() {
        return alphabet;
    }

    public void validate() {
        Objects.requireNonNull(categories, "categories");
        Objects.requireNonNull(pointsRule, "pointsRule");
        Objects.requireNonNull(alphabet, "alphabet");
    }
}
