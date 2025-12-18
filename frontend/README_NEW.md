# BankCore Frontend 🏦

Aplicación web bancaria moderna construida con **Vue 3**, **Vite**, **Pinia** y **Tailwind CSS**.

## 🚀 Características

### Autenticación y Seguridad

- ✅ Login/Register con JWT
- ✅ Token persistente en localStorage
- ✅ Axios interceptors para autorización automática
- ✅ Guards de navegación para rutas protegidas
- ✅ Logout automático en 401/403

### Funcionalidades Bancarias

- **Dashboard**: Resumen financiero con balance total y cuentas activas
- **Mis Cuentas**: Listado completo, creación de cuentas con saldo inicial
- **Transferencias**: Envío de dinero entre cuentas propias
- **Navegación Profesional**: Sidebar con iconos y estado activo

### Stack Tecnológico

- Vue 3 (Composition API)
- Vite (Dev server ultra-rápido)
- Pinia (State management)
- Vue Router (Navegación)
- Axios (HTTP client)
- Tailwind CSS (Estilos)

## 📦 Instalación

```bash
cd frontend
npm install
```

## 🔧 Desarrollo

```bash
npm run dev
```

La app se abrirá en **http://localhost:5173**

## 🏗️ Producción

```bash
npm run build
npm run preview
```

## ⚙️ Configuración

### API Base URL

Configurado en `src/libs/axios.js`:

- Desarrollo: `/api/v1` (usa proxy Vite → `http://localhost:8080`)
- Producción: ajustar según dominio

### Proxy Vite

Configurado en `vite.config.js` para evitar CORS durante desarrollo:

```javascript
proxy: {
  "/api": {
    target: "http://localhost:8080",
    changeOrigin: true,
  }
}
```

### CORS Backend

El backend Spring Boot incluye configuración CORS para `http://localhost:5173`.

## 📂 Estructura del Proyecto

```
frontend/
├── src/
│   ├── components/
│   │   └── AppLayout.vue       # Layout con sidebar
│   ├── views/
│   │   ├── LoginView.vue       # Página de login
│   │   ├── DashboardView.vue   # Resumen financiero
│   │   ├── AccountsView.vue    # Gestión de cuentas
│   │   └── TransferView.vue    # Transferencias
│   ├── stores/
│   │   └── auth.js             # Pinia store (auth)
│   ├── libs/
│   │   └── axios.js            # Cliente HTTP configurado
│   ├── router/
│   │   └── index.js            # Rutas y guards
│   ├── assets/
│   │   └── tailwind.css        # Estilos Tailwind
│   ├── App.vue                 # Componente raíz
│   └── main.js                 # Entry point
├── index.html
├── vite.config.js
├── tailwind.config.js
├── postcss.config.js
└── package.json
```

## 🔐 Rutas

| Ruta         | Componente    | Acceso    |
| ------------ | ------------- | --------- |
| `/login`     | LoginView     | Público   |
| `/dashboard` | DashboardView | Protegido |
| `/accounts`  | AccountsView  | Protegido |
| `/transfer`  | TransferView  | Protegido |

## 🎨 Diseño

- **Sidebar**: Navegación lateral azul oscuro con iconos
- **Cards**: Bordes redondeados, sombras suaves
- **Gradientes**: Fondos degradados en cards principales
- **Responsive**: Grid adaptativo para mobile/desktop
- **Loading States**: Spinners animados durante cargas
- **Feedback**: Mensajes de éxito/error inline

## 📝 Notas

- Requiere backend corriendo en `http://localhost:8080`
- Usuario de prueba: `testuser` / `password123`
- El token JWT expira según configuración del backend (por defecto 24h)
