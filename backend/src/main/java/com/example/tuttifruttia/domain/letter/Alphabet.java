package com.example.tuttifruttia.domain.letter;

import java.util.List;


public class Alphabet {

    private final List<Character> letters;

    public Alphabet(List<Character> letters) {
        if (letters == null || letters.isEmpty()) {
            throw new IllegalArgumentException("El Alfabeto de letras no puede estar vacío");
        }
        this.letters = List.copyOf(letters);
    }

    public List<Character> getLetters() {

        return letters;
    }
}
