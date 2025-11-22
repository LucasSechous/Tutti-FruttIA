export type Letter = string; // A, B, C...

export interface Category{
    id: number;
    name : string;// paises, frutas...
}

export interface RoundAnswer{
    categoryId: number;
    categoryName: string;
    answer: string;
    isValid: boolean;
    score?: number;
}

export interface RoundResult{

    roundNumber: number;
    letter: Letter;
    answers: RoundAnswer[];
    totalScore: number;
}

export interface StartGameRequest {
  playerName: string;
  categoryIds: number[];        // 👈 nombre correcto
  roundTimeSeconds?: number;    // opcional, por si querés mandar tiempo
}

export interface StartGameResponse{
    gameId: string;
    firstLetter: Letter;
    categories: Category[];
}


export interface SubmitRoundRequest {
  gameId: string;
  roundNumber: number;
  letter: Letter;
  answers: {
    categoryId: number;
    answer: string;
  }[];
}

export interface GameSummary {
  gameId: string;
  totalScore: number;
  rounds: RoundResult[];
}