package com.example.tuttifruttia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "single_player_game") // 👈 tabla en Supabase
public class SinglePlayerGameEntity {

    @Id
    @Column(name = "game_id", nullable = false, updatable = false)
    private UUID id; // ID lo controla el dominio

    @Column(name = "state", nullable = false, length = 30)
    private String state; // Podés mapear tu enum GameState como String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settings_id", nullable = false)
    private GameSettingsEntity settings;

    @OneToMany(
            mappedBy = "game",           // 👈 tiene que coincidir con el campo "game" en RoundEntity
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RoundEntity> rounds = new ArrayList<>();

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @OneToOne(
            mappedBy = "game",           // 👈 coincide con el campo "game" en ScoreBoardEntity
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ScoreBoardEntity scoreboard;

    protected SinglePlayerGameEntity() {
        // requerido por JPA
    }

    public SinglePlayerGameEntity(String state, GameSettingsEntity settings) {
        this.state = state;
        this.settings = settings;
        this.startedAt = OffsetDateTime.now();
    }

    // =========================
    //  HELPERS
    // =========================

    public void addRound(RoundEntity round) {
        rounds.add(round);
        round.setGame(this);
    }

    public void removeRound(RoundEntity round) {
        rounds.remove(round);
        round.setGame(null);
    }

    // =========================
    //  GETTERS & SETTERS
    // =========================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) { // lo usa el adapter para sincronizar ID dominio ↔ BD
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public GameSettingsEntity getSettings() {
        return settings;
    }

    public void setSettings(GameSettingsEntity settings) {
        this.settings = settings;
    }

    public List<RoundEntity> getRounds() {
        return rounds;
    }

    public void setRounds(List<RoundEntity> rounds) {
        this.rounds = rounds;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public OffsetDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(OffsetDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public ScoreBoardEntity getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(ScoreBoardEntity scoreboard) {
        this.scoreboard = scoreboard;
        if (scoreboard != null) {
            scoreboard.setGame(this);
        }
    }
}
