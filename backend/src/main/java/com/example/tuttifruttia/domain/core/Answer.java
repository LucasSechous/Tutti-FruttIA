package com.example.tuttifruttia.domain.core;


import java.util.List;

public class Answer {

    private final String text;
    private final boolean valid;
    private final List<String> reasons;

    public Answer(String text, boolean valid, List<String> reasons) {
        this.text = text;
        this.valid = valid;
        this.reasons = reasons == null ? List.of() : List.copyOf(reasons);
    }

    public String getText() {
        return text;
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
