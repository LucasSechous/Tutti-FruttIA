package com.example.tuttifruttia.domain.scoring;

import com.example.tuttifruttia.domain.core.*;

import java.util.Map;

public class DefaultScoreCalculator implements ScoreCalculator{

    @Override
    public ScoreBoard compute(Map<Category, ValidationResult> results, PointsRule rule){

        ScoreBoard board = new ScoreBoard();

        for (Map.Entry<Category, ValidationResult> entry : results.entrySet()) {
            Category category = entry.getKey();
            ValidationResult vr = entry.getValue();

            if (vr == null) {
                int points = rule.getPointsInvalid();
                board.addEntry(new ScoreEntry(category, points, EntryStatus.INVALID));
                continue;
            }

            EntryStatus status = vr.getStatus();
            int points;

            switch (status) {
                case VALID -> points = rule.getPointsValid();
                case EMPTY -> points = rule.getPointsEmpty();
                case INVALID -> points = rule.getPointsInvalid();
                default -> throw new IllegalStateException("Estado no soportado: " + status);
            }

            ScoreEntry scoreEntry = new ScoreEntry(category, points, status);
            board.addEntry(scoreEntry);
        }

        return board;
    }

}
