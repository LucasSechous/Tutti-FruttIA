package com.example.tuttifruttia.domain.scoring;

public class PointsRule {

    private final int pointsValid;
    private final int pointsEmpty;
    private final int pointsInvalid;

    public PointsRule(int pointsValid, int pointsEmpty, int pointsInvalid) {
        this.pointsValid = pointsValid;
        this.pointsEmpty = pointsEmpty;
        this.pointsInvalid = pointsInvalid;
    }

    public int getPointsValid() {
        return pointsValid;
    }

    public int getPointsEmpty() {
        return pointsEmpty;
    }

    public int getPointsInvalid() {
        return pointsInvalid;
    }
}
