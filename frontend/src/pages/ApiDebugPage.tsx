import { useState } from "react";

type CategoryId = 1 | 2 | 3 | 4;

const ALL_CATEGORIES: { id: CategoryId; label: string }[] = [
  { id: 1, label: "Frutas" },
  { id: 2, label: "Países" },
  { id: 3, label: "Animales" },
  { id: 4, label: "Colores" },
];

type StartGameResponse = {
  gameId: string;
  firstLetter: string;
  categories: { id: number; name: string; activado: boolean }[];
};

type RoundResultResponse = {
  gameId: string;
  letter: string;
  results: {
    categoryId: number;
    value: string;
    valid: boolean;
    score: number;
    reason: string;
  }[];
};

function ApiDebugPage() {
  const [playerName, setPlayerName] = useState("lucas");
  const [selectedCategories, setSelectedCategories] = useState<CategoryId[]>([
    1, 2, 3, 4,
  ]);

  const [gameId, setGameId] = useState<string | null>(null);
  const [letter, setLetter] = useState<string>("?");

  const [answers, setAnswers] = useState<Record<CategoryId, string>>({
    1: "",
    2: "",
    3: "",
    4: "",
  });

  const [startRequest, setStartRequest] = useState<any>(null);
  const [startResponse, setStartResponse] = useState<StartGameResponse | null>(
    null
  );

  const [roundRequest, setRoundRequest] = useState<any>(null);
  const [roundResponse, setRoundResponse] = useState<RoundResultResponse | null>(
    null
  );

  const toggleCategory = (id: CategoryId) => {
    setSelectedCategories((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };

  const handleAnswerChange = (id: CategoryId, value: string) => {
    setAnswers((prev) => ({ ...prev, [id]: value }));
  };

  // =========================
  // 1) /api/game/start
  // =========================
  const handleStartGame = async () => {
    const payload = {
      playerName,
      categoryIds: selectedCategories,
      roundTimeSeconds: 60,
    };

    setStartRequest(payload);

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
        console.error("Error HTTP en /game/start:", resp.status, text);
        alert("Error al llamar /game/start (ver consola)");
        return;
      }

      const data: StartGameResponse = await resp.json();
      setStartResponse(data);
      setGameId(data.gameId);
      setLetter(data.firstLetter);
    } catch (e) {
      console.error("Error de red en /game/start:", e);
      alert("Error de red al llamar /game/start");
    }
  };

  // =========================
  // 2) /api/game/round
  // =========================
  const handleSubmitRound = async () => {
    if (!gameId) {
      alert("Primero hay que iniciar la partida (gameId es null).");
      return;
    }

    const payload = {
      gameId,
      letter,
      answers: selectedCategories.map((id) => ({
        categoryId: id,
        value: answers[id],
      })),
    };

    setRoundRequest(payload);

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
        console.error("Error HTTP en /game/round:", resp.status, text);
        alert("Error al llamar /game/round (ver consola)");
        return;
      }

      const data: RoundResultResponse = await resp.json();
      setRoundResponse(data);
    } catch (e) {
      console.error("Error de red en /game/round:", e);
      alert("Error de red al llamar /game/round");
    }
  };

  return (
    <div style={{ padding: "16px", fontFamily: "system-ui" }}>
      <h1>API Debug Page (backend Tutti-FruttIA)</h1>
      <p>
        Página simple para probar <code>/api/game/start</code> y{" "}
        <code>/api/game/round</code>.
      </p>

      {/* ================= START GAME ================= */}
      <section style={{ marginTop: "24px", borderTop: "1px solid #ccc", paddingTop: "16px" }}>
        <h2>1. Iniciar partida (/api/game/start)</h2>

        <div style={{ marginBottom: "8px" }}>
          <label>
            Nombre del jugador:{" "}
            <input
              type="text"
              value={playerName}
              onChange={(e) => setPlayerName(e.target.value)}
            />
          </label>
        </div>

        <div style={{ marginBottom: "8px" }}>
          Categorías (IDs del backend):
          <div>
            {ALL_CATEGORIES.map((c) => (
              <label key={c.id} style={{ marginRight: "12px" }}>
                <input
                  type="checkbox"
                  checked={selectedCategories.includes(c.id)}
                  onChange={() => toggleCategory(c.id)}
                />{" "}
                {c.label} ({c.id})
              </label>
            ))}
          </div>
        </div>

        <button onClick={handleStartGame}>Llamar /game/start</button>

        <div style={{ marginTop: "12px" }}>
          <p>
            <strong>gameId:</strong> {gameId ?? "(sin partida aún)"}
          </p>
          <p>
            <strong>Letra actual:</strong> {letter}
          </p>
        </div>

        <div style={{ display: "flex", gap: "16px", marginTop: "12px" }}>
          <div style={{ flex: 1 }}>
            <h3>Request enviado</h3>
            <pre style={{ background: "#f5f5f5", padding: "8px" }}>
              {startRequest ? JSON.stringify(startRequest, null, 2) : "(aún nada)"}
            </pre>
          </div>
          <div style={{ flex: 1 }}>
            <h3>Response recibido</h3>
            <pre style={{ background: "#f5f5f5", padding: "8px" }}>
              {startResponse ? JSON.stringify(startResponse, null, 2) : "(aún nada)"}
            </pre>
          </div>
        </div>
      </section>

      {/* ================= ROUND ================= */}
      <section style={{ marginTop: "24px", borderTop: "1px solid #ccc", paddingTop: "16px" }}>
        <h2>2. Enviar ronda (/api/game/round)</h2>

        <p>
          <strong>gameId usado:</strong> {gameId ?? "(primero inicia la partida)"}
        </p>
        <p>
          <strong>Letra usada:</strong> {letter}
        </p>

        <div style={{ marginTop: "8px" }}>
          {ALL_CATEGORIES.map((c) => (
            <div key={c.id} style={{ marginBottom: "4px" }}>
              <label>
                {c.label} (id {c.id}):{" "}
                <input
                  type="text"
                  value={answers[c.id]}
                  onChange={(e) => handleAnswerChange(c.id, e.target.value)}
                  style={{ width: "200px" }}
                />
              </label>
            </div>
          ))}
        </div>

        <button
          onClick={handleSubmitRound}
          disabled={!gameId}
          style={{ marginTop: "8px" }}
        >
          Llamar /game/round
        </button>

        <div style={{ display: "flex", gap: "16px", marginTop: "12px" }}>
          <div style={{ flex: 1 }}>
            <h3>Request enviado</h3>
            <pre style={{ background: "#f5f5f5", padding: "8px" }}>
              {roundRequest ? JSON.stringify(roundRequest, null, 2) : "(aún nada)"}
            </pre>
          </div>
          <div style={{ flex: 1 }}>
            <h3>Response recibido</h3>
            <pre style={{ background: "#f5f5f5", padding: "8px" }}>
              {roundResponse ? JSON.stringify(roundResponse, null, 2) : "(aún nada)"}
            </pre>
          </div>
        </div>
      </section>
    </div>
  );
}

export default ApiDebugPage;
