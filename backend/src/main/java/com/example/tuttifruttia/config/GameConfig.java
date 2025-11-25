package com.example.tuttifruttia.config;


import com.example.tuttifruttia.domain.letter.Alphabet;
import com.example.tuttifruttia.domain.scoring.PointsRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class GameConfig {

    @Bean
    public PointsRule pointsRule() {
        // Aquí definimos la regla de puntos “por defecto”
        // Ajustalo como quieras
        return new PointsRule(
                10,  // puntos por respuesta válida
                0,   // puntos por respuesta inválida
                0    // puntos por respuesta vacía
        );
    }

    @Bean
    public Alphabet alphabet() {
        // Alfabeto básico A–Z. Ajustalo si querés incluir Ñ u otras letras.
        return new Alphabet(Set.of(
                'A','B','C','D','E','F','G','H','I','J',
                'K','L','M','N','O','P','Q','R','S','T',
                'U','V','W','X','Y','Z'
        ));
    }
}
