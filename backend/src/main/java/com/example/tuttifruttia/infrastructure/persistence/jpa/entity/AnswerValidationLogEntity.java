package com.example.tuttifruttia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "answerValidationLog")
public class AnswerValidationLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "log_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private RoundEntity round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private AnswerEntity answer; // puede ser null si igual querés loguear algo

    @Column(name = "ok", nullable = false)
    private boolean ok;

    @Column(name = "judge_type", length = 50)
    private String judgeType; // "RULE_BASED", "LLM", etc.

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(
            mappedBy = "validationLog",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ValidationReasonEntity> reasons = new ArrayList<>();

    protected AnswerValidationLogEntity() {
        // requerido por JPA
    }

    public AnswerValidationLogEntity(RoundEntity round,
                                     CategoryEntity category,
                                     AnswerEntity answer,
                                     boolean ok,
                                     String judgeType) {
        this.round = round;
        this.category = category;
        this.answer = answer;
        this.ok = ok;
        this.judgeType = judgeType;
        this.createdAt = OffsetDateTime.now();
    }

    // ============ GETTERS / SETTERS ============

    public UUID getId() { return id; }

    public RoundEntity getRound() { return round; }
    public void setRound(RoundEntity round) { this.round = round; }

    public CategoryEntity getCategory() { return category; }
    public void setCategory(CategoryEntity category) { this.category = category; }

    public AnswerEntity getAnswer() { return answer; }
    public void setAnswer(AnswerEntity answer) { this.answer = answer; }

    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }

    public String getJudgeType() { return judgeType; }
    public void setJudgeType(String judgeType) { this.judgeType = judgeType; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public List<ValidationReasonEntity> getReasons() { return reasons; }
    public void setReasons(List<ValidationReasonEntity> reasons) { this.reasons = reasons; }

    public void addReason(ValidationReasonEntity reason) {
        reasons.add(reason);
        reason.setValidationLog(this);
    }

    public void removeReason(ValidationReasonEntity reason) {
        reasons.remove(reason);
        reason.setValidationLog(null);
    }
}
