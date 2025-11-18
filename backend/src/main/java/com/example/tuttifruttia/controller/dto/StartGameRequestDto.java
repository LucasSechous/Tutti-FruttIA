package com.example.tuttifruttia.controller.dto;

import java.util.List;

public class StartGameRequestDto {

    private String playerName;
    private List<Long> categoryIds;
    private int roundTimeSeconds;

    public StartGameRequestDto() {}

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public List<Long> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<Long> categoryIds) { this.categoryIds = categoryIds; }

    public int getRoundTimeSeconds() { return roundTimeSeconds; }
    public void setRoundTimeSeconds(int roundTimeSeconds) { this.roundTimeSeconds = roundTimeSeconds; }
}
