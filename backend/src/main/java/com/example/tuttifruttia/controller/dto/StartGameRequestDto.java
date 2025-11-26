package com.example.tuttifruttia.controller.dto;

import java.util.List;

public class StartGameRequestDto {

    private String playerName;
    private List<Long> categoryIds;
    private Integer roundTimeSeconds;

    // 🔹 NUEVO: nombres de categorías solo para esta partida
    private List<String> customCategories;

    public StartGameRequestDto() {}

    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }
    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Integer getRoundTimeSeconds() {
        return roundTimeSeconds;
    }
    public void setRoundTimeSeconds(Integer roundTimeSeconds) {
        this.roundTimeSeconds = roundTimeSeconds;
    }

    // 🔹 getters/setters nuevos
    public List<String> getCustomCategories() {
        return customCategories;
    }
    public void setCustomCategories(List<String> customCategories) {
        this.customCategories = customCategories;
    }
}
