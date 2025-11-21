package com.example.tuttifruttia.controller.dto;

import java.util.List;

public class SubmitRoundRequestDto {

    private String gameId;
    private String letter;
    private List<AnswerDto> answers;

    public SubmitRoundRequestDto() {}

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getLetter() { return letter; }
    public void setLetter(String letter) { this.letter = letter; }

    public List<AnswerDto> getAnswers() { return answers; }
    public void setAnswers(List<AnswerDto> answers) { this.answers = answers; }

    // Sub-DTO
    public static class AnswerDto {
        private Long categoryId;
        private String value;

        public AnswerDto() {}

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}
