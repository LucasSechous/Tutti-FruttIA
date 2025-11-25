package com.example.tuttifruttia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
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

    public CategoryEntity(GameSettingsEntity gameSettings, String name, boolean enabled) {
        this.gameSettings = gameSettings;
        this.name = name;
        this.enabled = enabled;
    }

    // =====================
    //  GETTERS / SETTERS
    // =====================

    public UUID getId() { return id; }

    public GameSettingsEntity getGameSettings() { return gameSettings; }
    public void setGameSettings(GameSettingsEntity gameSettings) { this.gameSettings = gameSettings; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
