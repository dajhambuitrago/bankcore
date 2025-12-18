<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50">
    <div class="w-full max-w-md bg-white p-8 rounded-lg shadow">
      <h1 class="text-2xl font-semibold text-gray-800 mb-6 text-center">Iniciar sesión</h1>

      <form @submit.prevent="onSubmit" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Usuario</label>
          <input v-model="username" type="text" required class="w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-300" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
          <input v-model="password" type="password" required class="w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:border-blue-300" />
        </div>

        <p v-if="error" class="text-sm text-red-600">{{ error }}</p>

        <button :disabled="loading" type="submit" class="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded transition disabled:opacity-60">
          {{ loading ? 'Ingresando…' : 'Ingresar' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const username = ref('')
const password = ref('')
const error = ref(null)
const loading = ref(false)

const onSubmit = async () => {
  error.value = null
  loading.value = true
  try {
    await auth.login(username.value, password.value)
  } catch (e) {
    error.value = auth.error || 'Credenciales inválidas'
  } finally {
    loading.value = false
  }
}
</script>
