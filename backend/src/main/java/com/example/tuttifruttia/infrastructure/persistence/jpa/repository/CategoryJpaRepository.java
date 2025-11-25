package com.example.tuttifruttia.infrastructure.persistence.jpa.repository;

import com.example.tuttifruttia.infrastructure.persistence.jpa.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
    // Ejemplo de método útil:
    // List<CategoryEntity> findByGameSettings_Id(UUID settingsId);
}
