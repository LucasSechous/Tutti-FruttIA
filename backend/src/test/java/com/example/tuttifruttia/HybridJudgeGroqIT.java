package com.example.tuttifruttia;

import com.example.tuttifruttia.domain.ai.*;
import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.EntryStatus;
import com.example.tuttifruttia.domain.core.Letter;
import com.example.tuttifruttia.domain.core.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class HybridJudgeGroqIT {

    private HybridJudge newHybridJudge() {
        // 1) Cliente real de Groq, leyendo GROQ_API_KEY del entorno
        LLMProvider provider = Groq.fromEnv();

        // 2) SemanticJudge real (usa Groq)
        SemanticJudge semanticJudge = new SemanticJudge(provider);

        // 3) Regla básica simple (mínimo 3 caracteres, letra correcta, etc.)
        RuleBasedJudge ruleBasedJudge = new RuleBasedJudge(3);

        // 4) HybridJudge que combina ambos
        return new HybridJudge(ruleBasedJudge, semanticJudge);
    }

    @Test
    void givenValidCountryArgentina_shouldEndValid() {
        HybridJudge hybridJudge = newHybridJudge();

        // TODO: ajustá estos constructores a tus clases reales
        Letter letter = new Letter('F'); // o Letter.of('A'), etc.

        UUID id = UUID.randomUUID();
        Category category = new Category(id,"Videojuegos",true);     // o Category.of("País")

        ValidationResult result =
                hybridJudge.validate(letter, category, "Frotnite");

        System.out.println(">>> Resultado Argentina: "
                + result.getStatus() + " | " + result.getReasons());

        assertEquals(EntryStatus.VALID, result.getStatus(),
                "Esperábamos que 'Argentina' sea válida para letra A y categoría País");
    }

    @Test
    void givenInvalidCountryPerro_shouldEndInvalid() {
        HybridJudge hybridJudge = newHybridJudge();

        // TODO: ajustá estos constructores a tus clases reales
        Letter letter = new Letter('P');              // o Letter.of('A')
        UUID id = UUID.randomUUID();
        Category category = new Category(id,"Pais",true); // o Category.of("País")

        ValidationResult result =
                hybridJudge.validate(letter, category, "Perro");

        System.out.println(">>> Resultado Perro: "
                + result.getStatus() + " | " + result.getReasons());

        assertEquals(EntryStatus.INVALID, result.getStatus(),
                "Esperábamos que 'Perro' NO sea válido como País con letra A");
    }


}
