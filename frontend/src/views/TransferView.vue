<template>
  <AppLayout>
    <div class="p-8">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-800">Nueva Transferencia</h1>
        <p class="text-gray-600 mt-1">Envía dinero a cualquier cuenta</p>
      </div>

      <!-- Loading Accounts -->
      <div v-if="loadingAccounts" class="flex items-center justify-center py-12">
        <div class="text-center">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p class="text-gray-600 mt-4">Cargando cuentas...</p>
        </div>
      </div>

      <!-- No Accounts to Send From -->
      <div v-else-if="accounts.length === 0" class="bg-white rounded-xl shadow-lg p-12 text-center">
        <span class="text-6xl inline-block mb-4">⚠️</span>
        <h3 class="text-xl font-bold text-gray-800 mb-2">No tienes cuentas</h3>
        <p class="text-gray-600 mb-6">Crea una cuenta para poder hacer transferencias</p>
        <router-link
          to="/accounts"
          class="inline-block px-6 py-3 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition"
        >
          Ir a Mis Cuentas
        </router-link>
      </div>

      <!-- Transfer Form -->
      <div v-else class="max-w-2xl mx-auto">
        <div class="bg-white rounded-xl shadow-lg p-8 max-h-[80vh] overflow-y-auto">
          <form @submit.prevent="submitTransfer" class="space-y-6">
            <!-- Source Account -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">Cuenta Origen</label>
              <select
                v-model="form.sourceAccountId"
                required
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
              >
                <option :value="null" disabled>Selecciona tu cuenta</option>
                <option v-for="acc in accounts" :key="acc.id" :value="acc.id">
                  {{ acc.accountNumber }} - ${{ acc.balance.toLocaleString('es', { minimumFractionDigits: 2 }) }}
                </option>
              </select>
            </div>

            <!-- Target Account Search -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">Cuenta Destino</label>
              <div class="relative">
                <input
                  v-model="searchQuery"
                  @input="searchAccount"
                  type="text"
                  placeholder="Busca por número de cuenta (ej: ACC-1234567890-ABC)"
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
                />
                
                <!-- Search Results Dropdown -->
                <div v-if="showSearchResults" class="absolute top-full left-0 right-0 mt-2 bg-white border border-gray-300 rounded-lg shadow-xl z-50 max-h-64 overflow-y-auto">
                  <div v-if="searchLoading" class="p-4 text-center text-gray-600">
                    <div class="inline-block animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
                  </div>
                  
                  <div v-else-if="searchError" class="p-4 text-red-600 text-sm">
                    ❌ {{ searchError }}
                  </div>
                  
                  <div v-else-if="searchResults" class="p-4 cursor-pointer hover:bg-blue-50 border-b border-gray-200 last:border-b-0">
                    <div>
                      <p class="font-semibold text-gray-800">{{ searchResults.accountNumber }}</p>
                      <p class="text-sm text-gray-500">ID: {{ searchResults.id }}</p>
                    </div>
                    <button
                      type="button"
                      @click="selectTargetAccount(searchResults)"
                      class="w-full mt-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition text-sm font-medium"
                    >
                      Seleccionar
                    </button>
                  </div>

                  <div v-if="!searchLoading && !searchResults && !searchError && searchQuery" class="p-4 text-center text-gray-600 text-sm">
                    Ninguna cuenta encontrada
                  </div>
                </div>
              </div>
              <p v-if="targetAccountSelected" class="text-xs text-green-600 mt-2">
                ✅ Cuenta destino seleccionada
              </p>
              <div v-if="targetAccountSelected && targetAccount" class="mt-3 p-3 bg-green-100 border border-green-300 rounded-lg">
                <p class="text-sm font-semibold text-green-800">{{ targetAccount.accountNumber }}</p>
                <p class="text-xs text-green-600">Cuenta destino confirmada ✅</p>
              </div>
            </div>

            <!-- Amount -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">Monto a Transferir (USD)</label>
              <input
                v-model.number="form.amount"
                type="number"
                placeholder="0.00"
                step="0.01"
                min="0.01"
                required
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
              />
              <p v-if="sourceAccount" class="text-xs text-gray-500 mt-2">
                Disponible: ${{ sourceAccount.balance.toLocaleString('es', { minimumFractionDigits: 2 }) }}
              </p>
              <p v-if="insufficientFunds" class="text-xs text-red-600 mt-2">
                ❌ Fondos insuficientes
              </p>
            </div>

            <!-- Summary -->
            <div v-if="form.sourceAccountId && targetAccountSelected && form.amount > 0 && !insufficientFunds" class="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <h3 class="text-sm font-semibold text-blue-900 mb-3">📋 Resumen de Transferencia</h3>
              <div class="space-y-2 text-sm">
                <div class="flex justify-between">
                  <span class="text-gray-600">De:</span>
                  <span class="font-medium text-gray-800">{{ sourceAccount?.accountNumber }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-gray-600">A:</span>
                  <span class="font-medium text-gray-800">{{ targetAccount?.accountNumber }}</span>
                </div>
                <div v-if="targetAccount?.userId !== sourceAccount?.userId" class="flex justify-between bg-green-100 px-3 py-2 rounded border border-green-300">
                  <span class="text-gray-600">Tipo:</span>
                  <span class="font-medium text-green-700">💳 Transferencia Externa</span>
                </div>
                <div class="flex justify-between pt-2 border-t border-blue-200">
                  <span class="text-gray-600">Monto:</span>
                  <span class="font-bold text-blue-900 text-lg">${{ form.amount.toLocaleString('es', { minimumFractionDigits: 2 }) }}</span>
                </div>
              </div>
            </div>

            <!-- Error/Success Messages -->
            <div v-if="errorMessage" class="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700 text-sm">
              ❌ {{ errorMessage }}
            </div>
            <div v-if="successMessage" class="bg-green-50 border border-green-200 rounded-lg p-4 text-green-700 text-sm">
              ✅ {{ successMessage }}
            </div>

            <!-- Buttons -->
            <div class="flex gap-4">
              <button
                type="submit"
                :disabled="transferring || !canSubmit"
                class="flex-1 px-6 py-3 bg-green-600 text-white rounded-lg font-semibold hover:bg-green-700 transition disabled:opacity-60 disabled:cursor-not-allowed"
              >
                {{ transferring ? 'Procesando...' : '💸 Transferir Ahora' }}
              </button>
              <button
                type="button"
                @click="resetForm"
                class="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg font-semibold hover:bg-gray-50 transition"
              >
                Limpiar
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/AppLayout.vue'
import api from '../libs/axios'

const router = useRouter()

const accounts = ref([])
const loadingAccounts = ref(true)
const transferring = ref(false)
const errorMessage = ref(null)
const successMessage = ref(null)

const searchQuery = ref('')
const searchResults = ref(null)
const searchLoading = ref(false)
const searchError = ref(null)
const targetAccountSelected = ref(false)

const form = ref({
  sourceAccountId: null,
  targetAccountId: null,
  amount: 0,
})

let searchTimeout = null

const sourceAccount = computed(() => accounts.value.find(a => a.id === form.value.sourceAccountId))
const targetAccount = computed(() => searchResults.value)

const insufficientFunds = computed(() => {
  return sourceAccount.value && form.value.amount > sourceAccount.value.balance
})

const showSearchResults = computed(() => {
  return searchQuery.value.length > 0 && (searchLoading.value || searchResults.value || searchError.value)
})

const canSubmit = computed(() => {
  return form.value.sourceAccountId &&
         targetAccountSelected.value &&
         form.value.amount > 0 &&
         !insufficientFunds.value &&
         form.value.targetAccountId &&
         form.value.targetAccountId !== form.value.sourceAccountId
})

const fetchAccounts = async () => {
  loadingAccounts.value = true
  try {
    const { data } = await api.get('/accounts')
    accounts.value = data || []
  } catch (error) {
    console.error('Error al cargar cuentas:', error)
  } finally {
    loadingAccounts.value = false
  }
}

const searchAccount = async () => {
  if (!searchQuery.value.trim()) {
    searchResults.value = null
    searchError.value = null
    return
  }

  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(async () => {
    searchLoading.value = true
    searchError.value = null
    searchResults.value = null

    try {
      const { data } = await api.get('/accounts/search', {
        params: { accountNumber: searchQuery.value },
      })
      searchResults.value = data
    } catch (error) {
      if (error?.response?.status === 404) {
        searchError.value = 'Cuenta no encontrada'
      } else {
        searchError.value = error?.response?.data?.detail || 'Error al buscar cuenta'
      }
      searchResults.value = null
    } finally {
      searchLoading.value = false
    }
  }, 500) // Debounce 500ms
}

const selectTargetAccount = (account) => {
  form.value.targetAccountId = account.id
  targetAccountSelected.value = true
  searchQuery.value = '' // Cierra el dropdown
}

const submitTransfer = async () => {
  errorMessage.value = null
  successMessage.value = null
  transferring.value = true

  try {
    const { data } = await api.post('/transfers', {
      sourceAccountId: form.value.sourceAccountId,
      targetAccountId: form.value.targetAccountId,
      amount: form.value.amount,
    })

    successMessage.value = `✅ Transferencia exitosa! Nuevo saldo: $${data.sourceBalance}`
    
    // Refresh accounts
    await fetchAccounts()
    
    // Reset and redirect
    setTimeout(() => {
      resetForm()
      router.push('/accounts')
    }, 2000)
  } catch (error) {
    errorMessage.value = error?.response?.data?.detail || error.message || 'Error al procesar la transferencia'
  } finally {
    transferring.value = false
  }
}

const resetForm = () => {
  form.value = {
    sourceAccountId: null,
    targetAccountId: null,
    amount: 0,
  }
  searchQuery.value = ''
  searchResults.value = null
  targetAccountSelected.value = false
  errorMessage.value = null
  successMessage.value = null
}

onMounted(() => {
  fetchAccounts()
})
</script>
