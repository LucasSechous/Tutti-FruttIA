import "./HomePage.css";
import { Link } from "react-router-dom";

function HomePage() {
  return (
    <div className="min-h-screen max-w-screen font-sans hero-section flex flex-col items-center justify-center text-white px-4">
      <div className="text-center max-w-2xl px-4">
        <h1 className="text-5xl md:text-6xl font-bold mb-6 animate-bounce">
          Tutti - <span style={{ color: "#F3722C" }}>FruttIA</span>
        </h1>

        <p className="text-xl mb-12 opacity-90 max-w-lg mx-auto">
          Challenge your mind with this fast-paced word game! Think fast with
          words starting with random letters.
        </p>

        <div className="flex flex-col md:flex-row gap-6 justify-center">
          <Link
            to="/game"
            className="game-btn text-white font-bold py-4 px-8 rounded-full text-lg flex items-center justify-center gap-2"
            style={{ backgroundColor: "#E63946" }}
          >
            <span>Single Player</span>
          </Link>

          <button
            className="game-btn text-white font-bold py-4 px-8 rounded-full text-lg flex items-center justify-center gap-2"
            style={{ backgroundColor: "#F3722C" }}
          >
            <span>Multiplayer</span>
          </button>
        </div>
      </div>
    </div>
  );
}

export default HomePage;
