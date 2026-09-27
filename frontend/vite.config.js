import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

// Configuracion de Vite para el frontend Vue.js del sistema del gimnasio.
// El backend Spring Boot corre en http://localhost:8081 (ver application.properties).
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: "http://localhost:8081",
        changeOrigin: true,
      },
    },
  },
});
