package com.example.tuttifruttia.domain.scoring;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.ScoreBoard;
import com.example.tuttifruttia.domain.core.ValidationResult;

import java.util.Map;

public interface ScoreCalculator {

    ScoreBoard compute(Map<Category, ValidationResult> results, PointsRule rule);
}
