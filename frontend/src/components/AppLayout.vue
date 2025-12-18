<template>
  <div class="flex h-screen bg-gray-50">
    <!-- Sidebar -->
    <aside class="w-64 bg-gradient-to-b from-blue-900 to-blue-800 text-white flex flex-col shadow-xl">
      <!-- Logo -->
      <div class="p-6 border-b border-blue-700">
        <h1 class="text-2xl font-bold">🏦 BankCore</h1>
        <p class="text-blue-200 text-sm mt-1">Tu banco digital</p>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 px-4 py-6 space-y-2">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 hover:bg-blue-700"
          :class="isActive(item.path) ? 'bg-blue-700 shadow-lg' : ''"
        >
          <span class="text-xl">{{ item.icon }}</span>
          <span class="font-medium">{{ item.label }}</span>
        </router-link>
      </nav>

      <!-- User Info -->
      <div class="p-4 border-t border-blue-700">
        <div class="flex items-center gap-3 px-4 py-3 bg-blue-700 rounded-lg">
          <div class="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center text-lg font-bold">
            {{ userInitial }}
          </div>
          <div class="flex-1 min-w-0">
            <p class="font-medium truncate">{{ userName }}</p>
            <p class="text-blue-200 text-xs">Usuario activo</p>
          </div>
        </div>
        <button
          @click="logout"
          class="w-full mt-3 px-4 py-2 bg-red-600 hover:bg-red-700 rounded-lg transition font-medium"
        >
          Salir
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 overflow-y-auto">
      <slot />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const auth = useAuthStore()

const navItems = [
  { path: '/dashboard', label: 'Dashboard', icon: '📊' },
  { path: '/accounts', label: 'Mis Cuentas', icon: '💳' },
  { path: '/transfer', label: 'Transferir', icon: '💸' },
]

const userName = computed(() => auth.user?.username || 'Usuario')
const userInitial = computed(() => userName.value.charAt(0).toUpperCase())

const isActive = (path) => route.path === path

const logout = () => auth.logout()
</script>
