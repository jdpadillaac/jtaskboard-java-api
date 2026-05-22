# JTaskboard

API REST para gestión de tareas estilo Jira, construida con **Spring Boot 4** y **arquitectura hexagonal (Ports & Adapters)** organizada por feature. Incluye **autenticación basada en JWT**: los endpoints de tareas están protegidos y requieren un token de sesión.

---

## 📋 Tabla de contenidos

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Configuración de variables de entorno](#configuración-de-variables-de-entorno)
- [Levantar en local](#levantar-en-local)
- [Ejecutar pruebas](#ejecutar-pruebas)
- [Autenticación](#autenticación)
- [API Reference](#api-reference)
- [Modelo de datos](#modelo-de-datos)
- [Despliegue con Docker](#despliegue-con-docker)
- [Despliegue en Render](#despliegue-en-render)

---

## 🛠 Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 4.0.6 | Framework principal |
| Spring Data JPA | — | Acceso a datos |
| Spring Validation | — | Validación de requests |
| Spring Security | — | Protección de endpoints y filtro JWT |
| JJWT (jjwt) | 0.12.6 | Emisión y verificación de tokens JWT |
| PostgreSQL | 16 | Base de datos relacional |
| Lombok | — | Reducción de boilerplate |
| MapStruct | 1.6.3 | Mapeo entre capas |
| SpringDoc OpenAPI | 2.8.13 | Documentación Swagger |
| JUnit 5 / Mockito | — | Pruebas unitarias e integración |
| Docker / Docker Compose | — | Contenedorización y entorno local |
| Maven Wrapper | — | Gestión del build |

---

## 🏗 Arquitectura

El proyecto implementa **Arquitectura Hexagonal (Ports & Adapters)** organizada por **feature** (`tasks` y `auth`).

```
┌─────────────────────────────────────────────────────────────────┐
│                        HTTP / REST Client                        │
└──────────────────────────────┬──────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────┐
│              Infrastructure IN  (Adaptadores primarios)          │
│         TaskController  ──►  DTOs  ──►  TaskWebMapper            │
└──────────────────────────────┬──────────────────────────────────┘
                               │  Commands / Domain model
┌──────────────────────────────▼──────────────────────────────────┐
│                       Domain (núcleo)                            │
│                                                                  │
│   Use Cases (interfaces + impls):                                │
│     CreateTask · ListTasks · UpdateTask                          │
│     UpdateTaskStatus · DeleteTask                                │
│                                                                  │
│   Model:  JTask (record)  ·  TaskStatus (enum)                   │
│   Ports OUT:  SaveTaskPort · FindTaskByIdPort                    │
│               FindAllTasksPort · ExistsTaskByKeyPort             │
│               GenerateTaskKeyPort                                │
└──────────────────────────────┬──────────────────────────────────┘
                               │  Ports OUT
┌──────────────────────────────▼──────────────────────────────────┐
│             Infrastructure OUT (Adaptadores secundarios)         │
│   TaskPersistenceAdapter  ──►  TaskJpaRepository (JPA)           │
│   TaskKeyGeneratorAdapter                                        │
│   TaskEntity  ·  TaskPersistenceMapper                           │
└─────────────────────────────────────────────────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │     PostgreSQL DB    │
                    └─────────────────────┘
```

### Principios aplicados
- **Domain-centric**: la lógica de negocio no depende de frameworks ni infraestructura.
- **Ports & Adapters**: el dominio define interfaces (ports); la infraestructura las implementa (adapters).
- **Feature-based packaging**: todo lo relativo a `tasks` vive bajo `tasks/` y lo de autenticación bajo `auth/`.
- **Use Case per operation**: cada caso de uso tiene su propia interfaz e implementación.

> El feature `auth` sigue la misma estructura hexagonal: el dominio define los puertos
> (`SaveUserPort`, `PasswordHasherPort`, `GenerateTokenPort`, …) y la infraestructura los
> implementa con JPA, BCrypt y JWT respectivamente. La seguridad transversal (filtro JWT,
> configuración de Spring Security) vive en `shared/security`.

---

## 📁 Estructura del proyecto

```
src/main/java/com/jdpadillac/jtaskboard/
├── JtaskboardApplication.java
├── shared/
│   ├── config/
│   │   └── OpenApiConfiguration.java       # Configuración Swagger/OpenAPI
│   ├── security/
│   │   ├── SecurityConfiguration.java      # Filter chain, CORS, endpoints públicos
│   │   ├── JwtAuthenticationFilter.java    # Lee el header Bearer y autentica
│   │   └── RestAuthenticationEntryPoint.java # Respuesta 401 en JSON
│   └── web/
│       └── GlobalExceptionHandler.java     # Manejo centralizado de errores
├── auth/
│   ├── domain/
│   │   ├── exception/
│   │   │   ├── EmailAlreadyRegisteredException.java
│   │   │   └── InvalidCredentialsException.java
│   │   ├── model/
│   │   │   ├── User.java                   # Entidad de dominio (record)
│   │   │   └── AuthSession.java            # Token JWT + usuario autenticado
│   │   ├── port/out/                       # Puertos de salida
│   │   │   ├── SaveUserPort.java
│   │   │   ├── FindUserByEmailPort.java
│   │   │   ├── ExistsUserByEmailPort.java
│   │   │   ├── PasswordHasherPort.java
│   │   │   └── GenerateTokenPort.java
│   │   └── usecase/
│   │       ├── RegisterUserUseCase.java
│   │       ├── LoginUseCase.java
│   │       ├── command/                    # Comandos de entrada
│   │       └── impl/                       # Implementaciones
│   └── infrastructure/
│       ├── in/web/
│       │   ├── AuthController.java         # POST /register · POST /login
│       │   ├── dto/                        # Request / Response DTOs
│       │   └── mapper/
│       │       └── AuthWebMapper.java      # MapStruct: DTO ↔ Domain
│       └── out/
│           ├── adapters/
│           │   ├── UserPersistenceAdapter.java
│           │   ├── BCryptPasswordHasherAdapter.java
│           │   └── JwtTokenAdapter.java    # Firma/verificación JWT (HS256)
│           └── persistence/
│               ├── UserEntity.java
│               ├── UserJpaRepository.java
│               └── mapper/
│                   └── UserPersistenceMapper.java
└── tasks/
    ├── domain/
    │   ├── exception/
    │   │   └── TaskNotFoundException.java
    │   ├── model/
    │   │   ├── JTask.java                  # Entidad de dominio (record)
    │   │   └── TaskStatus.java             # Enum: TODO | IN_PROGRESS | DONE
    │   ├── port/out/                       # Interfaces (puertos de salida)
    │   │   ├── ExistsTaskByKeyPort.java
    │   │   ├── FindAllTasksPort.java
    │   │   ├── FindTaskByIdPort.java
    │   │   ├── GenerateTaskKeyPort.java
    │   │   └── SaveTaskPort.java
    │   └── usecase/
    │       ├── CreateTaskUseCase.java
    │       ├── DeleteTaskUseCase.java
    │       ├── ListTasksUseCase.java
    │       ├── UpdateTaskStatusUseCase.java
    │       ├── UpdateTaskUseCase.java
    │       ├── command/                    # Objetos de comando (input)
    │       └── impl/                      # Implementaciones de los casos de uso
    └── infrastructure/
        ├── in/web/
        │   ├── TaskController.java         # REST Controller
        │   ├── dto/                        # Request / Response DTOs
        │   └── mapper/
        │       └── TaskWebMapper.java      # MapStruct: DTO ↔ Domain
        └── out/
            ├── adapters/
            │   ├── TaskPersistenceAdapter.java
            │   └── TaskKeyGeneratorAdapter.java
            └── persistence/
                ├── TaskEntity.java
                ├── TaskJpaRepository.java
                └── mapper/
                    └── TaskPersistenceMapper.java
```

---

## ✅ Requisitos previos

Antes de levantar el proyecto asegúrate de tener instalado:

| Herramienta | Versión mínima | Verificar con |
|---|---|---|
| Java (JDK) | 21 | `java -version` |
| Maven | 3.9+ (o usa `./mvnw`) | `./mvnw -v` |
| Docker | 20+ | `docker -v` |
| Docker Compose | 2+ | `docker compose version` |

> **Tip macOS**: puedes instalar Java 21 con `brew install openjdk@21` o descargarlo desde [adoptium.net](https://adoptium.net/).

---

## ⚙️ Configuración de variables de entorno

La aplicación usa variables de entorno con valores por defecto seguros para local. No necesitas configurar nada extra si usas el `docker-compose.yml` incluido.

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `PORT` | `8080` | Puerto del servidor |
| `DB_HOST` | `localhost` | Host de PostgreSQL |
| `DB_PORT` | `5432` | Puerto de PostgreSQL |
| `DB_NAME` | `jtaskboard` | Nombre de la base de datos |
| `DB_USER` | `jtaskboard` | Usuario de la base de datos |
| `DB_PASSWORD` | `jtaskboard` | Contraseña de la base de datos |
| `JWT_SECRET` | `local-dev-secret-change-me-please-32chars-min` | Secreto HMAC para firmar los JWT. **Mínimo 32 caracteres. Cámbialo en producción.** |
| `JWT_EXPIRATION_MINUTES` | `60` | Minutos de validez del token de sesión |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Orígenes del frontend permitidos (separados por coma) |

> ⚠️ **Seguridad**: en cualquier entorno que no sea local define `JWT_SECRET` con un valor
> aleatorio y suficientemente largo. Si se filtra, cualquiera podría emitir tokens válidos.

Para sobreescribir alguno, puedes exportarlos antes de ejecutar la app:

```bash
export DB_HOST=mi-servidor DB_PASSWORD=mi-password
./mvnw spring-boot:run
```

---

## 🚀 Levantar en local

### Paso 1 — Levantar PostgreSQL con Docker

```bash
docker compose up -d
```

Esto levanta un contenedor de PostgreSQL 16 con:
- **Base de datos**: `jtaskboard`
- **Usuario**: `jtaskboard`
- **Contraseña**: `jtaskboard`
- **Puerto**: `5432`

Verifica que esté corriendo:

```bash
docker compose ps
```

### Paso 2 — Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La aplicación arrancará en **http://localhost:8080**.

> **Primera ejecución**: Spring JPA crea automáticamente las tablas (`ddl-auto=update`), no necesitas scripts SQL manuales.

### Paso 3 — Verificar que funciona

Accede a la documentación interactiva:

```
http://localhost:8080/swagger-ui.html
```

O prueba el flujo completo: regístrate para obtener un token y úsalo para listar tareas:

```bash
# 1. Registro → devuelve un accessToken
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"firstName":"Ada","lastName":"Lovelace","email":"ada@example.com","password":"secret123"}' \
  | jq -r .accessToken)

# 2. Llamada autenticada al recurso protegido
curl -s http://localhost:8080/api/v1/tasks -H "Authorization: Bearer $TOKEN" | jq
```

---

## 🧪 Ejecutar pruebas

### Solo pruebas (sin base de datos necesaria)

```bash
./mvnw test
```

### Pruebas + reporte de cobertura (JaCoCo)

```bash
./mvnw verify
```

El reporte HTML de cobertura se genera en:

```
target/site/jacoco/index.html
```

### Cobertura de tests incluida

| Clase testeada | Tipo |
|---|---|
| `CreateTaskUseCaseImpl` | Unitario |
| `UpdateTaskUseCaseImpl` | Unitario |
| `UpdateTaskStatusUseCaseImpl` | Unitario |
| `DeleteTaskUseCaseImpl` | Unitario |
| `ListTasksUseCaseImpl` | Unitario |
| `RegisterUserUseCaseImpl` | Unitario |
| `LoginUseCaseImpl` | Unitario |
| `TaskController` | Web (MockMvc) |
| `GlobalExceptionHandler` | Web (MockMvc) |

---

## 🔐 Autenticación

La API usa **JWT (JSON Web Token)** como mecanismo de sesión sin estado (_stateless_).

### Flujo

```
┌──────────┐   POST /auth/register ó /auth/login   ┌──────────────┐
│ Cliente  │ ───────────────────────────────────►  │     API      │
│ (React)  │ ◄───────────────────────────────────  │              │
└────┬─────┘        { accessToken, user }          └──────────────┘
     │
     │  Guarda el accessToken
     │
     │  GET /tasks  +  Header: Authorization: Bearer <accessToken>
     └───────────────────────────────────────────►  recurso protegido
```

1. El cliente llama a **`/auth/register`** (usuario nuevo) o **`/auth/login`** (usuario existente).
2. La API responde con un **`accessToken`** (JWT firmado con HS256) y los datos del usuario.
3. En cada petición a un endpoint protegido, el cliente envía el token en la cabecera:
   ```
   Authorization: Bearer <accessToken>
   ```
4. El token expira a los `JWT_EXPIRATION_MINUTES` minutos (60 por defecto); al expirar hay que volver a hacer login.

> **Flujo "login o registro" del frontend**: el cliente intenta `/auth/login`; si recibe
> `401`, muestra el formulario de registro y llama a `/auth/register`.

### Endpoints públicos vs. protegidos

| Ruta | Acceso |
|---|---|
| `POST /api/v1/auth/register` | 🟢 Público |
| `POST /api/v1/auth/login` | 🟢 Público |
| `/swagger-ui.html`, `/api-docs` | 🟢 Público |
| `/api/v1/tasks/**` | 🔒 Requiere `Bearer` token |

### Seguridad de las contraseñas

- Las contraseñas se almacenan **hasheadas con BCrypt** — nunca en texto plano.
- El correo se normaliza (minúsculas, sin espacios) y es **único** por usuario.
- Las respuestas de error de login no revelan si el fallo fue por correo o por contraseña.

### `POST /auth/register` — Registrar usuario

```bash
curl -i -X POST http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "firstName": "Ada",
    "lastName": "Lovelace",
    "email": "ada@example.com",
    "password": "secret123"
  }'
```

**Respuesta `201 Created`:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "firstName": "Ada",
    "lastName": "Lovelace",
    "email": "ada@example.com",
    "createdAt": "2024-01-01T00:00:00Z"
  }
}
```

> **Validaciones**: `firstName` y `lastName` no vacíos (máx. 100); `email` con formato válido;
> `password` entre 8 y 72 caracteres. Si el correo ya existe → `409 Conflict`.

---

### `POST /auth/login` — Iniciar sesión

```bash
curl -i -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"ada@example.com","password":"secret123"}'
```

**Respuesta `200 OK`:** mismo cuerpo que `/register` (`accessToken`, `tokenType`, `user`).

> Credenciales incorrectas o usuario inexistente → `401 Unauthorized`.

### Usar el token con Swagger UI

En **http://localhost:8080/swagger-ui.html** pulsa el botón **Authorize** 🔓 y pega el
`accessToken` devuelto por `/register` o `/login`. A partir de ahí, Swagger añadirá la
cabecera `Bearer` a todas las llamadas.

---

## 📡 API Reference

Base URL: `http://localhost:8080/api/v1`

> La documentación interactiva completa está disponible en **http://localhost:8080/swagger-ui.html**
>
> 🔒 **Todos los endpoints de `/tasks` requieren autenticación.** Añade la cabecera
> `Authorization: Bearer <accessToken>` (ver [Autenticación](#autenticación)). Sin token
> válido la API responde `401 Unauthorized`.

### `POST /tasks` — Crear tarea

```bash
curl -i -X POST http://localhost:8080/api/v1/tasks \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Configurar CI","description":"Pipeline con GitHub Actions"}'
```

**Respuesta `201 Created`:**
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "taskKey": "TASK-1",
  "title": "Configurar CI",
  "description": "Pipeline con GitHub Actions",
  "status": "TODO",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

---

### `GET /tasks` — Listar tareas

```bash
curl -s http://localhost:8080/api/v1/tasks -H "Authorization: Bearer $TOKEN" | jq
```

**Respuesta `200 OK`:** array de tareas.

---

### `PUT /tasks/{id}` — Actualizar tarea

```bash
curl -i -X PUT http://localhost:8080/api/v1/tasks/{id} \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Nuevo título","description":"Nueva descripción"}'
```

**Respuesta `200 OK`:** tarea actualizada.

---

### `PATCH /tasks/{id}/status` — Actualizar estado

```bash
curl -i -X PATCH http://localhost:8080/api/v1/tasks/{id}/status \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"status":"IN_PROGRESS"}'
```

> **Estados válidos**: `TODO` | `IN_PROGRESS` | `DONE`

**Respuesta `200 OK`:** tarea con el nuevo estado.

---

### `DELETE /tasks/{id}` — Eliminar tarea

```bash
curl -i -X DELETE http://localhost:8080/api/v1/tasks/{id} \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta `204 No Content`**

---

### Códigos de error

| Código | Descripción |
|---|---|
| `400 Bad Request` | Datos de entrada inválidos (validación fallida) |
| `401 Unauthorized` | Token ausente, inválido o expirado · credenciales de login incorrectas |
| `404 Not Found` | Tarea no encontrada |
| `409 Conflict` | El correo ya está registrado (`/auth/register`) |
| `500 Internal Server Error` | Error interno del servidor |

---

## 🗃 Modelo de datos

### Tarea (`JTask`)

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `UUID` | Identificador único |
| `taskKey` | `String` | Clave legible, ej. `TASK-1` |
| `title` | `String` | Título de la tarea (requerido) |
| `description` | `String` | Descripción detallada |
| `status` | `TaskStatus` | Estado actual |
| `createdAt` | `Instant` | Fecha de creación (UTC) |
| `deletedAt` | `Instant` | Fecha de borrado lógico (nullable) |

### Estados (`TaskStatus`)

```
TODO  ──►  IN_PROGRESS  ──►  DONE
```

### Usuario (`User`)

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `UUID` | Identificador único |
| `firstName` | `String` | Nombre (requerido) |
| `lastName` | `String` | Apellidos (requerido) |
| `email` | `String` | Correo único, normalizado a minúsculas |
| `passwordHash` | `String` | Contraseña hasheada con BCrypt (nunca se expone en la API) |
| `createdAt` | `Instant` | Fecha de registro (UTC) |

---

## 🐳 Despliegue con Docker

### Build de la imagen

```bash
docker build -t jtaskboard:latest .
```

### Ejecutar el contenedor apuntando a Postgres local

```bash
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=jtaskboard \
  -e DB_USER=jtaskboard \
  -e DB_PASSWORD=jtaskboard \
  -e JWT_SECRET=cambia-este-secreto-por-uno-aleatorio-de-32-chars \
  jtaskboard:latest
```

> **Nota macOS/Windows**: usa `host.docker.internal` como `DB_HOST` para acceder a servicios en tu máquina host. En Linux utiliza la IP del host o una red Docker compartida.

### Stack completo con Docker Compose

Para levantar la app + base de datos juntos, agrega el servicio al `docker-compose.yml`:

```yaml
services:
  postgres:
    # ...configuración existente...

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: jtaskboard
      DB_USER: jtaskboard
      DB_PASSWORD: jtaskboard
      JWT_SECRET: cambia-este-secreto-por-uno-aleatorio-de-32-chars
      CORS_ALLOWED_ORIGINS: http://localhost:5173
    depends_on:
      postgres:
        condition: service_healthy
```

Luego ejecuta:

```bash
docker compose up --build
```

---

## ☁️ Despliegue en Render

El proyecto incluye `render.yaml` listo para desplegar en [Render.com](https://render.com).

### Pasos

1. Haz fork del repositorio en tu cuenta de GitHub.
2. En Render, crea un nuevo servicio desde **"Blueprint"** y conecta tu repositorio.
3. Render detectará el `render.yaml` y creará automáticamente:
   - Una **base de datos PostgreSQL** (`jtaskboard-db`)
   - Un **web service** con Docker que se conecta a la base de datos
4. Las variables de entorno de la BD se inyectan automáticamente.
5. Define manualmente `JWT_SECRET` (y opcionalmente `CORS_ALLOWED_ORIGINS`) como variable
   de entorno del web service — no debe quedar con el valor por defecto de desarrollo.
6. Espera el deploy — la URL pública quedará disponible en el dashboard.

---

## 🔗 Links útiles (local)

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |

---

## 📝 Notas de desarrollo

- El proyecto usa **Java records** para el modelo de dominio (`JTask`, `User`), garantizando inmutabilidad.
- La autenticación es **stateless**: no hay sesiones en servidor, el JWT es la única fuente de verdad.
- El filtro `JwtAuthenticationFilter` valida el token en cada petición y puebla el `SecurityContext`.
- La clave de tarea (`taskKey`) se genera automáticamente con el formato `TASK-{n}`.
- El borrado lógico está previsto en el modelo (`deletedAt`), aunque el endpoint actual realiza hard delete.
- El esquema de base de datos se gestiona con `ddl-auto=update` — apto para desarrollo; en producción se recomienda migrar a **Flyway** o **Liquibase**.
