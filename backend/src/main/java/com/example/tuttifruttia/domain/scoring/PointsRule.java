package com.example.tuttifruttia.domain.scoring;

public class PointsRule {

    private final int pointsValid;
    private final int pointsInvalid;
    private final int pointsEmpty;


    public PointsRule(int pointsValid, int pointsInvalid, int pointsEmpty) {
        this.pointsValid = pointsValid;
        this.pointsInvalid = pointsInvalid;
        this.pointsEmpty = pointsEmpty;
    }

    public int getPointsValid() {
        return pointsValid;
    }

    public int getPointsInvalid() {
        return pointsInvalid;
    }

    public int getPointsEmpty() {
        return pointsEmpty;
    }
}
