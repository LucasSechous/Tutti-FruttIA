package com.example.tuttifruttia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Categories")
public class CategoryEntity {

    @Id
    @Column(name = "category_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_settings_id", nullable = false)
    private GameSettingsEntity gameSettings;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    // =====================
    //  CONSTRUCTORES
    // =====================

    protected CategoryEntity() {
        // requerido por JPA
    }

    public CategoryEntity(UUID id,
                          String name,
                          boolean enabled,
                          GameSettingsEntity gameSettings) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.gameSettings = gameSettings;
    }

    // =====================
    //  GETTERS / SETTERS
    // =====================

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public GameSettingsEntity getGameSettings() { return gameSettings; }
    public void setGameSettings(GameSettingsEntity gameSettings) { this.gameSettings = gameSettings; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
