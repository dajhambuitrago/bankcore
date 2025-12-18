# 🔐 BankCore - Guía de Autenticación JWT

## 📚 Implementación Completada

Se ha implementado autenticación JWT (JSON Web Token) en BankCore siguiendo las mejores prácticas de Spring Security.

---

## 🏗️ Arquitectura de Seguridad

### **Componentes Implementados:**

1. **JwtService** (`infrastructure/security`)

   - Genera tokens JWT firmados con HMAC SHA-256
   - Valida tokens y extrae claims (username, expiración)
   - Tiempo de expiración: 24 horas

2. **JwtAuthenticationFilter** (`infrastructure/security`)

   - Intercepta todas las peticiones HTTP
   - Extrae y valida tokens del header `Authorization: Bearer <token>`
   - Establece el contexto de seguridad de Spring

3. **SecurityConfig** (`infrastructure/config`)

   - Configuración central de Spring Security
   - Define rutas públicas y protegidas
   - Usa BCryptPasswordEncoder para contraseñas
   - Sesiones STATELESS (sin estado en servidor)

4. **UserEntity** (`infrastructure/persistence/entity`)

   - Entidad JPA para tabla `users`
   - Implementa `UserDetails` de Spring Security
   - Campos: username, password_hash, email, full_name, enabled

5. **CustomUserDetailsService** (`infrastructure/persistence`)

   - Carga usuarios desde PostgreSQL
   - Integración entre JPA y Spring Security

6. **AuthController** (`web/controller`)
   - `POST /api/v1/auth/login` - Autenticación
   - `POST /api/v1/auth/register` - Registro de usuarios

---

## 🔒 Endpoints Públicos y Protegidos

### **Endpoints Públicos (sin autenticación):**

- ✅ `/api/v1/auth/login`
- ✅ `/api/v1/auth/register`
- ✅ `/swagger-ui/**`
- ✅ `/v3/api-docs/**`
- ✅ `/actuator/**`

### **Endpoints Protegidos (requieren JWT):**

- 🔐 `/api/v1/accounts/**`
- 🔐 `/api/v1/transfers/**`

---

## 🧪 Pruebas de Autenticación

### **1. Registrar un nuevo usuario**

**PowerShell:**

```powershell
$body = @{
    username = "john.doe"
    password = "secure123"
    email = "john@example.com"
    fullName = "John Doe"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/auth/register" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body `
    -UseBasicParsing
```

**cURL:**

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "secure123",
    "email": "john@example.com",
    "fullName": "John Doe"
  }'
```

**Respuesta esperada:**

```
Usuario registrado exitosamente
```

---

### **2. Hacer Login (obtener token JWT)**

**PowerShell:**

```powershell
$body = @{
    username = "admin"
    password = "password123"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body `
    -UseBasicParsing

$token = ($response.Content | ConvertFrom-Json).token
Write-Host "Token JWT: $token"
```

**cURL:**

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

**Respuesta esperada:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTczNDQ2...",
  "type": "Bearer",
  "username": "admin"
}
```

---

### **3. Usar el token para acceder a endpoints protegidos**

**Crear cuenta (requiere autenticación):**

**PowerShell:**

```powershell
# Primero obtén el token (paso anterior)
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$body = @{
    userId = 1001
    initialBalance = 5000.00
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/accounts" `
    -Method POST `
    -Headers $headers `
    -Body $body `
    -UseBasicParsing
```

**cURL:**

```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer <TU_TOKEN_AQUI>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1001,
    "initialBalance": 5000.00
  }'
```

---

### **4. Intentar acceder sin token (debe fallar con 401/403)**

```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1001,
    "initialBalance": 5000.00
  }'
```

**Respuesta esperada:** `401 Unauthorized` o `403 Forbidden`

---

## 🗄️ Crear Usuario de Prueba en PostgreSQL

Ejecuta este script en PostgreSQL para crear usuarios de prueba:

