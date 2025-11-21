package com.example.tuttifruttia.domain.ai;

public interface LLMProvider {

    /**
     * Llama al modelo de lenguaje con un system prompt y un user prompt
     * y devuelve el contenido de la respuesta del asistente.
     */
    String complete(String systemPrompt, String userPrompt);
}
