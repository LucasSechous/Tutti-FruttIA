package com.example.tuttifruttia.domain.core;

public class PlayerAnswer {

    private final Category category;
    private final String text;

    public PlayerAnswer(Category category, String text) {
        this.category = category;
        this.text = text;
    }

    public Category getCategory() {
        return category;
    }

    public String getText() {
        return text;
    }
}
