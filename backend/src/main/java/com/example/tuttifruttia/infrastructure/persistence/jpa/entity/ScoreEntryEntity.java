package com.example.tuttifruttia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "score_entry")
public class ScoreEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scoreboard_id", nullable = false)
    private ScoreBoardEntity scoreboard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    private RoundEntity round; // opcional pero útil para auditoría

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // podría mapear tu EntryStatus enum

    protected ScoreEntryEntity() {
        // requerido por JPA
    }

    public ScoreEntryEntity(ScoreBoardEntity scoreboard,
                            RoundEntity round,
                            CategoryEntity category,
                            int points,
                            String status) {
        this.scoreboard = scoreboard;
        this.round = round;
        this.category = category;
        this.points = points;
        this.status = status;
    }

    // === GETTERS & SETTERS ===

    public UUID getId() { return id; }

    public ScoreBoardEntity getScoreboard() { return scoreboard; }
    public void setScoreboard(ScoreBoardEntity scoreboard) { this.scoreboard = scoreboard; }

    public RoundEntity getRound() { return round; }
    public void setRound(RoundEntity round) { this.round = round; }

    public CategoryEntity getCategory() { return category; }
    public void setCategory(CategoryEntity category) { this.category = category; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
