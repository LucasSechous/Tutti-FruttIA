package com.example.tuttifruttia.domain.ai;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.Letter;
import com.example.tuttifruttia.domain.core.ValidationResult;

import java.util.List;

public class FakeAIJudge implements AIJudge {

    @Override
    public ValidationResult validate(Letter letter, Category category, String text) {

        // 1) Respuesta vacía
        if (text == null || text.isBlank()) {
            return ValidationResult.empty(List.of("La respuesta está vacía"));
        }

        String trimmed = text.trim();
        char firstChar = Character.toUpperCase(trimmed.charAt(0));

        // 2) Regla simple: debe empezar con la letra sorteada
        if (firstChar != letter.getValue()) {
            return ValidationResult.invalid(
                    List.of("La respuesta no empieza con la letra " + letter.getValue())
            );
        }

        // 3) TODO: acá más adelante se puede agregar lógica semántica real
        return ValidationResult.valid(); // OK, sin razones
    }
}
