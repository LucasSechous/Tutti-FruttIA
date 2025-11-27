import "./HomePage.css";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { gameService } from "../services/gameService";
import type { Category } from "../types/game";
import { Settings, X } from "lucide-react";

type GameMode = "RUSH" | "NORMAL" | "MARATHON" | "SANDBOX";

const GAME_MODES: Record<
  GameMode,
  { label: string; description: string; rounds: number | null; time: number | null }
> = {
  RUSH: { label: "Rush", description: "3 rondas · 30s", rounds: 3, time: 30 },
  NORMAL: { label: "Normal", description: "5 rondas · 45s", rounds: 5, time: 45 },
  MARATHON: { label: "Maratón", description: "10 rondas · 45s", rounds: 10, time: 45 },
  SANDBOX: { label: "Sandbox", description: "Libre · tú eliges", rounds: null, time: null },
};

const LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

function HomePage() {
  const navigate = useNavigate();

  const [playerName, setPlayerName] = useState("");
  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showConfig, setShowConfig] = useState(false);

  const [gameMode, setGameMode] = useState<GameMode>("NORMAL");
  const [roundTime, setRoundTime] = useState(45);
  const [roundCount, setRoundCount] = useState(5);

  const [activeLetters, setActiveLetters] = useState<string[]>(LETTERS);

  const [customTopics, setCustomTopics] = useState<string[]>([]);
  const [topicInput, setTopicInput] = useState("");

  const isSandbox = gameMode === "SANDBOX";

  // Cargar categorías del backend al entrar
  useEffect(() => {
    const load = async () => {
      try {
        const cats = await gameService.getCategories();
        setCategories(cats);
        setSelectedCategories(cats.map((c) => c.id)); // todas seleccionadas por defecto
      } catch (err) {
        setError("No se pudieron cargar las categorías");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  // Cuando se cambia de modo de juego, si no es sandbox, fijar rondas y tiempo.
  useEffect(() => {
    if (!isSandbox) {
      const cfg = GAME_MODES[gameMode];
      if (cfg.rounds != null) setRoundCount(cfg.rounds);
      if (cfg.time != null) setRoundTime(cfg.time);
    }
  }, [gameMode, isSandbox]);

  const toggleCategory = (id: number) => {
    setSelectedCategories((prev) =>
      prev.includes(id) ? prev.filter((c) => c !== id) : [...prev, id]
    );
  };

  const toggleLetter = (letter: string) => {
    setActiveLetters((prev) =>
      prev.includes(letter) ? prev.filter((l) => l !== letter) : [...prev, letter]
    );
  };

  const addTopic = () => {
    const value = topicInput.trim();
    if (!value) return;
    setCustomTopics((prev) => [...prev, value]);
    setTopicInput("");
  };

  const removeTopic = (topic: string) => {
    setCustomTopics((prev) => prev.filter((t) => t !== topic));
  };

  // 🔥 Iniciar partida real
  const startGame = async () => {
    setError("");

    if (!playerName.trim()) {
      setError("Debes ingresar un nombre");
      return;
    }

    try {
      const response = await gameService.startGame({
        playerName,
        categoryIds: selectedCategories,
        roundTimeSeconds: roundTime,
        // 👉 más adelante pueden enviar roundCount, gameMode, letters, etc.
      });

      navigate("/game", {
        state: {
          ...response,
          roundTimeSeconds: roundTime,
          totalRounds: roundCount,
          gameMode,
          letters: activeLetters,
        },
      });
    } catch (err) {
      console.error(err);
      setError("Error al iniciar la partida");
    }
  };

  const effectiveTime = roundTime;
  const effectiveRounds = roundCount;

  return (
    <div
      className="min-h-screen w-full font-sans flex flex-col items-center justify-center px-4 relative overflow-hidden"
      style={{ backgroundColor: "#FFF6F6" }}
    >
      {/* Efectos decorativos de fondo */}
      <div className="absolute inset-0 opacity-30 pointer-events-none">
        <div
          className="absolute top-20 left-20 w-72 h-72 rounded-full blur-3xl animate-pulse"
          style={{ backgroundColor: "#FAD4D8" }}
        ></div>
        <div
          className="absolute bottom-20 right-20 w-96 h-96 rounded-full blur-3xl animate-pulse"
          style={{ backgroundColor: "#FAD4D8" }}
        ></div>
      </div>

      <div className="relative z-10 text-center max-w-5xl w-full">
        {/* Título principal */}
        <div className="mb-8">
          <h1
            className="text-5xl md:text-6xl font-bold mb-2 drop-shadow-md"
            style={{ color: "#2D1E1E" }}
          >
            Tutti - <span style={{ color: "#F3722C" }}>FruttIA</span>
          </h1>
          <p className="text-lg mt-4 font-medium" style={{ color: "#9B5151" }}>
            ¡Desafía tu mente con palabras!
          </p>
        </div>

        {/* Card principal */}
        <div
          className="rounded-3xl p-6 md:p-8 shadow-2xl border-2 bg-opacity-90"
          style={{
            backgroundColor: "#FAD4D8",
            borderColor: "#F3722C",
          }}
        >
          {error && (
            <div
              className="p-3 rounded-xl mb-6 text-sm font-bold text-white"
              style={{ backgroundColor: "#C1121F" }}
            >
              {error}
            </div>
          )}

          {/* Nombre + botones */}
          <div className="mb-6 md:mb-8 flex flex-col md:flex-row gap-4 md:items-end">
            <div className="flex-1 text-left">
              <label
                className="block mb-3 text-lg font-bold"
                style={{ color: "#2D1E1E" }}
              >
                Nombre del jugador
              </label>
              <input
                type="text"
                className="w-full p-3 rounded-xl font-medium text-lg border-2 focus:outline-none transition-all shadow-sm"
                style={{
                  backgroundColor: "white",
                  color: "#2D1E1E",
                  borderColor: "#F3722C",
                }}
                value={playerName}
                placeholder="Ingresa tu nombre"
                onChange={(e) => setPlayerName(e.target.value)}
                onFocus={(e) => (e.target.style.borderColor = "#E63946")}
                onBlur={(e) => (e.target.style.borderColor = "#F3722C")}
              />
            </div>

            <div className="flex flex-col gap-3 w-full md:w-64">
              <button
                onClick={() => setShowConfig(true)}
                className="text-white font-bold py-3 px-4 rounded-xl text-base md:text-lg flex items-center justify-center gap-2 transition-all hover:scale-105 border-2 shadow-md"
                style={{
                  backgroundColor: "#F3722C",
                  borderColor: "#E63946",
                }}
                onMouseEnter={(e) =>
                  (e.currentTarget.style.backgroundColor = "#E63946")
                }
                onMouseLeave={(e) =>
                  (e.currentTarget.style.backgroundColor = "#F3722C")
                }
              >
                <Settings size={20} />
                Configuración
              </button>

              <button
                className="w-full text-white font-bold py-3 md:py-4 px-4 rounded-xl text-lg shadow-lg transition-all hover:scale-105 hover:shadow-2xl"
                style={{ backgroundColor: "#E63946" }}
                onClick={startGame}
                onMouseEnter={(e) =>
                  (e.currentTarget.style.backgroundColor = "#C1121F")
                }
                onMouseLeave={(e) =>
                  (e.currentTarget.style.backgroundColor = "#E63946")
                }
              >
                🎮 Empezar partida
              </button>
            </div>
          </div>

          {/* Resumen de modo actual */}
          <div className="text-left text-sm md:text-base" style={{ color: "#9B5151" }}>
            <span className="font-semibold" style={{ color: "#2D1E1E" }}>
              Modo actual:
            </span>{" "}
            {GAME_MODES[gameMode].label} · {effectiveRounds} rondas ·{" "}
            {effectiveTime} segundos · Letras: {activeLetters.length}
          </div>
        </div>
      </div>

      {/* Modal de configuración */}
      {showConfig && (
        <div
          className="fixed inset-0 flex items-center justify-center z-50 p-4"
          style={{ backgroundColor: "rgba(45, 30, 30, 0.7)" }}
        >
          <div
            className="rounded-3xl p-6 md:p-8 max-w-5xl w-full shadow-2xl border-4 max-h-[95vh] overflow-y-auto"
            style={{
              backgroundColor: "#FAD4D8",
              borderColor: "#E63946",
            }}
          >
            {/* Header del modal */}
            <div className="flex justify-between items-center mb-6">
              <h2
                className="text-2xl md:text-3xl font-bold flex items-center gap-3"
                style={{ color: "#2D1E1E" }}
              >
                <Settings size={28} style={{ color: "#E63946" }} />
                Configuración
              </h2>
              <button
                onClick={() => setShowConfig(false)}
                className="p-2 rounded-full transition-all"
                style={{ backgroundColor: "transparent" }}
                onMouseEnter={(e) =>
                  (e.currentTarget.style.backgroundColor =
                    "rgba(227, 57, 70, 0.2)")
                }
                onMouseLeave={(e) =>
                  (e.currentTarget.style.backgroundColor = "transparent")
                }
              >
                <X size={24} style={{ color: "#E63946" }} />
              </button>
            </div>

            {/* Layout: panel lateral + panel principal */}
            <div className="flex flex-col md:flex-row gap-6">
              {/* Lateral izquierdo: MODO DE JUEGO / RONDAS / TIEMPO */}
              <aside className="w-full md:w-64 flex flex-col gap-4">
                {/* MODO DE JUEGO */}
                <div
                  className="rounded-2xl p-4 border-2 shadow-sm"
                  style={{
                    backgroundColor: "#2D1E1E",
                    borderColor: "#F3722C",
                  }}
                >
                  <p
                    className="text-xs font-semibold mb-2 tracking-wide"
                    style={{ color: "#FAD4D8" }}
                  >
                    MODO DE JUEGO
                  </p>
                  <div className="flex flex-col gap-2">
                    {(Object.keys(GAME_MODES) as GameMode[]).map((mode) => (
                      <button
                        key={mode}
                        type="button"
                        onClick={() => setGameMode(mode)}
                        className="w-full text-left px-3 py-2 rounded-xl text-sm font-semibold transition-all"
                        style={{
                          backgroundColor:
                            gameMode === mode ? "#F3722C" : "#3E2C2C",
                          color: gameMode === mode ? "white" : "#FAD4D8",
                          border:
                            gameMode === mode
                              ? "2px solid #FFE5D5"
                              : "1px solid transparent",
                        }}
                      >
                        <div>{GAME_MODES[mode].label}</div>
                        <div className="text-[11px]" style={{ opacity: 0.8 }}>
                          {GAME_MODES[mode].description}
                        </div>
                      </button>
                    ))}
                  </div>
                </div>

                {/* RONDAS */}
                <div
                  className="rounded-2xl p-4 border-2 shadow-sm"
                  style={{
                    backgroundColor: "#FCE3E5",
                    borderColor: "#F3722C",
                  }}
                >
                  <p
                    className="text-xs font-semibold mb-2 tracking-wide"
                    style={{ color: "#9B5151" }}
                  >
                    RONDAS
                  </p>
                  <select
                    className="w-full rounded-xl px-3 py-2 text-sm font-semibold border-2 focus:outline-none"
                    style={{
                      backgroundColor: "white",
                      color: "#2D1E1E",
                      borderColor: "#F3722C",
                      cursor: isSandbox ? "pointer" : "not-allowed",
                      opacity: isSandbox ? 1 : 0.6,
                    }}
                    disabled={!isSandbox}
                    value={effectiveRounds}
                    onChange={(e) => setRoundCount(Number(e.target.value))}
                  >
                    <option value={3}>3 rondas</option>
                    <option value={5}>5 rondas</option>
                    <option value={10}>10 rondas</option>
                  </select>
                  {!isSandbox && (
                    <p className="mt-2 text-[11px]" style={{ color: "#9B5151" }}>
                      En este modo las rondas son fijas.
                    </p>
                  )}
                </div>

                {/* TIEMPO */}
                <div
                  className="rounded-2xl p-4 border-2 shadow-sm"
                  style={{
                    backgroundColor: "#FCE3E5",
                    borderColor: "#F3722C",
                  }}
                >
                  <p
                    className="text-xs font-semibold mb-2 tracking-wide"
                    style={{ color: "#9B5151" }}
                  >
                    TIEMPO
                  </p>
                  <p className="text-lg font-bold" style={{ color: "#2D1E1E" }}>
                    {effectiveTime} s por ronda
                  </p>
                  <p className="mt-1 text-[11px]" style={{ color: "#9B5151" }}>
                    {isSandbox
                      ? "Puedes ajustar el tiempo abajo."
                      : "Tiempo bloqueado por el modo de juego."}
                  </p>
                </div>
              </aside>

              {/* Panel derecho: tiempo slider, letras, categorías / temas */}
              <section className="flex-1 flex flex-col gap-6">
                {/* Tiempo por ronda (slider) */}
                <div>
                  <label
                    className="block mb-3 text-sm md:text-base font-bold"
                    style={{ color: "#2D1E1E" }}
                  >
                    Tiempo por ronda:{" "}
                    <span style={{ color: "#E63946" }}>{effectiveTime}</span>{" "}
                    segundos
                  </label>
                  <input
                    type="range"
                    min="30"
                    max="120"
                    step="10"
                    value={effectiveTime}
                    disabled={!isSandbox}
                    onChange={(e) =>
                      isSandbox && setRoundTime(Number(e.target.value))
                    }
                    className="w-full h-3 rounded-lg cursor-pointer"
                    style={{
                      background: `linear-gradient(to right, #E63946 0%, #E63946 ${
                        ((effectiveTime - 30) / 90) * 100
                      }%, #9B5151 ${
                        ((effectiveTime - 30) / 90) * 100
                      }%, #9B5151 100%)`,
                      opacity: isSandbox ? 1 : 0.5,
                    }}
                  />
                  <div
                    className="flex justify-between text-xs md:text-sm mt-2 font-medium"
                    style={{ color: "#9B5151" }}
                  >
                    <span>30s</span>
                    <span>120s</span>
                  </div>
                </div>

                {/* Letras disponibles */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <h3
                      className="text-base md:text-lg font-bold"
                      style={{ color: "#2D1E1E" }}
                    >
                      Letras disponibles{" "}
                      <span style={{ color: "#E63946" }}>
                        ({activeLetters.length})
                      </span>
                    </h3>
                    <button
                      type="button"
                      className="text-xs font-semibold underline"
                      style={{
                        color: "#E63946",
                        opacity: isSandbox ? 1 : 0.6,
                        cursor: isSandbox ? "pointer" : "not-allowed",
                      }}
                      disabled={!isSandbox}
                      onClick={() => isSandbox && setActiveLetters(LETTERS)}
                    >
                      Seleccionar todas
                    </button>
                  </div>
                  <p
                    className="text-xs mb-3"
                    style={{ color: "#9B5151" }}
                  >
                    Toca una letra para activarla o desactivarla.
                  </p>
                  <div className="flex flex-wrap gap-2">
                    {LETTERS.map((letter) => {
                      const active = activeLetters.includes(letter);
                      return (
                        <button
                          key={letter}
                          type="button"
                          onClick={() => isSandbox && toggleLetter(letter)}
                          className="w-8 h-8 md:w-9 md:h-9 rounded-full text-sm md:text-base font-bold flex items-center justify-center transition-all border-2"
                          style={{
                            backgroundColor: active ? "#F3722C" : "#FCE3E5",
                            color: active ? "white" : "#2D1E1E",
                            borderColor: active ? "#E63946" : "#FAD4D8",
                            cursor: isSandbox ? "pointer" : "default",
                            opacity: isSandbox ? 1 : active ? 1 : 0.7,
                          }}
                        >
                          {letter}
                        </button>
                      );
                    })}
                  </div>
                </div>

                {/* Categorías base + temas de la partida (pills) */}
                <div>
                  <h3
                    className="text-base md:text-lg font-bold mb-2"
                    style={{ color: "#2D1E1E" }}
                  >
                    Temas de la partida
                  </h3>
                  <p
                    className="text-xs mb-3"
                    style={{ color: "#9B5151" }}
                  >
                    En Sandbox verás las categorías base y los temas custom
                    juntos como “pills”.
                  </p>

                  {/* Pills: categorías base seleccionadas + custom topics */}
                  <div className="flex flex-wrap gap-2 mb-3">
                    {/* Categorías BASE (no se pueden quitar desde aquí) */}
                    {categories
                      .filter((cat) => selectedCategories.includes(cat.id))
                      .map((cat) => (
                        <span
                          key={cat.id}
                          className="px-3 py-1 rounded-full text-xs md:text-sm font-semibold border"
                          style={{
                            backgroundColor: "#FFE5D5",
                            color: "#2D1E1E",
                            borderColor: "#F3722C",
                            opacity: isSandbox ? 1 : 0.8,
                          }}
                        >
                          {cat.name}
                        </span>
                      ))}

                    {/* Temas custom (sí se pueden eliminar) */}
                    {customTopics.map((topic) => (
                      <button
                        key={topic}
                        type="button"
                        onClick={() => removeTopic(topic)}
                        className="flex items-center gap-1 px-3 py-1 rounded-full text-xs md:text-sm font-semibold border"
                        style={{
                          backgroundColor: "#F3722C",
                          color: "white",
                          borderColor: "#E63946",
                        }}
                      >
                        <span>{topic}</span>
                        <span className="text-[10px]">✕</span>
                      </button>
                    ))}

                    {categories.length === 0 &&
                      customTopics.length === 0 && (
                        <span
                          className="text-xs"
                          style={{ color: "#9B5151" }}
                        >
                          Aún no agregaste temas.
                        </span>
                      )}
                  </div>

                  {/* Input para añadir nuevos temas */}
                  <div className="flex flex-col sm:flex-row gap-2">
                    <input
                      type="text"
                      className="flex-1 px-3 py-2 rounded-xl border-2 text-sm focus:outline-none"
                      style={{
                        backgroundColor: "white",
                        color: "#2D1E1E",
                        borderColor: "#F3722C",
                      }}
                      placeholder="Ej: Medicamento, Club deportivo..."
                      value={topicInput}
                      onChange={(e) => setTopicInput(e.target.value)}
                    />
                    <button
                      type="button"
                      onClick={addTopic}
                      className="px-4 py-2 rounded-xl text-sm font-bold text-white border-2 shadow-sm"
                      style={{
                        backgroundColor: "#F3722C",
                        borderColor: "#E63946",
                      }}
                    >
                      + Añadir
                    </button>
                  </div>

                  {/* Lista de categorías disponibles (solo para Sandbox) */}
                  <div className="mt-5">
                    <h4
                      className="text-sm md:text-base font-bold mb-3"
                      style={{ color: "#2D1E1E" }}
                    >
                      Categorías base disponibles
                    </h4>
                    {loading ? (
                      <p
                        className="text-center py-2 text-sm"
                        style={{ color: "#9B5151" }}
                      >
                        Cargando...
                      </p>
                    ) : (
                      <div className="space-y-2">
                        {categories.map((cat) => (
                          <label
                            key={cat.id}
                            className="flex items-center gap-2 p-2 rounded-xl cursor-pointer transition-all border-2 text-sm"
                            style={{
                              backgroundColor: selectedCategories.includes(cat.id)
                                ? "white"
                                : "rgba(255, 255, 255, 0.6)",
                              borderColor: selectedCategories.includes(cat.id)
                                ? "#E63946"
                                : "#9B5151",
                              opacity: isSandbox ? 1 : 0.5,
                              cursor: isSandbox ? "pointer" : "default",
                            }}
                          >
                            <input
                              type="checkbox"
                              checked={selectedCategories.includes(cat.id)}
                              disabled={!isSandbox}
                              onChange={() => isSandbox && toggleCategory(cat.id)}
                              className="w-4 h-4 cursor-pointer"
                              style={{ accentColor: "#E63946" }}
                            />
                            <span
                              className="font-semibold"
                              style={{ color: "#2D1E1E" }}
                            >
                              {cat.name}
                            </span>
                          </label>
                        ))}
                      </div>
                    )}
                    {!isSandbox && (
                      <p
                        className="mt-2 text-[11px]"
                        style={{ color: "#9B5151" }}
                      >
                        En los modos fijos las categorías se eligen de forma
                        automática (aleatorias de la base de datos).
                      </p>
                    )}
                  </div>
                </div>
              </section>
            </div>

            {/* Botón cerrar */}
            <button
              onClick={() => setShowConfig(false)}
              className="w-full mt-6 text-white font-bold py-3 rounded-xl transition-all shadow-md"
              style={{ backgroundColor: "#F3722C" }}
              onMouseEnter={(e) =>
                (e.currentTarget.style.backgroundColor = "#E63946")
              }
              onMouseLeave={(e) =>
                (e.currentTarget.style.backgroundColor = "#F3722C")
              }
            >
              Guardar y cerrar
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default HomePage;
