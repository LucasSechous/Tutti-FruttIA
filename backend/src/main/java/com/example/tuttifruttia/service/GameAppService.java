package com.example.tuttifruttia.service;

import com.example.tuttifruttia.controller.dto.*;

import java.util.List;

public interface GameAppService {

    List<CategoryDto> getAvailableCategories();

    StartGameResponseDto startGame(StartGameRequestDto request);

    RoundResultDto processRound(SubmitRoundRequestDto request);

    GameSummaryDto getGameSummary(String gameId);
}
