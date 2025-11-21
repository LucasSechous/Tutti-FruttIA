package com.example.tuttifruttia.domain.core;

import java.time.Duration;
import java.time.Instant;

public class CountdownTimer {

    private final int secondsTotal;
    private int remaining;
    private Instant startedAt;
    private boolean running;

    public CountdownTimer(int secondsTotal) {
        if (secondsTotal <= 0) {
            throw new IllegalArgumentException("secondsTotal must be > 0");
        }
        this.secondsTotal = secondsTotal;
        this.remaining = secondsTotal;
    }

    public void start() {
        if (running) {
            throw new IllegalStateException("Timer already running");
        }
        this.running = true;
        this.startedAt = Instant.now();
    }

    public void stop() {
        if (!running) {
            return;
        }
        updateRemaining();
        this.running = false;
    }

    public boolean expired() {
        if (running) {
            updateRemaining();
        }
        return remaining <= 0;
    }

    public int getSecondsTotal() {
        return secondsTotal;
    }

    public int getRemaining() {
        if (running) {
            updateRemaining();
        }
        return remaining;
    }

    private void updateRemaining() {
        if (startedAt == null) return;
        long elapsed = Duration.between(startedAt, Instant.now()).getSeconds();
        int newRemaining = (int) Math.max(0, secondsTotal - elapsed);
        this.remaining = newRemaining;
    }
}
