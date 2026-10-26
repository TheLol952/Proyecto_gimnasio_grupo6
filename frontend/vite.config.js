import { defineConfig, loadEnv } from "vite";
import vue from "@vitejs/plugin-vue";

// Configuracion de Vite para el frontend Vue.js del sistema del gimnasio.
// Localmente apunta a localhost; Docker Compose define el nombre del servicio.
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, "../", "");

  return {
    plugins: [vue()],
    server: {
      port: Number(env.FRONTEND_PORT || 5173),
      proxy: {
        "/api": {
          target: env.VITE_API_TARGET || "http://localhost:8081",
          changeOrigin: true,
        },
      },
    },
  };
});
