package com.example.tuttifruttia.controller.dto;

import java.util.List;

public class StartGameResponseDto {

    private String gameId;
    private String firstLetter;
    private List<CategoryDto> categories;
    private Integer roundTimeSeconds; // 👈 opcional pero muy útil

    public StartGameResponseDto() {}

    public String getGameId() {
        return gameId;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getFirstLetter() {
        return firstLetter;
    }
    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public List<CategoryDto> getCategories() {
        return categories;
    }
    public void setCategories(List<CategoryDto> categories) {
        this.categories = categories;
    }

    public Integer getRoundTimeSeconds() {
        return roundTimeSeconds;
    }
    public void setRoundTimeSeconds(Integer roundTimeSeconds) {
        this.roundTimeSeconds = roundTimeSeconds;
    }
}
