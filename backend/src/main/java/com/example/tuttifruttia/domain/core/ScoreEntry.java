package com.example.tuttifruttia.domain.core;

public class ScoreEntry {

    private final Category category;
    private final int points;
    private final EntryStatus status;

    public ScoreEntry(Category category, int points, EntryStatus status) {
        this.category = category;
        this.points = points;
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public int getPoints() {
        return points;
    }


    public EntryStatus getStatus() {
        return status;
    }
}
