package com.example.tuttifruttia.domain.persistence;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.ValidationResult;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryValidationLogRepository implements ValidationLogRepository {

    /**
     * Estructura en memoria:
     * roundId → mapa de validaciones
     */
    private final Map<UUID, Map<Category, ValidationResult>> logs = new ConcurrentHashMap<>();

    @Override
    public void save(UUID roundId, Map<Category, ValidationResult> details) {
        if (roundId == null || details == null) {
            throw new IllegalArgumentException("roundId and details must not be null");
        }
        logs.put(roundId, details);
    }

    // OPCIONAL: si querés poder leer logs más adelante
    public Map<Category, ValidationResult> get(UUID roundId) {
        return logs.get(roundId);
    }
}
