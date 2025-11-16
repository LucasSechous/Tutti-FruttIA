package com.example.tuttifruttia.domain.core;

import java.util.Collections;
import java.util.List;

public class ValidationResult {

    private final boolean ok;

    private final List<String> reasons;

    private ValidationResult(boolean ok, List<String> reasons) {
        this.ok = ok;
        this.reasons = reasons == null ? List.of() : List.copyOf(reasons);
    }

    public static ValidationResult success() {
        return new ValidationResult(true, List.of());
    }

    public static ValidationResult failure(List<String> reasons) {
        if (reasons == null || reasons.isEmpty()) {
            throw new IllegalArgumentException("La validacion debe de incluir al menos una razon");
        }
        return new ValidationResult(false, reasons);
    }

    public boolean isOk() {
        return ok;
    }

    public List<String> getReasons() {
        return Collections.unmodifiableList(reasons);
    }
}
