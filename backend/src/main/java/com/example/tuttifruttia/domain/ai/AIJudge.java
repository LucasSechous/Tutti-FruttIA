package com.example.tuttifruttia.domain.ai;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.Letter;
import com.example.tuttifruttia.domain.core.ValidationResult;

public interface AIJudge {

    ValidationResult validate(Letter letter, Category category, String text);
}
