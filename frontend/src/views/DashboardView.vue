<template>
  <AppLayout>
    <div class="p-8">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-800">Dashboard</h1>
        <p class="text-gray-600 mt-1">Resumen de tus finanzas</p>
      </div>

      <div v-if="loading" class="flex items-center justify-center py-12">
        <div class="text-center">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p class="text-gray-600 mt-4">Cargando información...</p>
        </div>
      </div>

      <div v-else>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-lg p-6 text-white">
            <div class="flex items-center justify-between mb-2">
              <p class="text-blue-100 text-sm font-medium">Balance Total</p>
              <span class="text-2xl">💰</span>
            </div>
            <p class="text-3xl font-bold">${{ totalBalance.toLocaleString('es', { minimumFractionDigits: 2 }) }}</p>
            <p class="text-blue-100 text-sm mt-2">{{ accounts.length }} cuenta{{ accounts.length !== 1 ? 's' : '' }}</p>
          </div>

          <div class="bg-white rounded-xl shadow-lg p-6">
            <div class="flex items-center justify-between mb-2">
              <p class="text-gray-600 text-sm font-medium">Cuentas Activas</p>
              <span class="text-2xl">💳</span>
            </div>
            <p class="text-3xl font-bold text-gray-800">{{ accounts.length }}</p>
            <router-link to="/accounts" class="text-blue-600 hover:text-blue-700 text-sm mt-2 inline-block font-medium">
              Ver todas →
            </router-link>
          </div>

          <div class="bg-gradient-to-br from-green-500 to-green-600 rounded-xl shadow-lg p-6 text-white">
            <div class="flex items-center justify-between mb-2">
              <p class="text-green-100 text-sm font-medium">Acción Rápida</p>
              <span class="text-2xl">⚡</span>
            </div>
            <p class="text-lg font-semibold mb-3">Transferir dinero</p>
            <router-link to="/transfer" class="inline-block px-4 py-2 bg-white text-green-600 rounded-lg font-medium hover:bg-green-50 transition">
              Nueva Transferencia
            </router-link>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-lg p-6">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-xl font-bold text-gray-800">Tus Cuentas</h2>
            <router-link to="/accounts" class="text-blue-600 hover:text-blue-700 font-medium text-sm">Ver todas</router-link>
          </div>

          <div v-if="accounts.length === 0" class="text-center py-12">
            <span class="text-6xl mb-4 inline-block">📭</span>
            <p class="text-gray-600 mb-4">No tienes cuentas aún</p>
            <router-link to="/accounts" class="inline-block px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition font-medium">
              Crear mi primera cuenta
            </router-link>
          </div>

          <div v-else class="space-y-4">
            <div v-for="account in accounts.slice(0, 3)" :key="account.id" class="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:border-blue-300 hover:shadow-md transition">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center text-xl">💳</div>
                <div>
                  <p class="font-semibold text-gray-800">{{ account.accountNumber }}</p>
                  <p class="text-sm text-gray-500">ID: {{ account.id }}</p>
                </div>
              </div>
              <div class="text-right">
                <p class="text-xl font-bold text-gray-800">${{ account.balance.toLocaleString('es', { minimumFractionDigits: 2 }) }}</p>
                <p class="text-xs text-gray-500">Disponible</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AppLayout from '../components/AppLayout.vue'
import api from '../libs/axios'

const accounts = ref([])
const loading = ref(true)

const totalBalance = computed(() => {
  return accounts.value.reduce((sum, acc) => sum + Number(acc.balance || 0), 0)
})

const fetchAccounts = async () => {
  loading.value = true
  try {
    const { data } = await api.get('/accounts')
    accounts.value = data || []
  } catch (error) {
    console.error('Error al cargar cuentas:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchAccounts()
})
</script>
