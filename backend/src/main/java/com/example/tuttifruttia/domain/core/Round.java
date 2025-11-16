package com.example.tuttifruttia.domain.core;

import java.util.UUID;

public class Round {

    private final UUID id;
    private Letter letter;
    private CountdownTimer timer;
    private AnswerSet answers;
    private RoundStatus status;

    public Round() {
        this.id = UUID.randomUUID();
        this.status = RoundStatus.WAITING_LETTER;
    }

    public void start(Letter letter, int seconds) {
        if (status != RoundStatus.WAITING_LETTER) {
            throw new IllegalStateException("Round already started");
        }
        if (letter == null) {
            throw new IllegalArgumentException("letter is required");
        }
        this.letter = letter;
        this.timer = new CountdownTimer(seconds);
        this.answers = new AnswerSet();
        this.timer.start();
        this.status = RoundStatus.ANSWERING;
    }

    public void setAnswers(AnswerSet answers) {
        this.answers = answers;
    }


    public void finish() {
        if (status != RoundStatus.ANSWERING) {
            throw new IllegalStateException("Round cannot be finished from state " + status);
        }
        if (timer != null) {
            timer.stop();
        }
        this.status = RoundStatus.FINISHED;
    }

    public boolean isExpired() {
        return timer != null && timer.expired();
    }

    public UUID getId() {
        return id;
    }

    public Letter getLetter() {
        return letter;
    }

    public CountdownTimer getTimer() {
        return timer;
    }

    public AnswerSet getAnswers() {
        return answers;
    }

    public RoundStatus getStatus() {
        return status;
    }
}
