import "./HomePage.css";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { gameService } from "../services/gameService";
import type { Category } from "../types/game";

function HomePage() {
  const navigate = useNavigate();

  const [playerName, setPlayerName] = useState("");
  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Cargar categorías del backend al entrar
  useEffect(() => {
    const load = async () => {
      try {
        const cats = await gameService.getCategories();
        setCategories(cats);
        setSelectedCategories(cats.map(c => c.id)); // todas seleccionadas por defecto
      } catch (err) {
        setError("No se pudieron cargar las categorías");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const toggleCategory = (id: number) => {
    setSelectedCategories(prev =>
      prev.includes(id)
        ? prev.filter(c => c !== id)
        : [...prev, id]
    );
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
  categoryIds: selectedCategories,    // 👈 nombre igual al DTO del back
  roundTimeSeconds: 60                // si querés, puedes ajustar el tiempo
});

      // ✔️ Navega a la GamePage llevando los datos reales
      navigate("/game", { state: response });

    } catch (err) {
      console.error(err);
      setError("Error al iniciar la partida");
    }
  };

  return (
    <div className="min-h-screen max-w-screen font-sans hero-section flex flex-col items-center justify-center text-white px-4">
      <div className="text-center max-w-2xl px-4">
        <h1 className="text-5xl md:text-6xl font-bold mb-6 animate-bounce">
          Tutti - <span style={{ color: "#F3722C" }}>FruttIA</span>
        </h1>

        <p className="text-xl mb-6 max-w-lg mx-auto opacity-90">
          Challenge your mind with this fast-paced word game!
        </p>

        {error && (
          <div className="bg-red-600 p-3 rounded mb-4">
            {error}
          </div>
        )}

        <div className="mb-6">
          <label className="block mb-2 text-lg">Nombre del jugador</label>
          <input
            type="text"
            className="w-full p-3 rounded text-black"
            value={playerName}
            placeholder="Ingresa tu nombre"
            onChange={(e) => setPlayerName(e.target.value)}
          />
        </div>

        <div className="mb-6 text-left">
          <h2 className="text-lg font-bold mb-2">Categorías disponibles:</h2>

          {loading ? (
            <p>Cargando...</p>
          ) : (
            <div className="grid grid-cols-2 gap-2">
              {categories.map(cat => (
                <label key={cat.id} className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    checked={selectedCategories.includes(cat.id)}
                    onChange={() => toggleCategory(cat.id)}
                  />
                  {cat.name}
                </label>
              ))}
            </div>
          )}
        </div>

        <button
          className="game-btn text-white font-bold py-4 px-8 rounded-full text-lg flex items-center justify-center gap-2"
          style={{ backgroundColor: "#E63946" }}
          onClick={startGame}
        >
          Empezar partida
        </button>
      </div>
    </div>
  );
}

export default HomePage;
