import { useEffect, useState } from "react";
import "../styles/game.css";
import { Link, useLocation } from "react-router-dom";
import type {
  StartGameRequest,
  StartGameResponse,
  SubmitRoundRequest,
  SubmitRoundResponse,
} from "../types/game";

const CATEGORIES = [
  { key: "fruit", label: "Fruit" },     // id 1 → Frutas
  { key: "country", label: "Country" }, // id 2 → Países
  { key: "animal", label: "Animal" },   // id 3 → Animales
  { key: "color", label: "Color" },     // id 4 → Colores
] as const;

type CategoryKey = (typeof CATEGORIES)[number]["key"];
type ValidationState = "correct" | "incorrect" | null;

const INITIAL_ANSWERS: Record<CategoryKey, string> = {
  fruit: "",
  country: "",
  animal: "",
  color: "",
};

const INITIAL_VALIDATION: Record<CategoryKey, ValidationState> = {
  fruit: null,
  country: null,
  animal: null,
  color: null,
};

// IDs de categorías del backend, en el mismo orden que CATEGORIES
const BACKEND_CATEGORY_IDS = [1, 2, 3, 4] as const;

function GamePage() {
  const location = useLocation();
  const gameStateFromNav = location.state as StartGameResponse | undefined;
  const defaultRoundTime = gameStateFromNav?.roundTimeSeconds ?? 60;

  const [currentLetter, setCurrentLetter] = useState<string>("?");
  const [score, setScore] = useState<number>(0);
  const [timer, setTimer] = useState<number>(defaultRoundTime);
const [initialRoundTime, setInitialRoundTime] = useState<number>(defaultRoundTime);
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [answers, setAnswers] = useState<Record<CategoryKey, string>>(INITIAL_ANSWERS);
  const [validation, setValidation] =
    useState<Record<CategoryKey, ValidationState>>(INITIAL_VALIDATION);
  const [aiMessage, setAiMessage] = useState<string>(
    "Ready to play? Click Start when you're ready!"
  );
  const [showAiMessage, setShowAiMessage] = useState<boolean>(true);

  // gameId que devuelve el backend en /api/game/start
  const [gameId, setGameId] = useState<string | null>(null);

  // 🆕 Si llegamos desde HomePage con datos de navegación
  useEffect(() => {
    if (gameStateFromNav) {
      setGameId(gameStateFromNav.gameId);
      setCurrentLetter(gameStateFromNav.firstLetter.trim().toUpperCase());
      
      const roundTimeFromNav = gameStateFromNav.roundTimeSeconds || 60;
      setInitialRoundTime(roundTimeFromNav);
      setTimer(roundTimeFromNav);
    }
  }, [gameStateFromNav]);

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
    setTimer(initialRoundTime);
    setCurrentLetter("?");
    setIsRunning(false);
    setAiMessage("Ready to play? Click Start when you're ready!");
    setShowAiMessage(true);
    // NO reseteamos score aquí para mantener el total de la partida
  };

  // 🔹 1) /api/game/start – misma lógica que ApiDebugPage pero usando tipos compartidos
  const fetchLetterFromApi = async (): Promise<string | null> => {
    const payload: StartGameRequest = {
      playerName: "Player 1",       // por ahora fijo
      categoryIds: [...BACKEND_CATEGORY_IDS],
      roundTimeSeconds: initialRoundTime,
    };

    try {
      const resp = await fetch("http://localhost:8080/api/game/start", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!resp.ok) {
        const text = await resp.text();
        console.error("Error HTTP en /api/game/start:", resp.status, text);
        setAiMessage("Error calling /api/game/start (check console).");
        setShowAiMessage(true);
        return null;
      }

      const data: StartGameResponse = await resp.json();
      setGameId(data.gameId);

      const letter = data.firstLetter.trim().toUpperCase();
      setCurrentLetter(letter);
      console.log("Letter from API:", letter);

      return letter;
    } catch (e) {
      console.error("Network error in /api/game/start:", e);
      setAiMessage("Network error calling /api/game/start.");
      setShowAiMessage(true);
      return null;
    }
  };

  // Start → resetea y pide letra al backend
  const startGame = async () => {
    if (isRunning) return;

    setAnswers(INITIAL_ANSWERS);
    setValidation(INITIAL_VALIDATION);
    setTimer(initialRoundTime);
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

  // 🔹 2) /api/game/round – mismo contrato que ApiDebugPage
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
      answers: CATEGORIES.map(({ key }, index) => ({
        categoryId: BACKEND_CATEGORY_IDS[index],
        value: answers[key],
      })),
    };

    console.log("Payload enviado a /api/game/round:", payload);

    try {
      const resp = await fetch("http://localhost:8080/api/game/round", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!resp.ok) {
        const text = await resp.text();
        console.error("Error HTTP en /api/game/round:", resp.status, text);
        setAiMessage("Error calling /api/game/round (check console).");
        setShowAiMessage(true);
        return;
      }

      const data: SubmitRoundResponse = await resp.json();
      console.log("Resultado de la ronda en el backend:", data);

      // Mapear resultado del backend → UI
      const idToKey: Record<number, CategoryKey> = {
        1: "fruit",
        2: "country",
        3: "animal",
        4: "color",
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

      // Reset suave después de unos segundos
      setTimeout(() => {
        resetRoundState();
      }, 10000);
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
      className="min-h-screen w-full font-sans px-4 py-8 relative overflow-hidden"
      style={{ backgroundColor: '#FFF6F6' }}
    >
      {/* Efectos decorativos de fondo */}
      <div className="absolute inset-0 opacity-20">
        <div 
          className="absolute top-20 left-20 w-72 h-72 rounded-full blur-3xl animate-pulse"
          style={{ backgroundColor: '#FAD4D8' }}
        ></div>
        <div 
          className="absolute bottom-20 right-20 w-96 h-96 rounded-full blur-3xl animate-pulse"
          style={{ backgroundColor: '#FAD4D8' }}
        ></div>
      </div>

      <div className="container mx-auto max-w-5xl relative z-10">
        {/* Botón Home */}
        <Link
          to="/"
          className="absolute top-0 left-0 p-3 rounded-full shadow-lg transition-all hover:scale-110 border-2"
          style={{ 
            backgroundColor: 'white',
            borderColor: '#E63946'
          }}
          onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#FAD4D8'}
          onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'white'}
        >
          <span style={{ fontSize: '24px', color: '#E63946' }}>🏠</span>
        </Link>

        {/* Título y letra actual */}
        <div className="text-center mb-6">
          <h1 
            className="text-5xl md:text-6xl font-bold mb-4"
            style={{ color: '#2D1E1E' }}
          >
            Tutti - <span style={{ color: '#F3722C' }}>FruttIA</span>
          </h1>
          
          <div className="flex items-center justify-center gap-6 flex-wrap">
            {/* Círculo con la letra */}
            <div 
              className="w-32 h-32 rounded-full flex items-center justify-center shadow-2xl border-4"
              style={{ 
                backgroundColor: 'white',
                borderColor: '#E63946'
              }}
            >
              <span 
                className="text-7xl font-bold"
                style={{ color: '#E63946' }}
              >
                {currentLetter}
              </span>
            </div>

            {/* Score y Timer */}
            <div className="flex flex-col gap-3">
              <div 
                className="px-6 py-3 rounded-2xl shadow-md border-2"
                style={{ 
                  backgroundColor: 'white',
                  borderColor: '#F3722C'
                }}
              >
                <span style={{ color: '#2D1E1E' }} className="font-bold text-lg">
                  Score: 
                </span>
                <span style={{ color: '#E63946' }} className="font-bold text-2xl ml-2">
                  {score}
                </span>
              </div>

              <div 
                className="px-6 py-3 rounded-2xl shadow-md border-2"
                style={{ 
                  backgroundColor: 'white',
                  borderColor: timer <= 10 ? '#C1121F' : '#F3722C'
                }}
              >
                <span style={{ color: '#2D1E1E' }} className="font-bold text-lg">
                  ⏱ Time: 
                </span>
                <span 
                  className={`font-bold text-2xl ml-2 ${timer <= 10 ? 'animate-pulse' : ''}`}
                  style={{ color: timer <= 10 ? '#C1121F' : '#E63946' }}
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
              backgroundColor: isRunning ? '#9B5151' : '#E63946',
              borderColor: '#C1121F'
            }}
            onMouseEnter={(e) => {
              if (!isRunning) e.currentTarget.style.backgroundColor = '#C1121F';
            }}
            onMouseLeave={(e) => {
              if (!isRunning) e.currentTarget.style.backgroundColor = '#E63946';
            }}
          >
            <span>▶</span>
            Start
          </button>

          <button
            onClick={stopGame}
            disabled={!isRunning}
            className="px-6 py-3 rounded-xl font-bold text-white shadow-lg transition-all hover:scale-105 flex items-center gap-2 border-2 disabled:opacity-50 disabled:cursor-not-allowed"
            style={{ 
              backgroundColor: !isRunning ? '#9B5151' : '#F3722C',
              borderColor: '#E63946'
            }}
            onMouseEnter={(e) => {
              if (isRunning) e.currentTarget.style.backgroundColor = '#E63946';
            }}
            onMouseLeave={(e) => {
              if (isRunning) e.currentTarget.style.backgroundColor = '#F3722C';
            }}
          >
            <span>■</span>
            Stop
          </button>

          <button
            onClick={giveUp}
            disabled={!isRunning}
            className="px-6 py-3 rounded-xl font-bold shadow-lg transition-all hover:scale-105 flex items-center gap-2 border-2 disabled:opacity-50 disabled:cursor-not-allowed"
            style={{ 
              backgroundColor: 'white',
              borderColor: '#E63946',
              color: '#E63946'
            }}
            onMouseEnter={(e) => {
              if (isRunning) {
                e.currentTarget.style.backgroundColor = '#FAD4D8';
              }
            }}
            onMouseLeave={(e) => {
              if (isRunning) {
                e.currentTarget.style.backgroundColor = 'white';
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
            backgroundColor: '#FAD4D8',
            borderColor: '#F3722C'
          }}
        >
          <h2 
            className="text-2xl font-bold mb-6 text-center"
            style={{ color: '#2D1E1E' }}
          >
            Categories
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
            {CATEGORIES.map(({ key, label }) => (
              <div key={key} className="flex flex-col">
                <label 
                  className="font-bold mb-2 text-lg"
                  style={{ color: '#2D1E1E' }}
                >
                  {label}
                </label>
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    className="flex-1 p-3 rounded-xl font-medium text-lg border-2 focus:outline-none transition-all shadow-sm"
                    style={{ 
                      backgroundColor: 'white',
                      color: '#2D1E1E',
                      borderColor: '#F3722C'
                    }}
                    value={answers[key]}
                    onChange={(e) => handleChange(key, e.target.value)}
                    disabled={!isRunning}
                    placeholder={`Enter a ${label.toLowerCase()}...`}
                    onFocus={(e) => e.target.style.borderColor = '#E63946'}
                    onBlur={(e) => e.target.style.borderColor = '#F3722C'}
                  />
                  
                  {validation[key] && (
                    <div 
                      className="w-10 h-10 rounded-full flex items-center justify-center font-bold text-white shadow-lg text-xl"
                      style={{ 
                        backgroundColor: validation[key] === "correct" ? '#E63946' : '#C1121F'
                      }}
                    >
                      {validation[key] === "correct" ? "✓" : "✗"}
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
              backgroundColor: !isRunning ? '#9B5151' : '#E63946',
              borderColor: '#C1121F'
            }}
            onMouseEnter={(e) => {
              if (isRunning) e.currentTarget.style.backgroundColor = '#C1121F';
            }}
            onMouseLeave={(e) => {
              if (isRunning) e.currentTarget.style.backgroundColor = '#E63946';
            }}
          >
            <span>✔</span>
            Submit Answers
          </button>
        </div>

        {/* Mensaje de la IA */}
        {showAiMessage && (
          <div 
            className="rounded-2xl p-6 shadow-xl border-2"
            style={{ 
              backgroundColor: 'white',
              borderColor: '#F3722C'
            }}
          >
            <div className="flex items-start gap-4">
              <div 
                className="rounded-full p-3 shadow-md"
                style={{ backgroundColor: '#E63946' }}
              >
                <span className="text-2xl">🤖</span>
              </div>
              <div className="flex-1">
                <p 
                  className="font-bold text-lg mb-1"
                  style={{ color: '#2D1E1E' }}
                >
                  AI Assistant:
                </p>
                <p 
                  className="italic text-lg"
                  style={{ color: '#9B5151' }}
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