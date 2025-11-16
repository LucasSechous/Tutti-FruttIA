package com.example.tuttifruttia.domain.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreBoard {

    private final List<ScoreEntry> entries = new ArrayList<>();

    public void addEntry(ScoreEntry entry) {
        entries.add(entry);
    }

    public List<ScoreEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public int total() {
        return entries.stream()
                .mapToInt(ScoreEntry::getPoints)
                .sum();
    }
}
