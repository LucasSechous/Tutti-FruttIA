package com.example.tuttifruttia.controller.dto;

import java.util.List;

public class StartGameResponseDto {

    private String gameId;
    private String firstLetter;
    private List<CategoryDTO> categories;

    public StartGameResponseDto() {}

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getFirstLetter() { return firstLetter; }
    public void setFirstLetter(String firstLetter) { this.firstLetter = firstLetter; }

    public List<CategoryDTO> getCategories() { return categories; }
    public void setCategories(List<CategoryDTO> categories) { this.categories = categories; }
}
