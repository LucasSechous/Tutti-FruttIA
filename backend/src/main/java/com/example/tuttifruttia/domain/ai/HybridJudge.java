package com.example.tuttifruttia.domain.ai;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.EntryStatus;
import com.example.tuttifruttia.domain.core.Letter;
import com.example.tuttifruttia.domain.core.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HybridJudge implements AIJudge{

    private final AIJudge ruleBased;
    private final AIJudge semanticJudge;

    public HybridJudge(AIJudge ruleBased, AIJudge semanticJudge) {
        this.ruleBased = Objects.requireNonNull(ruleBased);
        this.semanticJudge = Objects.requireNonNull(semanticJudge);
    }

    @Override
    public ValidationResult validate(Letter letter, Category category, String text) {

        // 1) Primero pasa por las reglas "durísimas"
        ValidationResult basic = ruleBased.validate(letter, category, text);
        EntryStatus basicStatus = basic.getStatus();

        // Si ya es EMPTY o INVALID, no tiene sentido gastar IA
        if (basicStatus == EntryStatus.EMPTY || basicStatus == EntryStatus.INVALID) {
            return basic;
        }

        // 2) Si pasó las reglas básicas, lo chequeamos con LLM
        ValidationResult semanticResult = semanticJudge.validate(letter, category, text);

        // 3) Merge de resultados (razones combinadas, status final)
        return merge(basic, semanticResult);
    }

    private ValidationResult merge(ValidationResult basic, ValidationResult semanticResult) {

        EntryStatus semanticStatus = semanticResult.getStatus();

        // Si el semántico lo tira abajo → nos quedamos con él
        if (semanticStatus == EntryStatus.INVALID || semanticStatus == EntryStatus.EMPTY) {
            // Podrías decidir EMPTY vs INVALID según cómo definas el SemanticJudge,
            // pero en cualquier caso devolvemos lo que diga la IA.
            return semanticResult;
        }

        // En este punto:
        // - basicStatus == VALID (lo garantizamos en validate)
        // - semanticStatus == VALID también

        List<String> mergedReasons = new ArrayList<>();
        mergedReasons.addAll(basic.getReasons());
        mergedReasons.addAll(semanticResult.getReasons());

        return ValidationResult.valid(mergedReasons);
    }


}
