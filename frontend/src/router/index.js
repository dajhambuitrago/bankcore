import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth";

const LoginView = () => import("../views/LoginView.vue");
const DashboardView = () => import("../views/DashboardView.vue");
const AccountsView = () => import("../views/AccountsView.vue");
const TransferView = () => import("../views/TransferView.vue");

const routes = [
  { path: "/", redirect: "/dashboard" },
  {
    path: "/login",
    name: "login",
    component: LoginView,
    meta: { public: true },
  },
  {
    path: "/dashboard",
    name: "dashboard",
    component: DashboardView,
    meta: { requiresAuth: true },
  },
  {
    path: "/accounts",
    name: "accounts",
    component: AccountsView,
    meta: { requiresAuth: true },
  },
  {
    path: "/transfer",
    name: "transfer",
    component: TransferView,
    meta: { requiresAuth: true },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to) => {
  const auth = useAuthStore();
  // Asegurar estado desde localStorage en recargas
  if (!auth.isAuthenticated) auth.initialize();

  const isLogged = auth.isAuthenticated;

  if (to.meta?.requiresAuth && !isLogged) {
    return { path: "/login", query: { redirect: to.fullPath } };
  }

  if (to.path === "/login" && isLogged) {
    return { path: "/dashboard" };
  }

  return true;
});

export default router;
