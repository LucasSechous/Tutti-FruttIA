package com.example.tuttifruttia.domain.letter;

import java.util.Set;

public class Alphabet {

    private final Set<Character> letters;

    public Alphabet(Set<Character> letters) {
        if (letters == null || letters.isEmpty()) {
            throw new IllegalArgumentException("El Alfabeto no puede estar vacío");
        }
        this.letters = Set.copyOf(letters);
    }

    public Set<Character> getLetters() {

        return letters;
    }
}
