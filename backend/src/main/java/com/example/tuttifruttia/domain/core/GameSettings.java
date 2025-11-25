package com.example.tuttifruttia.domain.core;

import java.util.List;
import java.util.Objects;

import com.example.tuttifruttia.domain.scoring.PointsRule;
import com.example.tuttifruttia.domain.letter.Alphabet;

public class GameSettings {

    private final List<Category> categories;
    private final int roundSeconds;
    private final PointsRule pointsRule;
    private final Alphabet alphabet;

    public GameSettings(List<Category> categories,
                        int roundSeconds,
                        PointsRule pointsRule,
                        Alphabet alphabet) {

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

        this.categories = List.copyOf(categories);
        this.roundSeconds = roundSeconds;
        this.pointsRule = pointsRule;
        this.alphabet = alphabet;
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
