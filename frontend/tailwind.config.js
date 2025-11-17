/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          100: "#FAD4D8",
          200: "#F5A9B0",
          300: "#F07E88",
          400: "#EB5360",
          500: "#E63946",
          600: "#C1121F",
          700: "#9B0E19",
          800: "#760B13",
          900: "#51070D",
        },
        secondary: {
          100: "#FFE5D9",
          200: "#FFCBB3",
          300: "#FFB18D",
          400: "#FF9767",
          500: "#F3722C",
          600: "#D85C1F",
          700: "#BD4612",
          800: "#A23005",
          900: "#871A00",
        },
      },
      fontFamily: {
        sans: ["Inter", "system-ui", "sans-serif"],
      },
    },
  },
  plugins: [],
};
