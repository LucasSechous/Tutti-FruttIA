package com.example.tuttifruttia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "answer")
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private RoundEntity round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(name = "text", columnDefinition = "text")
    private String text; // puede ser null si el jugador dejó vacío

    @Column(name = "valid", nullable = false)
    private boolean valid;

    // =====================
    //  CONSTRUCTORES
    // =====================

    protected AnswerEntity() {
        // requerido por JPA
    }

    public AnswerEntity(RoundEntity round,
                        CategoryEntity category,
                        String text,
                        boolean valid) {
        this.round = round;
        this.category = category;
        this.text = text;
        this.valid = valid;
    }

    // =====================
    //  GETTERS / SETTERS
    // =====================

    public UUID getId() { return id; }

    public RoundEntity getRound() { return round; }
    public void setRound(RoundEntity round) { this.round = round; }

    public CategoryEntity getCategory() { return category; }
    public void setCategory(CategoryEntity category) { this.category = category; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
}
