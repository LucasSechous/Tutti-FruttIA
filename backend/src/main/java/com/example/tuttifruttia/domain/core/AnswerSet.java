package com.example.tuttifruttia.domain.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnswerSet {

    private final Map<Category, Answer> byCategory = new HashMap<>();

    // --- PUT con solo texto (placeholder inicial) ---
    public void put(Category category, String text) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        Answer answer = new Answer(text, false, null); // la validación vendrá después
        byCategory.put(category, answer);
    }

    // --- PUT con Answer completo (para AIJudge / validate) ---
    public void put(Category category, Answer answer) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (answer == null) {
            throw new IllegalArgumentException("Answer is required");
        }
        byCategory.put(category, answer);
    }

    // --- Obtener una respuesta por categoría ---
    public Answer get(Category category) {
        return byCategory.get(category);
    }

    // --- Obtener todas las respuestas ---
    public Map<Category, Answer> getByCategory() {
        return Collections.unmodifiableMap(byCategory);
    }

    // --- Comprobar completitud según settings ---
    public boolean isComplete(Iterable<Category> expectedCategories) {
        for (Category cat : expectedCategories) {
            if (!byCategory.containsKey(cat)) {
                return false;
            }
        }
        return true;
    }
}
