// src/types/game.ts

export type Letter = string; // "A", "B", "C", ...

// Categoría que devuelve el backend en /api/game/start
export interface Category {
  id: number;
  name: string;      // "Frutas", "Países", etc.
  activado: boolean; // tal como viene en la API
}

/** 🔹 De dónde viene la categoría usada en esta partida */
export type CategorySource = "base" | "custom";

/** 🔹 Config de categorías que se usan en UNA partida */
export interface GameCategoryConfig {
  id?: number;          // solo para las base
  name: string;
  source: CategorySource;
}

/* ============ /api/game/start ============ */

export interface StartGameRequest {
  playerName: string;
  categoryIds: number[];     // IDs de las categorías base seleccionadas
  customCategories?: string[]; // Nombres de categorías solo para esta partida
  roundTimeSeconds?: number; // opcional
}

export interface StartGameResponse {
  gameId: string;
  firstLetter: Letter;
  categories: Category[];
  roundTimeSeconds?: number;
}

/* ============ /api/game/round ============ */

export interface SubmitRoundRequest {
  gameId: string;
  letter: Letter;
  answers: {
    categoryId: number;
    value: string;   // 👈 importante: el backend espera "value"
  }[];
}

export interface SubmitRoundResponse {
  gameId: string;
  letter: Letter;
  results: {
    categoryId: number;
    value: string;
    valid: boolean;
    score: number;
    reason: string;
  }[];
}

/* ============ Modelos "lógicos" del juego (para otras pantallas, resúmenes, etc.) ============ */

export interface RoundAnswer {
  categoryId: number;
  categoryName: string;
  answer: string;
  isValid: boolean;
  score?: number;
}

export interface RoundResult {
  roundNumber: number;
  letter: Letter;
  answers: RoundAnswer[];
  totalScore: number;
}

export interface GameSummary {
  gameId: string;
  totalScore: number;
  rounds: RoundResult[];
}
