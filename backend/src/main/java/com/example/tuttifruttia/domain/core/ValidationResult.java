package com.example.tuttifruttia.domain.core;

import java.util.Collections;
import java.util.List;

public class ValidationResult {

    private final boolean ok;
    private final EntryStatus status;
    private final List<String> reasons;

    public ValidationResult(boolean ok,EntryStatus status, List<String> reasons) {
        this.ok = ok;
        this.status = status;
        this.reasons = reasons == null ? List.of() : List.copyOf(reasons);
    }

    public static ValidationResult valid() {
        return new ValidationResult(true, EntryStatus.VALID, List.of());
    }

    public static ValidationResult valid(List<String> reasons) {
        return new ValidationResult(true, EntryStatus.VALID, reasons == null ? List.of() : List.copyOf(reasons));
    }

    public static ValidationResult empty(List<String> reasons) {
        return new ValidationResult(false, EntryStatus.EMPTY, reasons);
    }

    public static ValidationResult invalid(List<String> reasons) {
        if (reasons == null || reasons.isEmpty()) {
            throw new IllegalArgumentException("La validacion debe de incluir al menos una razon");
        }
        return new ValidationResult(false,EntryStatus.INVALID, reasons);
    }

    public boolean isOk() {
        return ok;
    }

    public EntryStatus getStatus() {
        return status;
    }

    public List<String> getReasons() {
        return Collections.unmodifiableList(reasons);
    }
}
