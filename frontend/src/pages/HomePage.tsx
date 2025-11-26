import "./HomePage.css";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { gameService } from "../services/gameService";
import type { Category, Letter } from "../types/game";
import { Settings, X } from "lucide-react";

interface GameConfigFromHome {
  playerName: string;
  categoryIds: number[];
  customCategories: string[];
  roundTimeSeconds: number;
}

const ALL_LETTERS: Letter[] = [
  "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
  "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
  "V", "W", "X", "Y", "Z",
];

function HomePage() {
  const navigate = useNavigate();

  const [playerName, setPlayerName] = useState("");
  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<number[]>([]);
  const [customCategories, setCustomCategories] = useState<string[]>([]);
  const [newCustomCategory, setNewCustomCategory] = useState("");

  const [selectedLetters, setSelectedLetters] = useState<Letter[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showConfig, setShowConfig] = useState(false);
  const [roundTime, setRoundTime] = useState(60);

  // Cargar categorías y letras del backend al entrar
  useEffect(() => {
    const load = async () => {
      try {
        const [cats, alphabet] = await Promise.all([
          gameService.getCategories(),
          gameService.getLetters(),
        ]);

        setCategories(cats);
        setSelectedCategories(cats.map((c) => c.id)); // todas seleccionadas por defecto
        setSelectedCategories(cats.map((c) => c.id)); // todas seleccionadas por defecto

        // Letras habilitadas desde el backend; si viniera vacío, usamos todas
        const backendLetters = alphabet.letters ?? [];
        setSelectedLetters(
          backendLetters.length > 0 ? backendLetters : ALL_LETTERS
        );
      } catch (err) {
        console.error(err);
        setError("No se pudieron cargar las categorías");
        console.error(err);
        setError("No se pudieron cargar las categorías o letras");
        // fallback: habilitamos todas las letras
        setSelectedLetters(ALL_LETTERS);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const toggleCategory = (id: number) => {
    setSelectedCategories((prev) =>
      prev.includes(id) ? prev.filter((c) => c !== id) : [...prev, id]
    );
  };

  const toggleLetter = (letter: Letter) => {
    setSelectedLetters((prev) =>
      prev.includes(letter)
        ? prev.filter((l) => l !== letter)
        : [...prev, letter]
    );
  };

  // Guardar configuración (solo letras por ahora) y cerrar modal
  const saveConfig = async () => {
    setError("");
    try {
      await gameService.updateLetters({
        enabledLetters: selectedLetters,
      });
      setShowConfig(false);
    } catch (err) {
      console.error(err);
      setError("No se pudo guardar la configuración de letras");
    }
  };

  // 🔹 Añadir categoría solo para esta partida
  const addCustomCategory = () => {
    const trimmed = newCustomCategory.trim();
    if (!trimmed) return;

    // evitar duplicados contra base + custom
    const lower = trimmed.toLowerCase();
    const existsInCustom = customCategories.some(
      (c) => c.toLowerCase() === lower
    );
    const existsInBase = categories.some(
      (c) => c.name.toLowerCase() === lower
    );

    if (existsInCustom || existsInBase) {
      setError("Esa categoría ya existe");
      return;
    }

    setCustomCategories((prev) => [...prev, trimmed]);
    setNewCustomCategory("");
  };

  const removeCustomCategory = (index: number) => {
    setCustomCategories((prev) => prev.filter((_, i) => i !== index));
  };

  // 🚀 Iniciar partida: solo navegamos con la configuración,
  // el backend se llama desde GamePage.
  const startGame = () => {
    setError("");

    if (!playerName.trim()) {
      setError("Debes ingresar un nombre");
      return;
    }

    if (selectedCategories.length === 0) {
      setError("Debes seleccionar al menos una categoría");
      return;
    }

    if (selectedLetters.length === 0) {
      setError("Debes seleccionar al menos una letra");
      return;
    }

    if (selectedCategories.length === 0 && customCategories.length === 0) {
      setError("Selecciona al menos una categoría o agrega un tema");
      return;
    }

    const config: GameConfigFromHome = {
      playerName: playerName.trim(),
      categoryIds: selectedCategories,
      customCategories,
      roundTimeSeconds: roundTime,
    };

    navigate("/game", { state: config });
    try {
      const response = await gameService.startGame({
        playerName,
        categoryIds: selectedCategories,
        roundTimeSeconds: roundTime,
      });

      // 👇 Enviamos el roundTime al estado de la partida
      navigate("/game", {
        state: {
          ...response,
          roundTimeSeconds: roundTime,
        },
      });
    } catch (err) {
      console.error(err);
      setError("Error al iniciar la partida");
    }
  };

  return (
    <div
      className="min-h-screen w-full font-sans flex flex-col items-center justify-center px-4 relative overflow-hidden"
      style={{ backgroundColor: "#FFF6F6" }}
    >
      {/* Efectos decorativos de fondo */}
      <div className="absolute inset-0 opacity-30">
        <div
          className="absolute top-20 left-20 w-72 h-72 rounded-full blur-3xl animate-pulse"
          style={{ backgroundColor: "#FAD4D8" }}
        ></div>
        <div
          className="absolute bottom-20 right-20 w-96 h-96 rounded-full blur-3xl animate-pulse"
          style={{ backgroundColor: "#FAD4D8" }}
        ></div>
      </div>

      <div className="relative z-10 text-center max-w-md w-full">
        {/* Título principal */}
        <div className="mb-8">
          <h1
            className="text-6xl md:text-7xl font-bold mb-2 drop-shadow-md"
            style={{ color: "#2D1E1E" }}
          >
            Tutti - <span style={{ color: "#F3722C" }}>FruttIA</span>
          </h1>
          <p
            className="text-lg mt-4 font-medium"
            style={{ color: "#9B5151" }}
          >
            ¡Desafía tu mente con palabras!
          </p>
        </div>

        {/* Card principal */}
        <div
          className="rounded-3xl p-8 shadow-2xl border-2"
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

          {/* Input nombre del jugador */}
          <div className="mb-6">
            <label
              className="block mb-3 text-lg font-bold text-left"
              style={{ color: "#2D1E1E" }}
            >
              Nombre del jugador
            </label>
            <input
              type="text"
              className="w-full p-4 rounded-xl font-medium text-lg border-2 focus:outline-none transition-all shadow-sm"
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

          {/* Botón de configuración */}
          <button
            onClick={() => setShowConfig(!showConfig)}
            className="w-full text-white font-bold py-4 px-6 rounded-xl text-lg flex items-center justify-center gap-3 mb-4 transition-all hover:scale-105 border-2 shadow-md"
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
            <Settings size={24} />
            Configuración
          </button>

          {/* Botón iniciar partida */}
          <button
            className="w-full text-white font-bold py-5 px-8 rounded-xl text-xl shadow-lg transition-all hover:scale-105 hover:shadow-2xl"
            style={{ backgroundColor: "#E63946" }}
            onClick={startGame}
            onMouseEnter={(e) =>
              (e.currentTarget.style.backgroundColor = "#C1121F")
            }
            onMouseLeave={(e) =>
              (e.currentTarget.style.backgroundColor = "#E63946")
            }
          >
            🎮 Empezar Partida
          </button>
        </div>
      </div>

      {/* Modal de configuración */}
      {showConfig && (
        <div
          className="fixed inset-0 flex items-center justify-center z-50 p-4"
          style={{ backgroundColor: "rgba(45, 30, 30, 0.7)" }}
        >
          <div
            className="rounded-3xl p-8 max-w-lg w-full shadow-2xl border-4 max-h-[90vh] overflow-y-auto"
            style={{
              backgroundColor: "#FAD4D8",
              borderColor: "#E63946",
            }}
          >
            {/* Header del modal */}
            <div className="flex justify-between items-center mb-6">
              <h2
                className="text-3xl font-bold flex items-center gap-3"
                style={{ color: "#2D1E1E" }}
              >
                <Settings size={32} style={{ color: "#E63946" }} />
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
                <X size={28} style={{ color: "#E63946" }} />
              </button>
            </div>

            {/* Tiempo por ronda */}
            <div className="mb-6">
              <label
                className="block mb-3 text-lg font-bold"
                style={{ color: "#2D1E1E" }}
              >
                Tiempo por ronda:{" "}
                <span style={{ color: "#E63946" }}>{roundTime}</span> segundos
              </label>
              <input
                type="range"
                min="30"
                max="120"
                step="10"
                value={roundTime}
                onChange={(e) => setRoundTime(Number(e.target.value))}
                className="w-full h-3 rounded-lg cursor-pointer"
                style={{
                  background: `linear-gradient(to right, #E63946 0%, #E63946 ${
                    ((roundTime - 30) / 90) * 100
                  }%, #9B5151 ${
                    ((roundTime - 30) / 90) * 100
                  }%, #9B5151 100%)`,
                }}
              />
              <div
                className="flex justify-between text-sm mt-2 font-medium"
                style={{ color: "#9B5151" }}
              >
                <span>30s</span>
                <span>120s</span>
              </div>
            </div>

            {/* Letras disponibles */}
            <div className="mb-6">
              <div className="flex justify-between items-center mb-2">
                <h3
                  className="text-xl font-bold"
                  style={{ color: "#2D1E1E" }}
                >
                  Letras disponibles{" "}
                  <span style={{ color: "#E63946" }}>
                    ({selectedLetters.length})
                  </span>
                </h3>
                <button
                  type="button"
                  className="text-sm font-semibold underline"
                  style={{ color: "#E63946" }}
                  onClick={() => setSelectedLetters(ALL_LETTERS)}
                >
                  Seleccionar todas
                </button>
              </div>
              <p
                className="text-sm mb-3"
                style={{ color: "#9B5151" }}
              >
                Toca una letra para activarla o desactivarla.
              </p>
              <div className="flex flex-wrap gap-3 justify-center">
                {ALL_LETTERS.map((letter) => {
                  const isEnabled = selectedLetters.includes(letter);
                  return (
                    <button
                      key={letter}
                      type="button"
                      onClick={() => toggleLetter(letter)}
                      className="w-10 h-10 rounded-full font-bold text-lg flex items-center justify-center transition-all border-2 shadow-sm"
                      style={{
                        backgroundColor: isEnabled ? "#FFFFFF" : "#9B5151",
                        color: isEnabled ? "#2D1E1E" : "#FAD4D8",
                        borderColor: isEnabled ? "#E63946" : "transparent",
                        opacity: isEnabled ? 1 : 0.6,
                      }}
                    >
                      {letter}
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Categorías disponibles */}
            {/* Categorías base */}
            <div>
              <h3
                className="text-xl font-bold mb-4"
                style={{ color: "#2D1E1E" }}
              >
                Categorías base
              </h3>
              {loading ? (
                <p
                  className="text-center py-4"
                  style={{ color: "#9B5151" }}
                >
                  Cargando...
                </p>
              ) : (
                <div className="space-y-3">
                  {categories.map((cat) => (
                    <label
                      key={cat.id}
                      className="flex items-center gap-3 p-4 rounded-xl cursor-pointer transition-all border-2 shadow-sm"
                      style={{
                        backgroundColor: selectedCategories.includes(cat.id)
                          ? "white"
                          : "rgba(255, 255, 255, 0.5)",
                        borderColor: selectedCategories.includes(cat.id)
                          ? "#E63946"
                          : "#9B5151",
                      }}
                      onMouseEnter={(e) => {
                        if (!selectedCategories.includes(cat.id)) {
                          e.currentTarget.style.backgroundColor =
                            "rgba(255, 255, 255, 0.8)";
                        }
                      }}
                      onMouseLeave={(e) => {
                        if (!selectedCategories.includes(cat.id)) {
                          e.currentTarget.style.backgroundColor =
                            "rgba(255, 255, 255, 0.5)";
                        }
                      }}
                    >
                      <input
                        type="checkbox"
                        checked={selectedCategories.includes(cat.id)}
                        onChange={() => toggleCategory(cat.id)}
                        className="w-5 h-5 cursor-pointer"
                        style={{ accentColor: "#E63946" }}
                      />
                      <span
                        className="text-lg font-semibold"
                        style={{ color: "#2D1E1E" }}
                      >
                        {cat.name}
                      </span>
                    </label>
                  ))}
                </div>
              )}
            </div>

            {/* Temas de la partida (custom) */}
            <div className="mt-6">
              <h3
                className="text-xl font-bold mb-3"
                style={{ color: "#2D1E1E" }}
              >
                Temas de la partida
              </h3>

              <div className="flex flex-wrap gap-2 mb-3">
                {customCategories.length === 0 && (
                  <p
                    className="text-sm italic"
                    style={{ color: "#9B5151" }}
                  >
                    No agregaste temas todavía. Escribe uno abajo y pulsa
                    &ldquo;Añadir&rdquo;.
                  </p>
                )}

                {customCategories.map((name, index) => (
                  <div
                    key={`${name}-${index}`}
                    className="flex items-center gap-2 px-3 py-1 rounded-full border text-sm shadow-sm"
                    style={{
                      backgroundColor: "white",
                      borderColor: "#E63946",
                      color: "#2D1E1E",
                    }}
                  >
                    <span>{name}</span>
                    <button
                      onClick={() => removeCustomCategory(index)}
                      className="w-5 h-5 flex items-center justify-center rounded-full text-xs font-bold"
                      style={{
                        backgroundColor: "#E63946",
                        color: "white",
                      }}
                    >
                      ✕
                    </button>
                  </div>
                ))}
              </div>

              <div className="flex gap-2">
                <input
                  type="text"
                  value={newCustomCategory}
                  onChange={(e) => setNewCustomCategory(e.target.value)}
                  placeholder="Ej: Medicamento, Club deportivo..."
                  className="flex-1 p-3 rounded-xl border-2 text-sm focus:outline-none shadow-sm"
                  style={{
                    backgroundColor: "white",
                    borderColor: "#F3722C",
                    color: "#2D1E1E",
                  }}
                  onFocus={(e) =>
                    (e.target.style.borderColor = "#E63946")
                  }
                  onBlur={(e) =>
                    (e.target.style.borderColor = "#F3722C")
                  }
                />
                <button
                  type="button"
                  onClick={addCustomCategory}
                  className="px-4 py-2 rounded-xl font-bold text-sm shadow-md"
                  style={{
                    backgroundColor: "#F3722C",
                    color: "white",
                    borderColor: "#E63946",
                    borderWidth: 2,
                    borderStyle: "solid",
                  }}
                >
                  + Añadir
                </button>
              </div>
            </div>

            {/* Botón guardar */}
            <button
              onClick={saveConfig}
              className="w-full mt-6 text-white font-bold py-4 rounded-xl transition-all shadow-md"
              style={{ backgroundColor: "#F3722C" }}
              onMouseEnter={(e) =>
                (e.currentTarget.style.backgroundColor = "#E63946")
              }
              onMouseLeave={(e) =>
                (e.currentTarget.style.backgroundColor = "#F3722C")
              }
            >
              Guardar y Cerrar
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default HomePage;
