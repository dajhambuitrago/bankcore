import axios from "axios";

const api = axios.create({
  // Usar ruta relativa para aprovechar el proxy de Vite (/api -> backend)
  baseURL: "/api/v1",
  timeout: 15000,
});

// Request interceptor: agrega Authorization si hay token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers = config.headers || {};
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor: desloguea en 401/403
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;
    if (status === 401 || status === 403) {
      // Limpieza rápida sin acoplar al store para evitar ciclos
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      // Redirigir a login
      if (location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

export default api;
