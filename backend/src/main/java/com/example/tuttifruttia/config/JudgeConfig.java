package com.example.tuttifruttia.config;

import com.example.tuttifruttia.domain.ai.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JudgeConfig {

    private static final Logger log = LoggerFactory.getLogger(JudgeConfig.class);

    // Leemos la API key de una propiedad de Spring:
    // application.properties -> groq.api.key=.....
    //
    // El ": " (dos puntos) significa "si no está definida, usar cadena vacía"
    @Value("${groq.api-key}")
    private String groqApiKey;

    @Bean
    public AIJudge aiJudge() {

        // 1) Reglas básicas (siempre disponibles)
        RuleBasedJudge ruleBased = new RuleBasedJudge(3);

        // 2) Si no hay API key -> solo reglas
        if (groqApiKey == null || groqApiKey.isBlank()) {
            log.warn("groq.api.key no está configurada; se usará solo RuleBasedJudge");
            return ruleBased;
        }

        // 3) Si hay API key -> armamos Semantic + Hybrid
        log.info("groq.api.key configurada; creando HybridJudge (RuleBased + Semantic/Groq)");

        LLMProvider provider = new Groq(groqApiKey);
        SemanticJudge semantic = new SemanticJudge(provider);

        return new HybridJudge(ruleBased, semantic);
    }
}
