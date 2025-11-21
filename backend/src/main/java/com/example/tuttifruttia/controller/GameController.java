package com.example.tuttifruttia.controller;

import com.example.tuttifruttia.controller.dto.*;
import com.example.tuttifruttia.service.GameAppService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://localhost:5173") // ajusta al puerto de tu front
public class GameController {

    private final GameAppService gameAppService;

    public GameController(GameAppService gameAppService) {
        this.gameAppService = gameAppService;
    }

    // 1) Obtener categorías
    @GetMapping("/categories")
    public List<CategoryDto> getCategories() {
        return gameAppService.getAvailableCategories();
    }

    // 2) Iniciar una partida
    @PostMapping("/game/start")
    public StartGameResponseDto startGame(@RequestBody StartGameRequestDto request) {
        return gameAppService.startGame(request);
    }

    // 3) Enviar respuestas de una ronda
    @PostMapping("/game/round")
    public RoundResultDto submitRound(@RequestBody SubmitRoundRequestDto request) {
        return gameAppService.processRound(request);
    }

    // 4) Obtener resumen final de la partida
    @GetMapping("/game/{gameId}/summary")
    public GameSummaryDto getSummary(@PathVariable String gameId) {
        return gameAppService.getGameSummary(gameId);
    }
}

