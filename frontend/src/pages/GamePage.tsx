import { useEffect, useState } from "react";
import "../styles/game.css"; // mismo CSS que ya se usaba para botones, inputs, etc.
import { Link } from "react-router-dom";
import { gameService } from "../services/gameService";
import type { StartGameRequest } from "../types/game";

const CATEGORIES = [
  { key: "name", label: "Name" },
  { key: "animal", label: "Animal" },
  { key: "country", label: "Country" },
  { key: "object", label: "Object" },
] as const;

type CategoryKey = (typeof CATEGORIES)[number]["key"];
type ValidationState = "correct" | "incorrect" | null;

const INITIAL_ANSWERS: Record<CategoryKey, string> = {
  name: "",
  animal: "",
  country: "",
  object: "",
};

const INITIAL_VALIDATION: Record<CategoryKey, ValidationState> = {
  name: null,
  animal: null,
  country: null,
  object: null,
};

// IDs de categorías del backend (Frutas, Países, Animales, Colores)
// El orden coincide con CATEGORIES
const BACKEND_CATEGORY_IDS = [1, 2, 3, 4] as const;

// Tipos de la respuesta del backend /game/round
type BackendAnswerResult = {
  categoryId: number;
  value: string;
  valid: boolean;
  score: number;
  reason: string;
};

type BackendRoundResult = {
  gameId: string;
  letter: string;
  results: BackendAnswerResult[];
};

