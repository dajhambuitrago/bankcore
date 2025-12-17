# 🐳 BankCore - Dockerized Application

## 📦 Arquitectura de Contenedores

Este proyecto utiliza **Docker Multi-Stage Build** para optimizar el tamaño de la imagen final y **Docker Compose** para orquestar los servicios.

### Servicios:

- **postgres** (PostgreSQL 16): Base de datos
- **app-bankcore** (Spring Boot 3.5.1): Aplicación REST API

---

## 🚀 Comandos Docker

### **1. Construir las imágenes**

```bash
docker-compose build
```

### **2. Levantar todos los servicios**

```bash
docker-compose up -d
```

### **3. Ver logs en tiempo real**

```bash
# Todos los servicios
docker-compose logs -f

# Solo la aplicación
docker-compose logs -f app-bankcore

# Solo PostgreSQL
docker-compose logs -f postgres
```

### **4. Verificar estado de los contenedores**

```bash
docker-compose ps
```

### **5. Detener todos los servicios**

```bash
docker-compose down
```

### **6. Detener y eliminar volúmenes (⚠️ borra datos)**

```bash
docker-compose down -v
```

### **7. Reconstruir sin cache**

```bash
docker-compose build --no-cache
docker-compose up -d
```

---

## 🔍 Healthchecks

### **PostgreSQL**

```bash
docker exec bankcore-postgres pg_isready -U bankcore_user -d bank_db
```

### **Spring Boot (Actuator)**

```bash
curl http://localhost:8080/actuator/health
```

**Respuesta esperada:**

```json
{
  "status": "UP"
}
```

---

## 📡 Endpoints Disponibles

| Endpoint                                      | Descripción           |
| --------------------------------------------- | --------------------- |
| `http://localhost:8080/api/v1/accounts`       | Crear cuentas         |
| `http://localhost:8080/api/v1/transfers`      | Transferir fondos     |
| `http://localhost:8080/swagger-ui/index.html` | Swagger UI            |
| `http://localhost:8080/v3/api-docs`           | OpenAPI JSON          |
| `http://localhost:8080/actuator/health`       | Health Check          |
| `http://localhost:8080/actuator/info`         | Info de la aplicación |
| `http://localhost:8080/actuator/metrics`      | Métricas              |

---

## 🧪 Probar la API (desde host)

### **Crear Cuenta:**

```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1001,
    "initialBalance": 5000.00
  }'
```

### **Transferir Fondos:**

```bash
curl -X POST http://localhost:8080/api/v1/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "sourceAccountId": 1,
    "targetAccountId": 2,
    "amount": 500.00
  }'
```

---

## 🐘 Conectar a PostgreSQL (desde host)

```bash
# Usando psql
psql -h localhost -p 5432 -U bankcore_user -d bank_db

# Usando Docker
docker exec -it bankcore-postgres psql -U bankcore_user -d bank_db
```

**Credenciales:**

- Host: `localhost`
- Puerto: `5432`
- Usuario: `bankcore_user`
- Contraseña: `bankcore_pass`
- Base de datos: `bank_db`

---

## 📊 Optimizaciones Implementadas

### **Dockerfile Multi-Stage Build:**

- ✅ **Stage 1 (Build)**: Maven + JDK 21 (imagen grande, solo para compilar)
- ✅ **Stage 2 (Run)**: JRE 21 Alpine (imagen pequeña ~180MB)
- ✅ Usuario no-root (`spring:spring`) para seguridad
- ✅ Healthcheck integrado
- ✅ Variables de entorno configurables

### **Docker Compose:**

- ✅ `depends_on` con `condition: service_healthy` (espera a PostgreSQL)
- ✅ Network aislada (`bankcore-network`)
- ✅ Volumen persistente para PostgreSQL
- ✅ Restart policy: `unless-stopped`
- ✅ Variables de entorno externalizadas

### **.dockerignore:**

- ✅ Excluye `target/`, `.git/`, IDEs, logs
- ✅ Reduce tamaño del contexto de build
- ✅ Acelera el proceso de construcción

---

## 🔧 Variables de Entorno

Puedes sobrescribir las variables en `docker-compose.yml`:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://bankcore-postgres:5432/bank_db
  SPRING_DATASOURCE_USERNAME: bankcore_user
  SPRING_DATASOURCE_PASSWORD: bankcore_pass
  SPRING_JPA_HIBERNATE_DDL_AUTO: update
  JAVA_OPTS: "-Xms256m -Xmx512m"
```

---

## 📈 Monitoreo

### **Ver uso de recursos:**

```bash
docker stats
```

### **Inspeccionar contenedor:**

```bash
docker inspect bankcore-app
```

### **Ver redes:**

```bash
docker network ls
docker network inspect bankore_bankcore-network
```

---

## 🛠️ Troubleshooting

### **Error: Puerto 8080 ocupado**

```bash
# Windows
netstat -ano | findstr :8080

# Matar proceso
taskkill /PID <PID> /F
```

### **Error: No se conecta a PostgreSQL**

```bash
# Verificar que postgres esté healthy
docker-compose ps

# Ver logs de postgres
docker-compose logs postgres
```

### **Reconstruir desde cero:**

```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

---

## 📝 Notas de Producción

### **Antes de desplegar a producción:**

1. **Cambiar contraseñas** en `docker-compose.yml`
2. **Habilitar HTTPS** (reverse proxy con Nginx/Traefik)
3. **Configurar Spring Security** (quitar `permitAll`)
4. **Usar secrets** en lugar de variables de entorno
5. **Configurar límites de recursos:**
   ```yaml
   deploy:
     resources:
       limits:
         cpus: "1"
         memory: 512M
   ```
6. **Configurar backups** de PostgreSQL
7. **Usar registry privado** para las imágenes
8. **Implementar monitoring** (Prometheus + Grafana)

---

## 📚 Tecnologías

- **Java 21** (Eclipse Temurin)
- **Spring Boot 3.5.1**
- **PostgreSQL 16**
- **Docker Multi-Stage Build**
- **Docker Compose**
- **Spring Boot Actuator**
- **SpringDoc OpenAPI 2.7.0**

---

## 📄 Licencia

MIT License - BankCore 2025
