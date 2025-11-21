package com.example.tuttifruttia.domain.core;

import java.util.Objects;

public class Letter {

    private final char value;

    public char getValue() {
        return value;
    }

    public Letter(char value) {
        if (!Character.isLetter(value)) {
            throw new IllegalArgumentException("Letter must be an alphabetic char");
        }
        this.value = Character.toUpperCase(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Letter)) return false;
        Letter letter = (Letter) o;
        return value == letter.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
