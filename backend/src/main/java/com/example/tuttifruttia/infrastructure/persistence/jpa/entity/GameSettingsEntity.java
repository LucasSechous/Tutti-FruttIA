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
    private UUID id;  // 👈 ahora el ID lo controla la app/dominio

    @Column(name = "round_seconds", nullable = false)
    private int roundSeconds;

    @Embedded
    private PointsRuleEmbeddable pointsRule;

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
                              PointsRuleEmbeddable pointsRule) {
        this.id = id;
        this.roundSeconds = roundSeconds;
        this.pointsRule = pointsRule;
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

    // opcional, por si querés setearlo después desde un mapper
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

    public Set<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryEntity> categories) {
        this.categories = categories;
    }
}
