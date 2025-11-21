package com.example.tuttifruttia.domain.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnswerSet {

    // Ahora el mapa es coherente en todos lados: Category -> PlayerAnswer
    private final Map<Category, PlayerAnswer> byCategory = new HashMap<>();

    // --- PUT con solo texto (cómodo para usar desde el servicio) ---
    public void put(Category category, String text) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        PlayerAnswer pAnswer = new PlayerAnswer(category, text);
        byCategory.put(category, pAnswer);
    }

    // --- PUT con PlayerAnswer ya construido ---
    public void put(Category category, PlayerAnswer answer) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (answer == null) {
            throw new IllegalArgumentException("PlayerAnswer is required");
        }
        byCategory.put(category, answer);
    }

    // --- Obtener una respuesta por categoría ---
    public PlayerAnswer get(Category category) {
        return byCategory.get(category);
    }

    // --- Obtener todas las respuestas ---
    public Map<Category, PlayerAnswer> getByCategory() {
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
