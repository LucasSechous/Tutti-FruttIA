import type{ ButtonHTMLAttributes, ReactNode } from "react";

type Variant = "primary" | "secondary" | "outline";

interface GameButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
  icon?: ReactNode;    // acá podés pasar un <i> o un icono de librería
  children: ReactNode;
}

const baseClasses =
  "flex items-center justify-center px-6 py-3 rounded-full font-bold transition-all duration-200 shadow-md disabled:opacity-50 disabled:cursor-not-allowed";

const variantClasses: Record<Variant, string> = {
  primary: "bg-primary-600 text-white hover:bg-primary-700 hover:shadow-lg",
  secondary: "bg-secondary-500 text-white hover:bg-secondary-600 hover:shadow-lg",
  outline:
    "border-2 border-primary-600 text-primary-600 bg-transparent hover:bg-primary-100",
};

export function GameButton({
  variant = "primary",
  icon,
  children,
  className = "",
  ...props
}: GameButtonProps) {
  return (
    <button
      className={`${baseClasses} ${variantClasses[variant]} ${className}`}
      {...props}
    >
      {icon && <span className="mr-2">{icon}</span>}
      {children}
    </button>
  );
}