import { useEffect, useState } from "react";
import "../styles/game.css";
import { Link, useLocation } from "react-router-dom";
import api from "../services/api";
import type {
  StartGameRequest,
  StartGameResponse,
  SubmitRoundRequest,
  SubmitRoundResponse,
  Category,
} from "../types/game";

type ValidationState = "correct" | "incorrect" | null;

interface GameConfigFromHome {
  playerName: string;
  categoryIds: number[];
  customCategories: string[];
  roundTimeSeconds: number;
}

function GamePage() {
  const location = useLocation();
  const configFromHome = location.state as GameConfigFromHome | undefined;

  const defaultRoundTime = configFromHome?.roundTimeSeconds ?? 60;

  const [currentLetter, setCurrentLetter] = useState<string>("?");
  const [score, setScore] = useState<number>(0);
  const [timer, setTimer] = useState<number>(defaultRoundTime);
  const [initialRoundTime, setInitialRoundTime] =
    useState<number>(defaultRoundTime);
  const [isRunning, setIsRunning] = useState<boolean>(false);

  const [categories, setCategories] = useState<Category[]>([]);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [validation, setValidation] = useState<
    Record<number, ValidationState>
  >({});

  const [aiMessage, setAiMessage] = useState<string>(
    "Ready to play? Click Start when you're ready!"
  );
  const [showAiMessage, setShowAiMessage] = useState<boolean>(true);

  const [gameId, setGameId] = useState<string | null>(null);

  // helpers para limpiar respuestas/validaciones según categorías actuales
  const buildEmptyAnswers = (cats: Category[]): Record<number, string> => {
    const obj: Record<number, string> = {};
    cats.forEach((c) => {
      obj[c.id] = "";
    });
    return obj;
  };

  const buildEmptyValidation = (
    cats: Category[]
  ): Record<number, ValidationState> => {
    const obj: Record<number, ValidationState> = {};
    cats.forEach((c) => {
      obj[c.id] = null;
    });
    return obj;
  };

  // efecto del timer
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

  const handleChange = (categoryId: number, value: string) => {
    setAnswers((prev) => ({ ...prev, [categoryId]: value }));
  };

  const resetRoundState = () => {
    setAnswers(buildEmptyAnswers(categories));
    setValidation(buildEmptyValidation(categories));
    setTimer(initialRoundTime);
    setCurrentLetter("?");
    setIsRunning(false);
    setAiMessage("Ready to play? Click Start when you're ready!");
    setShowAiMessage(true);
    // no reseteamos el score para mantener el total en el front
  };

  // 🔹 Llamada a /api/game/start usando la config del Home
  const fetchLetterFromApi = async (): Promise<string | null> => {
    if (!configFromHome) {
      setAiMessage("No game configuration found. Go back to the home page.");
      setShowAiMessage(true);
      return null;
    }

    const payload: StartGameRequest = {
      playerName: configFromHome.playerName,
      categoryIds: configFromHome.categoryIds,
      customCategories: configFromHome.customCategories,
      roundTimeSeconds: configFromHome.roundTimeSeconds,
    };

    try {
      const { data } = await api.post<StartGameResponse>(
        "/game/start",
        payload
      );

      setGameId(data.gameId);
      const letter = data.firstLetter.trim().toUpperCase();
      setCurrentLetter(letter);

      const cats = data.categories ?? [];
      setCategories(cats);
      setAnswers(buildEmptyAnswers(cats));
      setValidation(buildEmptyValidation(cats));

      const rt =
        data.roundTimeSeconds ?? configFromHome.roundTimeSeconds ?? 60;
      setInitialRoundTime(rt);
      setTimer(rt);

      console.log("Letter from API:", letter, "categories:", cats);

      return letter;
    } catch (e) {
      console.error("Error calling /api/game/start:", e);
      setAiMessage("Error calling /api/game/start (check console).");
      setShowAiMessage(true);
      return null;
    }
  };

  // Start → resetea y pide letra al backend
  const startGame = async () => {
    if (isRunning) return;

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

  // 🔹 /api/game/round
  const submitAnswers = async () => {
    if (!isRunning) return;
    setIsRunning(false);

    if (!gameId) {
      console.warn("No gameId, can't send round to backend.");
      setAiMessage("No game in progress. Start a new game first!");
      setShowAiMessage(true);
      return;
    }

    const payload: SubmitRoundRequest = {
      gameId: gameId,
      letter: currentLetter,
      answers: categories.map((cat) => ({
        categoryId: cat.id,
        value: answers[cat.id] ?? "",
      })),
    };

    console.log("Payload enviado a /api/game/round:", payload);

    try {
      const { data } = await api.post<SubmitRoundResponse>(
        "/game/round",
        payload
      );
      console.log("Resultado de la ronda en el backend:", data);

      const newValidation = buildEmptyValidation(categories);
      const newAnswers: Record<number, string> = { ...answers };

      let roundScore = 0;

      data.results.forEach((r) => {
        newAnswers[r.categoryId] = r.value ?? "";
        newValidation[r.categoryId] = r.valid ? "correct" : "incorrect";
        roundScore += r.score ?? 0;
      });

      setAnswers(newAnswers);
      setValidation(newValidation);
      setScore((prev) => prev + roundScore);

      setAiMessage(
        `Round finished! You scored ${roundScore} points this round.`
      );
      setShowAiMessage(true);

      setTimeout(() => {
        resetRoundState();
      }, 10000);
    } catch (error) {
      console.error("Error enviando la ronda al backend:", error);
      setAiMessage("There was an error sending your answers to the server.");
      setShowAiMessage(true);
    }
  };

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
      className="min-h-screen w-full font-sans px-4 py-8 relative overflow-hidden"
      style={{ backgroundColor: "#FFF6F6" }}
    >
      {/* Efectos decorativos de fondo */}
      <div className="absolute inset-0 opacity-20">
        <div
          className="absolute top-20 left-20 w-72 h-72 rounded-full blur-3xl animate-pulse"
          style={{ backgroundColor: "#FAD4D8" }}
        ></div>
        <div
          className="absolute bottom-20 right-20 w-96 h-96 rounded-full blur-3xl animate-pulse"
          style={{ backgroundColor: "#FAD4D8" }}
        ></div>
      </div>

      <div className="container mx-auto max-w-5xl relative z-10">
        {/* Botón Home */}
        <Link
          to="/"
          className="absolute top-0 left-0 p-3 rounded-full shadow-lg transition-all hover:scale-110 border-2"
          style={{
            backgroundColor: "white",
            borderColor: "#E63946",
          }}
          onMouseEnter={(e) =>
            (e.currentTarget.style.backgroundColor = "#FAD4D8")
          }
          onMouseLeave={(e) =>
            (e.currentTarget.style.backgroundColor = "white")
          }
        >
          <span style={{ fontSize: "24px", color: "#E63946" }}>🏠</span>
        </Link>

        {/* Título y letra actual */}
        <div className="text-center mb-6">
          <h1
            className="text-5xl md:text-6xl font-bold mb-4"
            style={{ color: "#2D1E1E" }}
          >
            Tutti - <span style={{ color: "#F3722C" }}>FruttIA</span>
          </h1>

          <div className="flex items-center justify-center gap-6 flex-wrap">
            {/* Círculo con la letra */}
            <div
              className="w-32 h-32 rounded-full flex items-center justify-center shadow-2xl border-4"
              style={{
                backgroundColor: "white",
                borderColor: "#E63946",
              }}
            >
              <span
                className="text-7xl font-bold"
                style={{ color: "#E63946" }}
              >
                {currentLetter}
              </span>
            </div>

            {/* Score y Timer */}
            <div className="flex flex-col gap-3">
              <div
                className="px-6 py-3 rounded-2xl shadow-md border-2"
                style={{
                  backgroundColor: "white",
                  borderColor: "#F3722C",
                }}
              >
                <span
                  style={{ color: "#2D1E1E" }}
                  className="font-bold text-lg"
                >
                  Score:
                </span>
                <span
                  style={{ color: "#E63946" }}
                  className="font-bold text-2xl ml-2"
                >
                  {score}
                </span>
              </div>

              <div
                className="px-6 py-3 rounded-2xl shadow-md border-2"
                style={{
                  backgroundColor: "white",
                  borderColor: timer <= 10 ? "#C1121F" : "#F3722C",
                }}
              >
                <span
                  style={{ color: "#2D1E1E" }}
                  className="font-bold text-lg"
                >
                  ⏱ Time:
                </span>
                <span
                  className={`font-bold text-2xl ml-2 ${
                    timer <= 10 ? "animate-pulse" : ""
                  }`}
                  style={{ color: timer <= 10 ? "#C1121F" : "#E63946" }}
                >
                  {timer}s
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Botones de control */}
        <div className="flex flex-wrap gap-4 justify-center mb-6">
          <button
            onClick={startGame}
            disabled={isRunning}
            className="px-6 py-3 rounded-xl font-bold text-white shadow-lg transition-all hover:scale-105 flex items-center gap-2 border-2 disabled:opacity-50 disabled:cursor-not-allowed"
            style={{
              backgroundColor: isRunning ? "#9B5151" : "#E63946",
              borderColor: "#C1121F",
            }}
            onMouseEnter={(e) => {
              if (!isRunning)
                e.currentTarget.style.backgroundColor = "#C1121F";
            }}
            onMouseLeave={(e) => {
              if (!isRunning)
                e.currentTarget.style.backgroundColor = "#E63946";
            }}
          >
            <span>▶</span>
            Start
          </button>

          <button
            onClick={giveUp}
            disabled={!isRunning}
            className="px-6 py-3 rounded-xl font-bold shadow-lg transition-all hover:scale-105 flex items-center gap-2 border-2 disabled:opacity-50 disabled:cursor-not-allowed"
            style={{
              backgroundColor: "white",
              borderColor: "#E63946",
              color: "#E63946",
            }}
            onMouseEnter={(e) => {
              if (isRunning) {
                e.currentTarget.style.backgroundColor = "#FAD4D8";
              }
            }}
            onMouseLeave={(e) => {
              if (isRunning) {
                e.currentTarget.style.backgroundColor = "white";
              }
            }}
          >
            <span>✖</span>
            Give Up
          </button>
        </div>

        {/* Card principal con categorías */}
        <div
          className="rounded-3xl p-8 shadow-2xl border-2 mb-6"
          style={{
            backgroundColor: "#FAD4D8",
            borderColor: "#F3722C",
          }}
        >
          <h2
            className="text-2xl font-bold mb-6 text-center"
            style={{ color: "#2D1E1E" }}
          >
            Categorías
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
            {categories.map((cat) => (
              <div key={cat.id} className="flex flex-col">
                <label
                  className="font-bold mb-2 text-lg"
                  style={{ color: "#2D1E1E" }}
                >
                  {cat.name}
                </label>
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    className="flex-1 p-3 rounded-xl font-medium text-lg border-2 focus:outline-none transition-all shadow-sm"
                    style={{
                      backgroundColor: "white",
                      color: "#2D1E1E",
                      borderColor: "#F3722C",
                    }}
                    value={answers[cat.id] ?? ""}
                    onChange={(e) => handleChange(cat.id, e.target.value)}
                    disabled={!isRunning}
                    placeholder={`Ingresa una ${cat.name.toLowerCase()}...`}
                    onFocus={(e) =>
                      (e.target.style.borderColor = "#E63946")
                    }
                    onBlur={(e) =>
                      (e.target.style.borderColor = "#F3722C")
                    }
                  />

                  {validation[cat.id] && (
                    <div
                      className="w-10 h-10 rounded-full flex items-center justify-center font-bold text-white shadow-lg text-xl"
                      style={{
                        backgroundColor:
                          validation[cat.id] === "correct"
                            ? "#E63946"
                            : "#C1121F",
                      }}
                    >
                      {validation[cat.id] === "correct" ? "✓" : "✗"}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>

          {/* Botón Submit */}
          <button
            onClick={submitAnswers}
            disabled={!isRunning}
            className="w-full py-4 rounded-xl font-bold text-white text-lg shadow-lg transition-all hover:scale-105 flex items-center justify-center gap-2 border-2 disabled:opacity-50 disabled:cursor-not-allowed"
            style={{
              backgroundColor: !isRunning ? "#9B5151" : "#E63946",
              borderColor: "#C1121F",
            }}
            onMouseEnter={(e) => {
              if (isRunning)
                e.currentTarget.style.backgroundColor = "#C1121F";
            }}
            onMouseLeave={(e) => {
              if (isRunning)
                e.currentTarget.style.backgroundColor = "#E63946";
            }}
          >
            <span>✔</span>
            Tutti Frutti
          </button>
        </div>

        {/* Mensaje de la IA */}
        {showAiMessage && (
          <div
            className="rounded-2xl p-6 shadow-xl border-2"
            style={{
              backgroundColor: "white",
              borderColor: "#F3722C",
            }}
          >
            <div className="flex items-start gap-4">
              <div
                className="rounded-full p-3 shadow-md"
                style={{ backgroundColor: "#E63946" }}
              >
                <span className="text-2xl">🤖</span>
              </div>
              <div className="flex-1">
                <p
                  className="font-bold text-lg mb-1"
                  style={{ color: "#2D1E1E" }}
                >
                  AI Assistant:
                </p>
                <p
                  className="italic text-lg"
                  style={{ color: "#9B5151" }}
                >
                  {aiMessage}
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default GamePage;
