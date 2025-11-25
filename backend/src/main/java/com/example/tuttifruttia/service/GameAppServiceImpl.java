package com.example.tuttifruttia.service;

import com.example.tuttifruttia.controller.dto.*;
import com.example.tuttifruttia.domain.ai.AIJudge;
import com.example.tuttifruttia.domain.core.*;
import com.example.tuttifruttia.domain.letter.*;
import com.example.tuttifruttia.domain.scoring.*;
import com.example.tuttifruttia.domain.persistence.PersistenceFactory;
import com.example.tuttifruttia.domain.scoring.ScoreCalculator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameAppServiceImpl implements GameAppService {

    private final AIJudge judge;
    private final LetterStrategy letterStrategy;
    private final ScoreCalculator scorer;
    private final PersistenceFactory persistenceFactory;
    private final PointsRule pointsRule;
    private final Alphabet alphabet;

    private final Map<UUID, SinglePlayerGame> games = new HashMap<>();

    public GameAppServiceImpl(
            AIJudge judge,
            LetterStrategy letterStrategy,
            ScoreCalculator scorer,
            PersistenceFactory persistenceFactory,
            PointsRule pointsRule,
            Alphabet alphabet
    ) {
        this.judge = judge;
        this.letterStrategy = letterStrategy;
        this.scorer = scorer;
        this.persistenceFactory = persistenceFactory;
        this.pointsRule = pointsRule;
        this.alphabet = alphabet;
    }

    // ========================================================
    // GET /categories
    // ========================================================

    @Override
    public List<CategoryDto> getAvailableCategories() {
        // TODO: más adelante esto debería venir de dominio/persistencia
        return List.of(
                new CategoryDto(1L, "Frutas", true),
                new CategoryDto(2L, "Países",true),
                new CategoryDto(3L, "Animales",true),
                new CategoryDto(4L, "Colores",true)
        );
    }

    // ========================================================
    // POST /game/start
    // ========================================================

    @Override
    public StartGameResponseDto startGame(StartGameRequestDto request) {

        GameSettings settings = new GameSettings(
                getCategoriesFromDto(request.getCategoryIds()),
                request.getRoundTimeSeconds(),
                pointsRule,   // 👈 aquí va PointsRule
                alphabet      // 👈 aquí va Alphabet (instancia, no el tipo)
        );

        SinglePlayerGame game = new SinglePlayerGame(
                settings,
                judge,
                letterStrategy,
                scorer,
                persistenceFactory
        );

        game.start(settings);

        games.put(game.getId(), game);

        Letter firstLetter = game.generateLetter();

        StartGameResponseDto response = new StartGameResponseDto();
        response.setGameId(game.getId().toString());
        response.setFirstLetter(String.valueOf(firstLetter.getValue())); // char -> String

        // Usamos las categorías del request para el DTO
        List<CategoryDto> selectedCategories = getAvailableCategories().stream()
                .filter(c -> request.getCategoryIds().contains(c.getId()))
                .toList();

        response.setCategories(selectedCategories);

        return response;
    }

    // ========================================================
    // POST /game/round
    // ========================================================

    @Override
    public RoundResultDto processRound(SubmitRoundRequestDto request) {
        // 1) Recuperar juego
        UUID gameId = UUID.fromString(request.getGameId());
        SinglePlayerGame game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found: " + request.getGameId());
        }

        // 2) Construir AnswerSet
        AnswerSet answerSet = new AnswerSet();

        List<Category> categories = game.getSettings().getCategories();
        List<SubmitRoundRequestDto.AnswerDto> answersDto = request.getAnswers();

        for (int i = 0; i < answersDto.size(); i++) {
            SubmitRoundRequestDto.AnswerDto dto = answersDto.get(i);
            Category category = categories.get(i);

            // 👇 AHORA SÍ: PlayerAnswer (NO Answer)
            PlayerAnswer pAnswer = new PlayerAnswer(category, dto.getValue());
            answerSet.put(category, pAnswer);
        }

        // 3) Lógica de dominio
        game.submitAnswers(answerSet);

        // 4) Datos de la ronda
        Round round = game.getCurrentRound();
        Map<Category, ValidationResult> validations = round.getValidationResults();
        AnswerSet storedAnswers = round.getAnswers();

        // 5) DTO
        RoundResultDto resultDto = new RoundResultDto();
        resultDto.setGameId(request.getGameId());
        resultDto.setLetter(String.valueOf(round.getLetter().getValue()));

        List<RoundResultDto.AnswerResultDto> answerResults = new ArrayList<>();

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            SubmitRoundRequestDto.AnswerDto dto = answersDto.get(i);

            ValidationResult vr = validations.get(category);

            // 👇 Ahora recuperamos PlayerAnswer
            PlayerAnswer pAnswer = storedAnswers.get(category);

            RoundResultDto.AnswerResultDto ar = new RoundResultDto.AnswerResultDto();
            ar.setCategoryId(dto.getCategoryId());
            ar.setValue(pAnswer != null ? pAnswer.getText() : null);

            if (vr != null) {
                ar.setValid(vr.isOk());
                ar.setReason(String.join("; ", vr.getReasons()));
                ar.setScore(vr.isOk() ? 10 : 0);
            } else {
                ar.setValid(false);
                ar.setReason("Sin resultado de validación");
                ar.setScore(0);
            }

            answerResults.add(ar);
        }

        resultDto.setResults(answerResults);
        return resultDto;
    }



    // ========================================================
    // GET /game/{id}/summary
    // ========================================================

    @Override
    public GameSummaryDto getGameSummary(String gameId) {
        UUID id = UUID.fromString(gameId);

        SinglePlayerGame game = games.get(id);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        GameSummaryDto dto = new GameSummaryDto();
        dto.setGameId(gameId);
        dto.setPlayerName("Single Player"); // si luego lo pones en GameSettings, lo leemos de ahí
        dto.setTotalScore(game.getScoreBoard().total());
        dto.setRoundsPlayed(game.getScoreBoard().getEntries().size());
        dto.setRoundScores(new ArrayList<>()); // se puede completar más adelante

        return dto;
    }

    // ========================================================
// Helpers
// ========================================================

    private List<Category> getCategoriesFromDto(List<Long> ids) {
        // 🔒 Protección por si el front manda null o no manda el campo
        if (ids == null || ids.isEmpty()) {
            return List.of(); // lista vacía, sin romper
        }

        // Mapeamos los DTO que ya conocemos (los 4 fijos del getAvailableCategories)
        Map<Long, CategoryDto> dtoById = getAvailableCategories().stream()
                .collect(Collectors.toMap(
                        CategoryDto::getId,
                        c -> c
                ));

        List<Category> result = new ArrayList<>();

        for (Long id : ids) {
            CategoryDto dto = dtoById.get(id);
            if (dto != null) {
                // En el dominio usamos un UUID interno y las marcamos como activas
                Category category = new Category(
                        UUID.randomUUID(),
                        dto.getName(),
                        true
                );
                result.add(category);
            }
        }

        return result;
    }

}
