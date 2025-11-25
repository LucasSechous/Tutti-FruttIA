package com.example.tuttifruttia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Rounds")
public class RoundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "round_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private SinglePlayerGameEntity game;

    @Column(name = "letter", nullable = false, length = 1)
    private String letter;

    @Column(name = "seconds_total", nullable = false)
    private int secondsTotal;

    @Column(name = "remaining_seconds", nullable = false)
    private int remainingSeconds;

    @Column(name = "status", nullable = false, length = 30)
    private String status; // más adelante podés mapear tu enum RoundStatus

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @OneToMany(
            mappedBy = "round",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AnswerEntity> answers = new ArrayList<>();

    protected RoundEntity() {
        // requerido por JPA
    }

    public RoundEntity(SinglePlayerGameEntity game,
                       String letter,
                       int secondsTotal,
                       int remainingSeconds,
                       String status) {
        this.game = game;
        this.letter = letter;
        this.secondsTotal = secondsTotal;
        this.remainingSeconds = remainingSeconds;
        this.status = status;
        this.startedAt = OffsetDateTime.now();
    }

    // === HELPERS ===

    public void addAnswer(AnswerEntity answer) {
        answers.add(answer);
        answer.setRound(this);
    }

    public void removeAnswer(AnswerEntity answer) {
        answers.remove(answer);
        answer.setRound(null);
    }

    // === GETTERS & SETTERS ===

    public UUID getId() { return id; }

    public SinglePlayerGameEntity getGame() { return game; }
    public void setGame(SinglePlayerGameEntity game) { this.game = game; }

    public String getLetter() { return letter; }
    public void setLetter(String letter) { this.letter = letter; }

    public int getSecondsTotal() { return secondsTotal; }
    public void setSecondsTotal(int secondsTotal) { this.secondsTotal = secondsTotal; }

    public int getRemainingSeconds() { return remainingSeconds; }
    public void setRemainingSeconds(int remainingSeconds) { this.remainingSeconds = remainingSeconds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }

    public OffsetDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(OffsetDateTime finishedAt) { this.finishedAt = finishedAt; }

    public List<AnswerEntity> getAnswers() { return answers; }
    public void setAnswers(List<AnswerEntity> answers) { this.answers = answers; }
}
