package com.example.tuttifruttia.domain.ai;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.Letter;
import com.example.tuttifruttia.domain.core.ValidationResult;

import java.util.List;

public class RuleBasedJudge implements AIJudge{

    private final int minLength;

    public RuleBasedJudge(int minLength) {
        this.minLength = minLength;
    }

    public RuleBasedJudge() {
        this(3);
    }

    @Override
    public ValidationResult validate(Letter letter, Category category, String text) {

        // 1) Caso vacío
        if (text == null || text.isBlank()) {
            return ValidationResult.empty(List.of("La respuesta se encuentra vacía"));
        }

        String normalized = text.trim();
        char firstChar = Character.toUpperCase(normalized.charAt(0));
        char expected = Character.toUpperCase(letter.getValue());

        // 2) Letra incorrecta
        if (firstChar != expected) {
            return ValidationResult.invalid(
                    List.of("La respuesta no empieza con la letra '" + expected + "'")
            );
        }

        // 3) Longitud mínima
        if (normalized.length() < minLength) {
            return ValidationResult.invalid(
                    List.of("La respuesta es demasiado corta (mínimo " + minLength + " caracteres)")
            );
        }

        // 4) Si pasa todas las reglas es válida(For now...).
        return ValidationResult.valid();
    }
}
