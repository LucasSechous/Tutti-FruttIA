package com.example.tuttifruttia.domain.ai;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.Letter;
import com.example.tuttifruttia.domain.core.ValidationResult;

public class SemanticJudge implements AIJudge{

    private final LLMProvider provider;

    public SemanticJudge(LLMProvider provider) {
        this.provider = provider;
    }

    @Override
    public ValidationResult validate(Letter letter, Category category, String text) {
        // armar prompt, llamar a provider.complete(...)
        // parsear respuesta del modelo y devolver VALID/INVALID con reasons
        // (esto ya lo pueden iterar más adelante)
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
