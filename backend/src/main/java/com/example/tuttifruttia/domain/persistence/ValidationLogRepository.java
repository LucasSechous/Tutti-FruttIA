package com.example.tuttifruttia.domain.persistence;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.ValidationResult;

import java.util.Map;
import java.util.UUID;

public interface ValidationLogRepository {

    void save(UUID roundId, Map<Category, ValidationResult> details);

}
