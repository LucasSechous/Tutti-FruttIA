package com.example.tuttifruttia;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.GameSettings;
import com.example.tuttifruttia.domain.letter.Alphabet;
import com.example.tuttifruttia.domain.persistence.GameSettingsRepository;
import com.example.tuttifruttia.domain.scoring.PointsRule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.tuttifruttia.infrastructure.persistence.jpa.repository")
@EntityScan(basePackages = "com.example.tuttifruttia.infrastructure.persistence.jpa.entity")
public class TuttiFruttiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TuttiFruttiaApplication.class, args);
    }

    /**
     * Smoke test para verificar que GameSettings -> Mapper -> JPA -> BD funciona bien.
     * Se ejecuta una sola vez al arrancar la app.
     */
    @Bean
    CommandLineRunner smokeTestSettings(GameSettingsRepository settingsRepository) {
        return args -> {
            System.out.println("\n========== SMOKE TEST: GameSettings ==========");

            // 1) Alphabet simple (ABCDE)
            Set<Character> letters = new LinkedHashSet<>();
            for (char c : "ABCDE".toCharArray()) {
                letters.add(c);
            }
            Alphabet alphabet = new Alphabet(letters);

            // 2) Reglas de puntaje
            PointsRule pointsRule = new PointsRule(10, 0, -5);

            // 3) Categorías de ejemplo
            Category c1 = new Category(UUID.randomUUID(), "Nombre", true);
            Category c2 = new Category(UUID.randomUUID(), "Animal", true);

            // 4) GameSettings de dominio
            GameSettings settings = new GameSettings(
                    List.of(c1, c2),
                    60,
                    pointsRule,
                    alphabet
            );

            // 5) Guardar en BD
            settingsRepository.save(settings);
            System.out.println("Guardado GameSettings con id: " + settings.getId());

            // 6) Leer desde BD
            GameSettings loaded = settingsRepository.findById(settings.getId())
                    .orElseThrow(() -> new IllegalStateException("NO se encontró luego de guardar"));

            System.out.println("Leído desde BD:");
            System.out.println(" - ID:           " + loaded.getId());
            System.out.println(" - RoundSeconds: " + loaded.getRoundSeconds());
            System.out.println(" - Categorías:   " + loaded.getCategories().size());

            // 7) Cantidad total
            int total = settingsRepository.findAll().size();
            System.out.println("Total de GameSettings en BD: " + total);

            System.out.println("========== SMOKE TEST COMPLETADO ==========\n");
        };
    }
}
