package com.example.tuttifruttia.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PointsRuleEmbeddable {

    @Column(name = "points_valid", nullable = false)
    private int pointsValid;

    @Column(name = "points_empty", nullable = false)
    private int pointsEmpty;

    @Column(name = "points_invalid", nullable = false)
    private int pointsInvalid;

    protected PointsRuleEmbeddable() {
        // requerido por JPA
    }

    public PointsRuleEmbeddable(int pointsValid, int pointsEmpty, int pointsInvalid) {
        this.pointsValid = pointsValid;
        this.pointsEmpty = pointsEmpty;
        this.pointsInvalid = pointsInvalid;
    }

    public int getPointsValid() { return pointsValid; }
    public void setPointsValid(int pointsValid) { this.pointsValid = pointsValid; }

    public int getPointsEmpty() { return pointsEmpty; }
    public void setPointsEmpty(int pointsEmpty) { this.pointsEmpty = pointsEmpty; }

    public int getPointsInvalid() { return pointsInvalid; }
    public void setPointsInvalid(int pointsInvalid) { this.pointsInvalid = pointsInvalid; }
}
