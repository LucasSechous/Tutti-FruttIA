package com.example.tuttifruttia.domain.core;

import java.util.UUID;

public class Category {

    private final UUID id;
    private final String name;
    private boolean enabled;

    public Category(UUID id, String name, boolean enabled) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.name = name;
        this.enabled = enabled;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }
}
