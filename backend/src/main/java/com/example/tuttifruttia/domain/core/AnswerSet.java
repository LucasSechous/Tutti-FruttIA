package com.example.tuttifruttia.domain.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnswerSet {

    private final Map<Category, Answer> byCategory = new HashMap<>();

    public void put(Category category, String text) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        Answer answer = new Answer(text, false, null); // la validación vendrá después
        byCategory.put(category, answer);
    }

    public Map<Category, Answer> getByCategory() {
        return Collections.unmodifiableMap(byCategory);
    }

    public boolean isComplete(Iterable<Category> expectedCategories) {
        for (Category cat : expectedCategories) {
            if (!byCategory.containsKey(cat)) {
                return false;
            }
        }
        return true;
    }
}
