package com.example.tuttifruttia.domain.ai;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.Letter;
import com.example.tuttifruttia.domain.core.ValidationResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

public class SemanticJudge implements AIJudge{

    private static final String SYSTEM_PROMPT = """
    Sos un árbitro estricto de un juego de Tutti Frutti (Stop, Basta).
    Reglas del juego:
    - El jugador debe escribir UNA sola palabra por respuesta, exeptuando si es una palabra compuesta.
    - La palabra debe comenzar EXACTAMENTE con la letra indicada (sin contar tildes ni espacios).
    - La palabra debe pertenecer claramente a la categoría indicada (por ejemplo: País, Animal, Fruta, Color, etc.).
    - La palabra debe ser un término real en español o nombre propio válido (no inventado).
    - Si la respuesta está vacía, son solo espacios o símbolos, se considera inválida.
    - Si la respuesta se asemeja en mas de un 95% de la letras de la palabra correcta, por error de misclick por ejemplo, puede darse como valida
    
    Tu tarea:
    - Analizar la respuesta del jugador con respecto a la letra y la categoría.
    - Decidir si es válida o inválida.
    
    Formato de respuesta:
    Devolvé SIEMPRE y ÚNICAMENTE un JSON con este formato EXACTO, sin texto adicional:
    
    {
      "status": "VALID" | "INVALID",
      "reason": "explicación corta en español"
    }
    """;


    private final LLMProvider provider;

    private final Gson gson = new Gson();

    public SemanticJudge(LLMProvider provider) {
        this.provider = provider;
    }

    @Override
    public ValidationResult validate(Letter letter, Category category, String text) {
        // 1) Manejo de casos triviales sin llamar a la IA
        if (text == null || text.isBlank()) {
            // Ajustá estos métodos a tu API real de ValidationResult
            return ValidationResult.invalid(List.of("Respuesta vacía o nula"));
        }

        String trimmed = text.trim();

        // 2) Armar prompt de usuario
        String userPrompt = String.format("""
        Letra: %s
        Categoría: %s
        Respuesta del jugador: "%s"
        
        Decidí si la respuesta es válida o inválida según las reglas.
        Recordá devolver SOLO el JSON indicado, sin comentarios ni explicación adicional.
        """,
                letter.toString(),          // o letter.getValue()
                category.getName(),         // ajustá el método si se llama diferente
                trimmed
        );

        // 3) Llamar a Groq (vía LLMProvider)
        String rawResponse = provider.complete(SYSTEM_PROMPT, userPrompt);

        // 4) Limpiar la respuesta por si viene envuelta en ```json ... ```
        String jsonString = extractJson(rawResponse);

        // 5) Parsear JSON
        try {
            JsonObject obj = gson.fromJson(jsonString, JsonObject.class);

            String status = obj.get("status").getAsString();
            String reason = obj.has("reason") && !obj.get("reason").isJsonNull()
                    ? obj.get("reason").getAsString()
                    : "";

            boolean isValid = "VALID".equalsIgnoreCase(status);

            List<String> reasons = List.of(reason);

            // Mapear a tu modelo de dominio
            if (isValid) {
                return ValidationResult.valid(reasons);
            } else {
                return ValidationResult.invalid(reasons);
            }
        } catch (Exception e) {
            // Fallback defensivo en caso de que el modelo devuelva algo raro
            return ValidationResult.invalid(List.of("No se pudo interpretar la respuesta de la IA"));
        }
    }

    /**
     * Trata de extraer el JSON puro, en caso de que el modelo lo envuelva en un bloque de código.
     */
    private String extractJson(String raw) {
        if (raw == null) return "{}";
        String trimmed = raw.trim();

        // Caso típico: ```json { ... } ```
        if (trimmed.startsWith("```")) {
            int firstBrace = trimmed.indexOf('{');
            int lastBrace = trimmed.lastIndexOf('}');
            if (firstBrace >= 0 && lastBrace >= firstBrace) {
                return trimmed.substring(firstBrace, lastBrace + 1);
            }
        }

        return trimmed;
    }
}
