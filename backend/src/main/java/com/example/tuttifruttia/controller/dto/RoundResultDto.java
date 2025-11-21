package com.example.tuttifruttia.controller.dto;

import java.util.List;

public class RoundResultDto {

    private String gameId;
    private String letter;
    private List<AnswerResultDto> results;

    public RoundResultDto() {}

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getLetter() { return letter; }
    public void setLetter(String letter) { this.letter = letter; }

    public List<AnswerResultDto> getResults() { return results; }
    public void setResults(List<AnswerResultDto> results) { this.results = results; }

    // Sub-DTO
    public static class AnswerResultDto {
        private Long categoryId;
        private String value;
        private boolean valid;
        private int score;
        private String reason;

        public AnswerResultDto() {}

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
