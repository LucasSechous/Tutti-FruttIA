package com.example.tuttifruttia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "scoreboard")  // 👈 nombre de la tabla en Supabase
public class ScoreBoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "scoreboard_id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false, unique = true)
    private SinglePlayerGameEntity game;   // 👈 coincide con mappedBy = "game" en SinglePlayerGameEntity

    @OneToMany(
            mappedBy = "scoreboard",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ScoreEntryEntity> entries = new ArrayList<>();

    @Column(name = "total_points", nullable = false)
    private int totalPoints;

    protected ScoreBoardEntity() {
        // requerido por JPA
    }

    public ScoreBoardEntity(SinglePlayerGameEntity game) {
        this.game = game;
        this.totalPoints = 0;
    }

    // === HELPERS ===
    public void addEntry(ScoreEntryEntity entry) {
        entries.add(entry);
        entry.setScoreboard(this);
        totalPoints += entry.getPoints();
    }

    public void removeEntry(ScoreEntryEntity entry) {
        entries.remove(entry);
        entry.setScoreboard(null);
        totalPoints -= entry.getPoints();
    }

    // === GETTERS & SETTERS ===

    public UUID getId() { return id; }

    public SinglePlayerGameEntity getGame() { return game; }
    public void setGame(SinglePlayerGameEntity game) { this.game = game; }

    public List<ScoreEntryEntity> getEntries() { return entries; }
    public void setEntries(List<ScoreEntryEntity> entries) { this.entries = entries; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
}
