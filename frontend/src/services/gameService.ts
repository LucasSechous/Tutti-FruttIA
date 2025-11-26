import api from "./api";
import type {
  Category,
  StartGameRequest,
  StartGameResponse,
  SubmitRoundRequest,
  SubmitRoundResponse,
  GameSummary,
  AlphabetResponse,
  UpdateAlphabetRequest,
} from "../types/game";

export const gameService = {
  // Obtener categorías disponibles
  async getCategories(): Promise<Category[]> {
    const response = await api.get<Category[]>("/game/categories");
    return response.data;
  },

  // Obtener letras actualmente habilitadas
  async getLetters(): Promise<AlphabetResponse> {
    const response = await api.get<AlphabetResponse>("/game/letters");
    return response.data;
  },

  // Actualizar letras habilitadas
  async updateLetters(payload: UpdateAlphabetRequest): Promise<void> {
    await api.post("/game/letters", payload);
  },

  // Iniciar una nueva partida
  async startGame(payload: StartGameRequest): Promise<StartGameResponse> {
    const response = await api.post<StartGameResponse>("/game/start", payload);
    return response.data;
  },

  // Enviar respuestas de una ronda
  async submitRound(payload: SubmitRoundRequest): Promise<SubmitRoundResponse> {
    const response = await api.post<SubmitRoundResponse>("/game/round", payload);
    return response.data;
  },

  // Obtener resumen final de una partida
  async getGameSummary(gameId: string): Promise<GameSummary> {
    const response = await api.get<GameSummary>(`/game/${gameId}/summary`);
    return response.data;
  },
};
