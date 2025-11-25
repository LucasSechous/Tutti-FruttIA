package com.example.tuttifruttia.controller.dto;

import java.util.List;

public class AlphabetDto {

    private List<Character> letters;

    public AlphabetDto() {}

    public AlphabetDto(List<Character> letters) {
        this.letters = letters;
    }

    public List<Character> getLetters() {
        return letters;
    }

    public void setLetters(List<Character> letters) {
        this.letters = letters;
    }
}