function GamePage() {
  const [currentLetter, setCurrentLetter] = useState<string>("?");
  const [score, setScore] = useState<number>(0);
  const [timer, setTimer] = useState<number>(60);
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [answers, setAnswers] = useState<Record<CategoryKey, string>>(INITIAL_ANSWERS);
  const [validation, setValidation] =
    useState<Record<CategoryKey, ValidationState>>(INITIAL_VALIDATION);
  const [aiMessage, setAiMessage] = useState<string>(
    "Ready to play? Click Start when you're ready!"
  );
  const [showAiMessage, setShowAiMessage] = useState<boolean>(true);

  // gameId que devuelve el backend en /game/start
  const [gameId, setGameId] = useState<string | null>(null);

  // ⏱ efecto del timer
  useEffect(() => {
    if (!isRunning) return;

    const id = setInterval(() => {
      setTimer((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(id);
  }, [isRunning]);

  // Cuando el timer llega a 0, frenamos y enviamos la ronda
  useEffect(() => {
    if (isRunning && timer <= 0) {
      stopGame();
    }
  }, [timer, isRunning]);

  const handleChange = (key: CategoryKey, value: string) => {
    setAnswers((prev) => ({ ...prev, [key]: value }));
  };

  const resetRoundState = () => {
    setAnswers(INITIAL_ANSWERS);
    setValidation(INITIAL_VALIDATION);
    setTimer(60);
    setCurrentLetter("?");
    setIsRunning(false);
    setAiMessage("Ready to play? Click Start when you're ready!");
    setShowAiMessage(true);
  };

  // 🔹 Pide letra y gameId al backend (/api/game/start)
  const fetchLetterFromApi = async (): Promise<string | null> => {
    try {
      const payload: StartGameRequest = {
        playerName: "Player 1", // de momento fijo
        categoryIds: [...BACKEND_CATEGORY_IDS],
        roundTimeSeconds: 60,
      };

      const resp = await gameService.startGame(payload);
      setGameId(resp.gameId);

      const letter = resp.firstLetter.trim().toUpperCase();
      setCurrentLetter(letter);
      console.log("Letra desde API:", letter);
      return letter;
    } catch (error) {
      console.error("Error al iniciar la partida en el backend:", error);
      setAiMessage("There was an error starting the game on the server.");
      setShowAiMessage(true);
      return null;
    }
  };

  // Start → resetea y pide letra al backend
  const startGame = async () => {
    if (isRunning) return;

    setAnswers(INITIAL_ANSWERS);
    setValidation(INITIAL_VALIDATION);
    setTimer(60);
    setIsRunning(true);
    setShowAiMessage(true);
    setAiMessage("Getting a letter from the server...");

    const letter = await fetchLetterFromApi();
    if (!letter) {
      setIsRunning(false);
      return;
    }

    setAiMessage(`Quick! Find words starting with ${letter}!`);
  };

  // Submit → detiene el juego y envía las respuestas al backend
  const submitAnswers = async () => {
    if (!isRunning) return;
    setIsRunning(false);

    if (!gameId) {
      console.warn("No hay gameId, no se puede enviar la ronda al backend");
      return;
    }

    const payload = {
      gameId: gameId,
      letter: currentLetter,
      answers: CATEGORIES.map(({ key }, index) => ({
        categoryId: BACKEND_CATEGORY_IDS[index],
        value: answers[key],
      })),
    };

    try {
      const resp = await fetch("http://localhost:8080/api/game/round", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!resp.ok) {
        throw new Error(`HTTP ${resp.status}`);
      }

      const data: BackendRoundResult = await resp.json();
      console.log("Resultado de la ronda en el backend:", data);

      // Mapear resultado del backend → UI
      const idToKey: Record<number, CategoryKey> = {
        1: "name",
        2: "animal",
        3: "country",
        4: "object",
      };

      const newValidation: Record<CategoryKey, ValidationState> = {
        ...INITIAL_VALIDATION,
      };
      const newAnswers: Record<CategoryKey, string> = { ...answers };

      let roundScore = 0;

      data.results.forEach((r) => {
        const key = idToKey[r.categoryId];
        if (!key) return;

        newAnswers[key] = r.value ?? "";
        newValidation[key] = r.valid ? "correct" : "incorrect";
        roundScore += r.score ?? 0;
      });

      setAnswers(newAnswers);
      setValidation(newValidation);
      setScore((prev) => prev + roundScore);

      setAiMessage(`Round finished! You scored ${roundScore} points this round.`);
      setShowAiMessage(true);

      // si querés que se resetee sola después de unos segundos:
      setTimeout(() => {
        resetRoundState();
      }, 4000);
    } catch (error) {
      console.error("Error enviando la ronda al backend:", error);
      setAiMessage("There was an error sending your answers to the server.");
      setShowAiMessage(true);
    }
  };

  // Stop → simplemente reutiliza submitAnswers
  const stopGame = () => {
    if (!isRunning) return;
    submitAnswers();
  };

  const giveUp = () => {
    if (!isRunning) return;

    setIsRunning(false);
    setShowAiMessage(true);
    setAiMessage("No worries! Try again with a new letter.");

    setTimeout(() => {
      resetRoundState();
    }, 2000);
  };

  return (
    <div
      className="w-full font-sans"
      style={{ background: "linear-gradient(135deg, #E63946 0%, #C1121F 100%)" }}
    >
      <div className="container mx-auto px-4 py-8">
        {/* Botón Home (de momento hace un link a la raíz) */}
        <Link
          to="/"
          className="absolute top-4 left-4 bg-white p-2 rounded-full shadow-md hover:bg-gray-100 transition-colors"
        >
          <span className="text-primary-600 font-bold">⟵</span>
        </Link>

        {/* Game Area */}
        <div className="max-w-3xl mx-auto bg-white rounded-2xl shadow-xl overflow-hidden">
          {/* Letter Display */}
          <div className="bg-primary-100 py-6 px-4 flex items-center justify-center gap-8">
            <div className="inline-block bg-white rounded-full w-32 h-32 flex items-center justify-center shadow-lg">
              <span className="text-7xl font-bold text-primary-600 animate-pulse">
                {currentLetter}
              </span>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold text-white">
              <span className="text-secondary-600">Tutti-</span>
              <span className="text-primary-600">FruttIA</span>
            </h1>
          </div>

          {/* Timer & Score */}
          <div className="bg-primary-100 px-6 py-4 flex justify-between items-center">
            <div className="inline-block bg-white rounded-full px-4 py-1">
              <span className="text-primary-800 font-bold">Score:</span>
              <span className="text-primary-600 font-bold text-xl ml-1">
                {score}
              </span>
            </div>
            <div className="text-primary-800 font-medium flex items-center gap-2">
              <span>⏱</span>
              <span>Time left:</span>
              <div
                className={
                  "text-2xl font-bold " +
                  (timer <= 10 ? "text-red-500 animate-pulse" : "text-primary-600")
                }
              >
                {timer}
              </div>
            </div>
          </div>

          {/* Game Controls */}
          <div className="p-6">
            <div className="flex flex-wrap gap-4 justify-center mb-8">
              <button
                onClick={startGame}
                disabled={isRunning}
                className="btn-primary"
              >
                <span className="mr-2">▶</span> Start
              </button>

              <button
                onClick={stopGame}
                disabled={!isRunning}
                className="btn-secondary"
              >
                <span className="mr-2">■</span> Stop
              </button>

              <button
                onClick={giveUp}
                disabled={!isRunning}
                className="btn-outline"
              >
                <span className="mr-2">✖</span> Give Up
              </button>
            </div>

            {/* Categories */}
            <div className="pb-8">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {CATEGORIES.map(({ key, label }) => (
                  <div key={key} className="category-item flex items-center">
                    <label className="category-label w-1/3">{label}</label>
                    <div className="flex-1 flex items-center">
                      <input
                        type="text"
                        className="category-input"
                        value={answers[key]}
                        onChange={(e) => handleChange(key, e.target.value)}
                        disabled={!isRunning}
                      />
                      <span
                        className={
                          "validation-icon ml-2 " +
                          (validation[key] === "correct"
                            ? "correct"
                            : validation[key] === "incorrect"
                            ? "incorrect"
                            : "hidden")
                        }
                      >
                        {validation[key] === "correct" && "✓"}
                        {validation[key] === "incorrect" && "✗"}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Game Controls */}
            <div className="p-6">
              <div className="flex flex-wrap gap-3 justify-center mb-3">
                <button
                  onClick={submitAnswers}
                  disabled={!isRunning}
                  className="btn-secondary"
                >
                  <span className="mr-2">✔</span> Submit
                </button>
              </div>
            </div>

            {/* AI Message */}
            {showAiMessage && (
              <div className="mt-8 p-4 bg-secondary-100 rounded-lg text-primary-800">
                <div className="flex items-start">
                  <div className="bg-primary-500 rounded-full p-2 mr-4">
                    <span className="text-white">🤖</span>
                  </div>
                  <div>
                    <p className="font-medium">AI Assistant:</p>
                    <p className="italic">{aiMessage}</p>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default GamePage;
