package com.example.tuttifruttia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "game_settings")
public class GameSettingsEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;  // ID manejado por el dominio

    @Column(name = "round_seconds", nullable = false)
    private int roundSeconds;

    @Embedded
    private PointsRuleEmbeddable pointsRule;

    // === NUEVO: persistimos el alfabeto como un string =====
    @Column(name = "alphabet_letters", nullable = false, columnDefinition = "text")
    private String alphabetLetters;

    // Relación inversa con categorías
    @OneToMany(
            mappedBy = "gameSettings",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<CategoryEntity> categories = new HashSet<>();

    protected GameSettingsEntity() {
        // requerido por JPA
    }

    public GameSettingsEntity(UUID id,
                              int roundSeconds,
                              PointsRuleEmbeddable pointsRule,
                              String alphabetLetters) {
        this.id = id;


        ointsRule;
        this.alphabetLetters = alphabetLetters;
    }

    // === HELPERS ===
    public void addCategory(CategoryEntity category) {
        categories.add(category);
        category.setGameSettings(this);
    }

    public void removeCategory(CategoryEntity category) {
        categories.remove(category);
        category.setGameSettings(null);
    }

    // === GETTERS & SETTERS ===

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getRoundSeconds() {
        return roundSeconds;
    }

    public void setRoundSeconds(int roundSeconds) {
        this.roundSeconds = roundSeconds;
    }

    public PointsRuleEmbeddable getPointsRule() {
        return pointsRule;
    }

    public void setPointsRule(PointsRuleEmbeddable pointsRule) {
        this.pointsRule = pointsRule;
    }

    public String getAlphabetLetters() {
        return alphabetLetters;
    }

    public void setAlphabetLetters(String alphabetLetters) {
        this.alphabetLetters = alphabetLetters;
    }

    public Set<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryEntity> categories) {
        this.categories = categories;
    }
}
