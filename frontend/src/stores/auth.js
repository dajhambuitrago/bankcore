import { defineStore } from "pinia";
import router from "../router";
import api from "../libs/axios";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: null,
    user: null,
    isAuthenticated: false,
    loading: false,
    error: null,
  }),

  actions: {
    async login(username, password) {
      try {
        this.loading = true;
        this.error = null;

        const { data } = await api.post("/auth/login", { username, password });
        const token = data?.token;
        if (!token) throw new Error("Token no recibido");

        this.token = token;
        // Guardar algún identificador de usuario si viene (userId o username)
        const user = data?.userId
          ? { id: data.userId, username }
          : { username: data?.username || username };
        this.user = user;
        this.isAuthenticated = true;

        // Persistir
        localStorage.setItem("token", token);
        localStorage.setItem("user", JSON.stringify(user));

        // Configurar header por si hay llamadas inmediatas
        api.defaults.headers.common["Authorization"] = `Bearer ${token}`;

        // Redirigir
        router.push({ path: "/dashboard" });
      } catch (err) {
        this.error =
          err?.response?.data?.message ||
          err.message ||
          "Error al iniciar sesión";
        throw err;
      } finally {
        this.loading = false;
      }
    },

    logout() {
      this.token = null;
      this.user = null;
      this.isAuthenticated = false;
      this.error = null;

      localStorage.removeItem("token");
      localStorage.removeItem("user");

      // Remover header por limpieza
      delete api.defaults.headers.common["Authorization"];

      router.push({ path: "/login" });
    },

    initialize() {
      const token = localStorage.getItem("token");
      const userRaw = localStorage.getItem("user");
      if (token) {
        this.token = token;
        this.isAuthenticated = true;
        try {
          this.user = userRaw ? JSON.parse(userRaw) : null;
        } catch {
          this.user = null;
        }
        api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
      } else {
        this.token = null;
        this.user = null;
        this.isAuthenticated = false;
      }
    },
  },
});