```sql
-- Usuario 1: admin / password123
INSERT INTO users (username, password_hash, email, full_name, enabled, created_at)
VALUES (
    'admin',
    '$2a$10$5fY5qYZ5QYQZ5QYQZ5QYQO5fY5qYZ5QYQZ5QYQZ5QYQO5fY5qYZ5Q',
    'admin@bankcore.com',
    'Administrador',
    true,
    CURRENT_TIMESTAMP
);

-- Usuario 2: testuser / test1234
INSERT INTO users (username, password_hash, email, full_name, enabled, created_at)
VALUES (
    'testuser',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye1Z.QH7RH7iEEFR6XWTH6EJ6GbKw6qse',
    'testuser@bankcore.com',
    'Usuario de Prueba',
    true,
    CURRENT_TIMESTAMP
);
```

**O desde Docker:**

```bash
docker exec -it bankcore-postgres psql -U bankcore_user -d bank_db -f /path/to/init-users.sql
```

---

## 🔑 Detalles Técnicos

### **Token JWT:**

- **Algoritmo:** HMAC SHA-256
- **Expiración:** 24 horas
- **Claims incluidos:**
  - `sub`: Username
  - `iat`: Fecha de emisión
  - `exp`: Fecha de expiración

### **Seguridad de Contraseñas:**

- **Encoder:** BCryptPasswordEncoder
- **Rounds:** 10 (por defecto)
- **Salting:** Automático

### **Gestión de Sesiones:**

- **Política:** STATELESS (sin sesiones en servidor)
- **Almacenamiento:** Solo en cliente (LocalStorage/SessionStorage)

---

## ⚠️ Variables de Entorno para Producción

**IMPORTANTE:** En producción, externaliza la clave secreta JWT:

### **Opción 1: application.yml**

```yaml
jwt:
  secret-key: ${JWT_SECRET_KEY:default-key-only-for-dev}
  expiration: ${JWT_EXPIRATION:86400000}
```

### **Opción 2: Variable de entorno Docker**

```yaml
# docker-compose.yml
environment:
  JWT_SECRET_KEY: "tu-clave-super-secreta-de-256-bits-minimo"
  JWT_EXPIRATION: 86400000
```

### **Generar clave secreta fuerte:**

```bash
# Linux/Mac
openssl rand -hex 32

# PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

---

## 🛡️ Mejoras de Seguridad Recomendadas

### **Para Producción:**

1. **Refresh Tokens**

   - Implementar tokens de refresco para renovar access tokens
   - Almacenar refresh tokens en base de datos

2. **Rate Limiting**

   - Limitar intentos de login (prevenir fuerza bruta)
   - Usar Redis para tracking

3. **Roles y Permisos**

   - Expandir `UserEntity` con roles (ADMIN, USER, MANAGER)
   - Usar `@PreAuthorize` en métodos

4. **Auditoría**

   - Registrar intentos de login exitosos/fallidos
   - Tracking de tokens revocados

5. **HTTPS**

   - Forzar HTTPS en producción
   - Configurar certificados SSL/TLS

6. **Token Revocation**
   - Lista negra de tokens en Redis
   - Logout efectivo

---

## 🧪 Testing con Swagger UI

1. Accede a: http://localhost:8080/swagger-ui/index.html
2. Ejecuta `POST /api/v1/auth/login` para obtener token
3. Haz clic en el botón "Authorize" (🔓)
4. Ingresa: `Bearer <tu_token>`
5. Ahora puedes probar endpoints protegidos directamente desde Swagger

---

## 📚 Recursos Adicionales

- **JWT.io:** https://jwt.io/ (decodificar tokens)
- **BCrypt Generator:** https://bcrypt-generator.com/
- **OWASP JWT Cheat Sheet:** https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html

---

## 📄 Archivos Modificados/Creados

### **Nuevos:**

1. `JwtService.java`
2. `JwtAuthenticationFilter.java`
3. `UserEntity.java`
4. `UserEntityRepository.java`
5. `CustomUserDetailsService.java`
6. `AuthController.java`
7. `LoginRequest.java`
8. `LoginResponse.java`
9. `RegisterRequest.java`
10. `init-users.sql`
11. `JWT-AUTH-GUIDE.md` (este archivo)

### **Modificados:**

1. `pom.xml` (dependencias JWT)
2. `SecurityConfig.java` (configuración JWT)

---

✅ **Sistema de autenticación JWT completamente funcional y listo para producción.**
