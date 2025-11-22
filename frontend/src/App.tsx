// import { useEffect, useState } from "react";
// import { gameService } from "./services/gameService";
// import type{ Category } from "./types/game";
// import "./App.css";

// function App() {
//   const [categories, setCategories] = useState<Category[]>([]);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState<string | null>(null);

//   useEffect(() => {
//     const fetchCategories = async () => {
//       try {
//         setLoading(true);
//         setError(null);
//         const data = await gameService.getCategories();
//         setCategories(data);
//       } catch (err) {
//         console.error(err);
//         setError("No se pudieron cargar las categorías 😢");
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchCategories();
//   }, []);

//   return (
//     <div className="app-container">
//       <h1>Tutti-FruttIA 🧠🍓</h1>

//       {loading && <p>Cargando categorías...</p>}
//       {error && <p style={{ color: "red" }}>{error}</p>}

//       {!loading && !error && (
//         <ul>
//           {categories.map((c) => (
//             <li key={c.id}>{c.name}</li>
//           ))}
//         </ul>
//       )}
//     </div>
//   );
// }

// export default App
// ------------------------------------------------------------------


import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "./pages/HomePage";
import GamePage from "./pages/GamePage";
import ApiDebugPage from "./pages/ApiDebugPage";  // 👈 importar la nueva

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/game" element={<GamePage />} />
        <Route path="/debug-api" element={<ApiDebugPage />} />  {/* 👈 ruta nueva */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;

