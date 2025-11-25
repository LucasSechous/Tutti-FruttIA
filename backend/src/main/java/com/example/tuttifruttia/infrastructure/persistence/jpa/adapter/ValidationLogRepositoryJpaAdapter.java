package com.example.tuttifruttia.infrastructure.persistence.jpa.adapter;

import com.example.tuttifruttia.domain.core.Category;
import com.example.tuttifruttia.domain.core.ValidationResult;
import com.example.tuttifruttia.domain.persistence.ValidationLogRepository;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.AnswerEntity;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.AnswerValidationLogEntity;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.CategoryEntity;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.RoundEntity;
import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.ValidationReasonEntity;
import com.example.tuttifruttia.infrastructure.persistence.jpa.repository.AnswerValidationLogJpaRepository;
import com.example.tuttifruttia.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.example.tuttifruttia.infrastructure.persistence.jpa.repository.RoundJpaRepository;
import com.example.tuttifruttia.infrastructure.persistence.jpa.repository.ValidationReasonJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class ValidationLogRepositoryJpaAdapter implements ValidationLogRepository {

    private final RoundJpaRepository roundJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final AnswerValidationLogJpaRepository logJpaRepository;
    private final ValidationReasonJpaRepository reasonJpaRepository;
    // Si más adelante querés asociar AnswerEntity concreta por ronda+categoría:
    // private final AnswerJpaRepository answerJpaRepository;

    public ValidationLogRepositoryJpaAdapter(RoundJpaRepository roundJpaRepository,
                                             CategoryJpaRepository categoryJpaRepository,
                                             AnswerValidationLogJpaRepository logJpaRepository,
                                             ValidationReasonJpaRepository reasonJpaRepository) {
        this.roundJpaRepository = roundJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
        this.logJpaRepository = logJpaRepository;
        this.reasonJpaRepository = reasonJpaRepository;
    }

    @Override
    @Transactional
    public void save(UUID roundId, Map<Category, ValidationResult> details) {

        // 1) Buscamos la Round en BD
        RoundEntity round = roundJpaRepository.findById(roundId)
                .orElseThrow(() -> new IllegalStateException("Round not found: " + roundId));

        // 2) Por cada categoría + resultado de validación, generamos un log
        for (Map.Entry<Category, ValidationResult> entry : details.entrySet()) {

            Category domainCategory = entry.getKey();
            ValidationResult validationResult = entry.getValue();

            UUID categoryId = domainCategory.getId();
            CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalStateException("Category not found: " + categoryId));

            // Por ahora no asociamos a una AnswerEntity concreta (se deja null).
            AnswerEntity answerEntity = null;

            // 3) Creamos el log de validación
            AnswerValidationLogEntity logEntity =
                    new AnswerValidationLogEntity(
                            round,
                            categoryEntity,
                            answerEntity,
                            validationResult.isOk(),
                            "LLM" // TODO: parametrizar según el tipo de juez (LLM / RULE_BASED, etc.)
                    );

            // 4) Creamos las razones (manteniendo el orden)
            AtomicInteger position = new AtomicInteger(0);
            for (String reasonText : validationResult.getReasons()) {
                ValidationReasonEntity reasonEntity =
                        new ValidationReasonEntity(
                                logEntity,
                                reasonText,
                                position.getAndIncrement()
                        );
                logEntity.addReason(reasonEntity);
            }

            // 5) Guardamos el log (y en cascada las reasons)
            logJpaRepository.save(logEntity);
        }
    }
}
