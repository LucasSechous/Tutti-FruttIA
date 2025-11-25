package com.example.tuttifruttia.controller.dto;

import java.util.List;

public class GameSummaryDto {

    private String gameId;
    private String playerName;
    private int totalScore;
    private int roundsPlayed;
    private List<RoundScoreDto> roundScores;

    public GameSummaryDto() {}

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public int getRoundsPlayed() { return roundsPlayed; }
    public void setRoundsPlayed(int roundsPlayed) { this.roundsPlayed = roundsPlayed; }

    public List<RoundScoreDto> getRoundScores() { return roundScores; }
    public void setRoundScores(List<RoundScoreDto> roundScores) { this.roundScores = roundScores; }

    // Sub-DTO
    public static class RoundScoreDto {
        private int roundNumber;
        private int score;

        public RoundScoreDto() {}

        public int getRoundNumber() { return roundNumber; }
        public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
    }
}
