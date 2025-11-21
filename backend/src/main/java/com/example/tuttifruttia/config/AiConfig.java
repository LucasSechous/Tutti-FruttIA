package com.example.tuttifruttia.config;

import com.example.tuttifruttia.domain.ai.AIJudge;
import com.example.tuttifruttia.domain.ai.Groq;
import com.example.tuttifruttia.domain.ai.LLMProvider;
import com.example.tuttifruttia.domain.ai.SemanticJudge;
import org.springframework.context.annotation.Bean;

public class AiConfig {

    @Bean
    public LLMProvider llmProvider() {
        return Groq.fromEnv(); // lee GROQ_API_KEY
    }

    @Bean
    public AIJudge semanticJudge(LLMProvider llmProvider) {
        return new SemanticJudge(llmProvider);
    }


}
