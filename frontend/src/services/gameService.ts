import api from "./api";
import type{
    Category,
    StartGameRequest,
    StartGameResponse,
    SubmitRoundRequest,
    RoundResult,
    GameSummary,
} from "../types/game";

export const gameService = {
    // aca obtenemos las categorias disponibles
    async getCategories(): Promise<Category[]> {
        const response = await api.get<Category[]>("/game/categories");
        return response.data;
    },


// Iniciar una nueva partida
  async startGame(payload: StartGameRequest): Promise<StartGameResponse> {
    const response = await api.post<StartGameResponse>("/game/start", payload);
    return response.data;
  },

  // Enviar respuestas de una ronda
  async submitRound(payload: SubmitRoundRequest): Promise<RoundResult> {
    const response = await api.post<RoundResult>("/game/round", payload);
    return response.data;
  },

  // Obtener resumen final de una partida
  async getGameSummary(gameId: string): Promise<GameSummary> {
    const response = await api.get<GameSummary>(`/game/${gameId}/summary`);
    return response.data;
  },
};
