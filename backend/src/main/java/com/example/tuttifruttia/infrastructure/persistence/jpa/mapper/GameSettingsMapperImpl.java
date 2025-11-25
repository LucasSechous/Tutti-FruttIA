package com.example.tuttifruttia.infrastructure.persistence.jpa.mapper;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.GameSettings;
import com.example.tuttifruttia.domain.letter.Alphabet;
import com.example.tuttifruttia.domain.scoring.PointsRule;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.CategoryEntity;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.GameSettingsEntity;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.PointsRuleEmbeddable;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class GameSettingsMapperImpl implements GameSettingsMapper {

    @Override
    public GameSettingsEntity toEntity(GameSettings domain) {
        if (domain == null) {
            return null;
        }

        UUID id = domain.getId();
        int roundSeconds = domain.getRoundSeconds();

        PointsRule rule = domain.getPointsRule();
        PointsRuleEmbeddable emb =
                new PointsRuleEmbeddable(
                        rule.getPointsValid(),
                        rule.getPointsEmpty(),
                        rule.getPointsInvalid()
                );

        // Alphabet -> String (ej: "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ")
        String alphabetLetters = alphabetToString(domain.getAlphabet());

        GameSettingsEntity entity = new GameSettingsEntity(
                id,
                roundSeconds,
                emb,
                alphabetLetters
        );

        // Mapear categorías de dominio -> CategoryEntity
        for (Category cat : domain.getCategories()) {
            CategoryEntity catEntity = new CategoryEntity(
                    cat.getId(),
                    cat.getName(),
                    cat.isEnabled(),
                    entity  // seteamos el GameSettings dueño
            );
            entity.addCategory(catEntity);
        }

        return entity;
    }

    @Override
    public GameSettings toDomain(GameSettingsEntity entity) {
        if (entity == null) {
            return null;
        }

        UUID id = entity.getId();
        int roundSeconds = entity.getRoundSeconds();

        PointsRuleEmbeddable emb = entity.getPointsRule();
        PointsRule rule = new PointsRule(
                emb.getPointsValid(),
                emb.getPointsEmpty(),
                emb.getPointsInvalid()
        );

        Alphabet alphabet = stringToAlphabet(entity.getAlphabetLetters());

        List<Category> categories = entity.getCategories()
                .stream()
                .map(this::toDomainCategory)
                .collect(Collectors.toList());

        // Usa el constructor con UUID que ya le agregamos a GameSettings
        return new GameSettings(
                id,
                categories,
                roundSeconds,
                rule,
                alphabet
        );
    }

    // =======================
    //  HELPERS PRIVADOS
    // =======================

    private String alphabetToString(Alphabet alphabet) {
        Set<Character> letters = alphabet.getLetters();
        StringBuilder sb = new StringBuilder();
        for (Character c : letters) {
            sb.append(c);
        }
        return sb.toString();
    }

    private Alphabet stringToAlphabet(String lettersString) {
        Set<Character> set = new LinkedHashSet<>();
        for (char c : lettersString.toCharArray()) {
            set.add(c);
        }
        return new Alphabet(set);
    }

    private Category toDomainCategory(CategoryEntity entity) {
        return new Category(
                entity.getId(),
                entity.getName(),
                entity.isEnabled()
        );
    }
}
