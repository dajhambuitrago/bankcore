<template>
  <AppLayout>
    <div class="p-8">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-800">Mis Cuentas</h1>
        <p class="text-gray-600 mt-1">Administra tus cuentas bancarias</p>
      </div>

      <!-- Create Account Section -->
      <div class="bg-gradient-to-r from-blue-500 to-blue-600 rounded-xl shadow-lg p-6 mb-8 text-white">
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-2xl font-bold">Crear Nueva Cuenta</h2>
            <p class="text-blue-100 mt-1">Abre una cuenta con saldo inicial</p>
          </div>
          <button
            @click="showCreateForm = !showCreateForm"
            class="px-6 py-3 bg-white text-blue-600 rounded-lg font-semibold hover:bg-blue-50 transition"
          >
            {{ showCreateForm ? 'Cancelar' : '+ Nueva Cuenta' }}
          </button>
        </div>

        <form v-if="showCreateForm" @submit.prevent="createAccount" class="mt-6 bg-white bg-opacity-20 backdrop-blur rounded-lg p-4">
          <div class="flex gap-4 items-end">
            <div class="flex-1">
              <label class="block text-sm font-medium mb-2">Saldo Inicial (USD)</label>
              <input
                v-model.number="newAccountBalance"
                type="number"
                placeholder="0.00"
                step="0.01"
                min="0"
                required
                class="w-full px-4 py-3 rounded-lg text-gray-800 focus:outline-none focus:ring-2 focus:ring-white"
              />
            </div>
            <button
              type="submit"
              :disabled="creating"
              class="px-8 py-3 bg-white text-blue-600 rounded-lg font-semibold hover:bg-blue-50 transition disabled:opacity-60"
            >
              {{ creating ? 'Creando...' : 'Crear' }}
            </button>
          </div>
          <p v-if="createError" class="text-white text-sm mt-2">❌ {{ createError }}</p>
          <p v-if="createSuccess" class="text-white text-sm mt-2">✅ {{ createSuccess }}</p>
        </form>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-12">
        <div class="text-center">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p class="text-gray-600 mt-4">Cargando cuentas...</p>
        </div>
      </div>

      <!-- Accounts List -->
      <div v-else>
        <div v-if="accounts.length === 0" class="bg-white rounded-xl shadow-lg p-12 text-center">
          <span class="text-6xl inline-block mb-4">📭</span>
          <h3 class="text-xl font-bold text-gray-800 mb-2">No tienes cuentas</h3>
          <p class="text-gray-600 mb-6">Crea tu primera cuenta para comenzar a usar BankCore</p>
          <button
            @click="showCreateForm = true"
            class="px-6 py-3 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition"
          >
            Crear Primera Cuenta
          </button>
        </div>

        <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div
            v-for="account in accounts"
            :key="account.id"
            class="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition"
          >
            <div class="bg-gradient-to-r from-blue-500 to-blue-600 p-6 text-white">
              <div class="flex items-center justify-between mb-4">
                <span class="text-3xl">💳</span>
                <span class="text-xs bg-white bg-opacity-30 px-3 py-1 rounded-full font-medium">
                  ID: {{ account.id }}
                </span>
              </div>
              <p class="text-blue-100 text-sm font-medium">Número de Cuenta</p>
              <p class="text-xl font-bold mb-1">{{ account.accountNumber }}</p>
            </div>

            <div class="p-6">
              <div class="flex items-baseline justify-between mb-4">
                <div>
                  <p class="text-gray-600 text-sm font-medium">Saldo Disponible</p>
                  <p class="text-3xl font-bold text-gray-800">
                    ${{ account.balance.toLocaleString('es', { minimumFractionDigits: 2 }) }}
                  </p>
                </div>
              </div>

              <div class="flex gap-2">
                <button
                  @click="selectForTransfer(account)"
                  class="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition font-medium text-sm"
                >
                  💸 Transferir
                </button>
                <button class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition font-medium text-sm">
                  Ver Detalle
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/AppLayout.vue'
import api from '../libs/axios'

const router = useRouter()
const accounts = ref([])
const loading = ref(true)
const showCreateForm = ref(false)
const newAccountBalance = ref(0)
const creating = ref(false)
const createError = ref(null)
const createSuccess = ref(null)

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

const createAccount = async () => {
  createError.value = null
  createSuccess.value = null
  creating.value = true

  try {
    const { data } = await api.post('/accounts', null, {
      params: { initialBalance: newAccountBalance.value },
    })
    createSuccess.value = `Cuenta ${data.accountNumber} creada exitosamente`
    newAccountBalance.value = 0
    showCreateForm.value = false
    await fetchAccounts()
  } catch (error) {
    createError.value = error?.response?.data?.detail || 'Error al crear cuenta'
  } finally {
    creating.value = false
  }
}

const selectForTransfer = (account) => {
  router.push({ path: '/transfer', query: { from: account.id } })
}

onMounted(() => {
  fetchAccounts()
})
</script>
