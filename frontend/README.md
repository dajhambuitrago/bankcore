# BankCore Frontend (Vue 3 + Vite)

Cliente web mínimo para autenticación JWT contra el backend de BankCore.

## Requisitos

- Node.js 18+ y npm
- Backend corriendo en http://localhost:8080

## Instalación

```bash
cd frontend
npm install
```

## Desarrollo

```bash
npm run dev
```

Se abrirá en http://localhost:5173

## Producción

```bash
npm run build
npm run preview
```

## Configuración

- Base API: configurada en `src/libs/axios.js` como `http://localhost:8080/api/v1`.
- Tailwind: configurado en `tailwind.config.js` y `postcss.config.js`. Estilos en `src/assets/tailwind.css`.
- Autenticación: Pinia store en `src/stores/auth.js`. El token se persiste en `localStorage`.
- Rutas: `/login` (pública) y `/dashboard` (protegida) con guardas globales en `src/router/index.js`.

## Notas

- Si el backend no tiene CORS habilitado para `http://localhost:5173`, podrás necesitar configurar CORS en el backend o un proxy en Vite.
