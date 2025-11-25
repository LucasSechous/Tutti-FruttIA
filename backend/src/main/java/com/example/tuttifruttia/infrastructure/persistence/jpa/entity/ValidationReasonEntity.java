package com.example.tuttifruttia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "validation_reason")
public class ValidationReasonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reason_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validation_log_id", nullable = false)
    private AnswerValidationLogEntity validationLog;

    @Column(name = "reason", nullable = false, columnDefinition = "text")
    private String reason;

    @Column(name = "position", nullable = false)
    private int position;

    protected ValidationReasonEntity() {
        // Requerido por JPA
    }

    public ValidationReasonEntity(AnswerValidationLogEntity validationLog,
                                  String reason,
                                  int position) {
        this.validationLog = validationLog;
        this.reason = reason;
        this.position = position;
    }

    // =====================
    // GETTERS / SETTERS
    // =====================

    public UUID getId() {
        return id;
    }

    public AnswerValidationLogEntity getValidationLog() {
        return validationLog;
    }

    public void setValidationLog(AnswerValidationLogEntity validationLog) {
        this.validationLog = validationLog;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
