package com.example.tuttifruttia.controller.dto;

import java.util.List;

public class UpdateAlphabetRequestDto {

    private List<Character> enabledLetters;

    public UpdateAlphabetRequestDto() {}

    public List<Character> getEnabledLetters() {
        return enabledLetters;
    }

    public void setEnabledLetters(List<Character> enabledLetters) {
        this.enabledLetters = enabledLetters;
    }
}
