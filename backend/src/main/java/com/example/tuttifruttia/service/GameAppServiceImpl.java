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
    private Alphabet currentAlphabet;


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
        this.currentAlphabet = alphabet; // inicialmente es el alfabeto completo
    }


    // ========================================================
    // GET /letters
    // ========================================================

    @Override
    public AlphabetDto getAlphabet() {
        return new AlphabetDto(currentAlphabet.getLetters());
    }


    // ========================================================
    // POST /letters
    // ========================================================

    @Override
    public void updateAlphabet(UpdateAlphabetRequestDto request) {

        if (request.getEnabledLetters() == null || request.getEnabledLetters().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una letra");
        }

        // Creamos un nuevo alfabeto según las letras habilitadas por el usuario
        this.currentAlphabet = new Alphabet(request.getEnabledLetters());
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
            // 🔹 ahora pasamos también las custom
            getCategoriesFromDto(request.getCategoryIds(), request.getCustomCategories()),
            request.getRoundTimeSeconds(),
            pointsRule,
            alphabet
    );
        GameSettings settings = new GameSettings(
                getCategoriesFromDto(request.getCategoryIds()),
                request.getRoundTimeSeconds(),
                pointsRule,   // 👈 aquí va PointsRule
                currentAlphabet  // 👈 AHORA usa el alfabeto configurado por el usuario
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

    // 🔹 armamos las categorías que el front verá (base + custom)
    List<CategoryDto> selectedCategories = buildSelectedCategoriesDto(
            request.getCategoryIds(),
            request.getCustomCategories()
    );
    response.setCategories(selectedCategories);

    // 🔹 devolvemos también el tiempo configurado
    response.setRoundTimeSeconds(request.getRoundTimeSeconds());

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

   private List<Category> getCategoriesFromDto(List<Long> ids, List<String> customNames) {
    // DTOs conocidos (las 4 fijas por ahora)
    Map<Long, CategoryDto> dtoById = getAvailableCategories().stream()
            .collect(Collectors.toMap(CategoryDto::getId, c -> c));

    List<Category> result = new ArrayList<>();

    // 1) categorías base seleccionadas por id
    if (ids != null) {
        for (Long id : ids) {
            CategoryDto dto = dtoById.get(id);
            if (dto != null) {
                result.add(new Category(
                        UUID.randomUUID(),   // id interno de dominio
                        dto.getName(),
                        true
                ));
            }
        }
    }

    // 2) categorías custom solo para esta partida
    if (customNames != null) {
        for (String name : customNames) {
            if (name == null || name.isBlank()) continue;
            result.add(new Category(
                    UUID.randomUUID(),
                    name,
                    true
            ));
        }
    }

    return result;
}

// 🔹 Construye la lista de CategoryDto que verá el front (con ids numéricos)
private List<CategoryDto> buildSelectedCategoriesDto(List<Long> baseIds, List<String> customNames) {
    List<CategoryDto> selected = new ArrayList<>();

    // base
    Map<Long, CategoryDto> dtoById = getAvailableCategories().stream()
            .collect(Collectors.toMap(CategoryDto::getId, c -> c));

    if (baseIds != null) {
        for (Long id : baseIds) {
            CategoryDto dto = dtoById.get(id);
            if (dto != null) {
                selected.add(new CategoryDto(dto.getId(), dto.getName(), true));
            }
        }
    }

    // custom: ids "falsos" empezando en 1000
    if (customNames != null) {
        long fakeIdBase = 1000L;
        int i = 0;
        for (String name : customNames) {
            if (name == null || name.isBlank()) continue;
            selected.add(new CategoryDto(fakeIdBase + i, name, true));
            i++;
        }
    }

    return selected;
}

}
