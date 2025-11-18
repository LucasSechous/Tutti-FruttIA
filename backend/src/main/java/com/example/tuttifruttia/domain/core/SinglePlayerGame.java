package com.example.tuttifruttia.domain.core;

import com.example.tuttifruttia.domain.ai.AIJudge;
import com.example.tuttifruttia.domain.letter.LetterStrategy;
import com.example.tuttifruttia.domain.persistence.PersistenceFactory;
import com.example.tuttifruttia.domain.persistence.ValidationLogRepository;
import com.example.tuttifruttia.domain.scoring.PointsRule;
import com.example.tuttifruttia.domain.scoring.ScoreCalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SinglePlayerGame {

    // --- Identidad y estado principal del juego ---
    private final UUID id;
    private GameState state;
    private GameSettings settings;
    private Round currentRound;
    private final ScoreBoard scoreBoard;

    // --- Estrategias / dependencias (DIP) ---
    private final AIJudge judge;
    private final LetterStrategy letterStrat;
    private final ScoreCalculator scorer;
    private final PersistenceFactory persistence;

    public SinglePlayerGame(GameSettings settings,
                            AIJudge judge,
                            LetterStrategy letterStrat,
                            ScoreCalculator scorer,
                            PersistenceFactory persistence) {

        this.id = UUID.randomUUID();
        this.settings = Objects.requireNonNull(settings, "settings is required");
        this.settings.validate();

        this.judge = Objects.requireNonNull(judge, "judge is required");
        this.letterStrat = Objects.requireNonNull(letterStrat, "letterStrat is required");
        this.scorer = Objects.requireNonNull(scorer, "scorer is required");
        this.persistence = Objects.requireNonNull(persistence, "persistence is required");

        this.scoreBoard = new ScoreBoard();
        this.state = GameState.NOT_STARTED;
        this.currentRound = null;
    }

    // --- Getters básicos (por si los necesitamos más adelante) ---

    public UUID getId() {
        return id;
    }

    public GameState getState() {
        return state;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    // =========================================================
    // 1) start(config)
    // =========================================================

    public void start(GameSettings newSettings) {
        if (state != GameState.NOT_STARTED) {
            throw new IllegalStateException("Game already started");
        }
        this.settings = Objects.requireNonNull(newSettings, "settings is required");
        this.settings.validate();

        this.state = GameState.ONGOING;
        this.currentRound = null;
        // No creamos la ronda todavía; eso lo hará generateLetter().
        saveGame();
    }

    // =========================================================
    // 2) generateLetter(): Letter
    // =========================================================

    public Letter generateLetter() {
        ensureRunning();

        // Pedimos una letra a la estrategia actual
        Letter letter = letterStrat.nextLetter(settings.getAlphabet());

        // Creamos una nueva Round y la arrancamos
        Round round = new Round();
        round.start(letter, settings.getRoundSeconds());

        this.currentRound = round;
        saveGame();

        return letter;
    }

    // =========================================================
    // 3) submitAnswers(ans: AnswerSet): void
    // =========================================================

    public void submitAnswers(AnswerSet answers) {
        ensureRunning();

        if (currentRound == null) {
            throw new IllegalStateException("No active round to submit answers");
        }

        // 1) Asociamos las respuestas a la ronda actual
        //    (si quisieras ser más estricto, podrías clonar el AnswerSet)
        //    acá vamos a usar directamente el AnswerSet recibido.
        //    Para eso necesitamos un pequeño helper en Round:
        //    public void setAnswers(AnswerSet answers) { this.answers = answers; }
        currentRound.setAnswers(answers);

        // 2) Validamos cada respuesta con el AIJudge
        Map<Category, ValidationResult> results = new HashMap<>();
        for (Category category : settings.getCategories()) {
            Answer answer = answers.get(category); // puede ser null si no respondió
            String text = answer != null ? answer.getText() : null;

            ValidationResult vr = judge.validate(
                    currentRound.getLetter(),
                    category,
                    text
            );
            results.put(category, vr);
        }

        // 3) Log de validación (no nos importa la implementación concreta todavía)
        ValidationLogRepository logRepo = persistence.logRepo();
        logRepo.save(currentRound.getId(), results);

        // 4) Calculamos el puntaje de la ronda
        PointsRule rule = settings.getPointsRule();
        ScoreBoard roundBoard = scorer.compute(results, rule);

        // 5) Sumamos ese resultado al ScoreBoard global
        for (ScoreEntry entry : roundBoard.getEntries()) {
            this.scoreBoard.addEntry(entry);
        }

        // 6) Cerramos la ronda
        currentRound.finish();

        // 7) Persistimos el estado del juego
        saveGame();
    }

    // =========================================================
    // 4) giveUp(): void
    // =========================================================

    public void giveUp() {
        if (state == GameState.FINISHED || state == GameState.GIVEN_UP) {
            return; // ya está en un estado terminal compatible
        }
        this.state = GameState.GIVEN_UP;
        saveGame();
    }

    // =========================================================
    // 5) tuttiFrutti(): void
    // =========================================================

    public void tuttiFrutti() {
        // Para single player, lo interpretamos como:
        // "cerrar la ronda actual de forma anticipada"
        ensureRunning();

        if (currentRound == null) {
            throw new IllegalStateException("No active round for tutti frutti");
        }

        // De momento lo tratamos igual que un submit sin validar:
        // sólo cerramos la ronda; la lógica real se puede ajustar luego.
        currentRound.finish();
        saveGame();
    }

    // =========================================================
    // 6) closeByTimeout(): void
    // =========================================================

    public void closeByTimeout() {
        if (state != GameState.ONGOING) {
            return;
        }

        if (currentRound != null && currentRound.getStatus() == RoundStatus.ANSWERING) {
            // si el timer se venció, cerramos la ronda
            if (currentRound.isExpired()) {
                currentRound.finish();
            }
        }

        this.state = GameState.TIMEOUT;
        saveGame();
    }

    // =========================================================
    // Helpers privados
    // =========================================================

    private void ensureRunning() {
        if (state != GameState.ONGOING) {
            throw new IllegalStateException("Game is not running. Current state: " + state);
        }
    }

    private void saveGame() {
        persistence.gameRepo().save(this);
    }








}
