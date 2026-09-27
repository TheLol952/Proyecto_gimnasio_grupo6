import axios from "axios";

// Cliente REST hacia el backend Spring Boot.
// En desarrollo, Vite hace proxy de /api -> http://localhost:8081 (ver vite.config.js).
const api = axios.create({
  baseURL: "/api",
  headers: { "Content-Type": "application/json" },
});

export default api;
