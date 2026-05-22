# JTaskboard

API REST para gestión de tareas estilo Jira, construida con **Spring Boot 4** y **arquitectura hexagonal (Ports & Adapters)** organizada por feature.

---

## 📋 Tabla de contenidos

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Configuración de variables de entorno](#configuración-de-variables-de-entorno)
- [Levantar en local](#levantar-en-local)
- [Ejecutar pruebas](#ejecutar-pruebas)
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
| PostgreSQL | 16 | Base de datos relacional |
| Lombok | — | Reducción de boilerplate |
| MapStruct | 1.6.3 | Mapeo entre capas |
| SpringDoc OpenAPI | 2.8.13 | Documentación Swagger |
| JUnit 5 / Mockito | — | Pruebas unitarias e integración |
| Docker / Docker Compose | — | Contenedorización y entorno local |
| Maven Wrapper | — | Gestión del build |

---

## 🏗 Arquitectura

El proyecto implementa **Arquitectura Hexagonal (Ports & Adapters)** organizada por **feature** (`tasks`).

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
- **Feature-based packaging**: todo lo relativo a `tasks` vive bajo `tasks/`.
- **Use Case per operation**: cada caso de uso tiene su propia interfaz e implementación.

---

## 📁 Estructura del proyecto

```
src/main/java/com/jdpadillac/jtaskboard/
├── JtaskboardApplication.java
├── shared/
│   ├── config/
│   │   └── OpenApiConfiguration.java       # Configuración Swagger/OpenAPI
│   └── web/
│       └── GlobalExceptionHandler.java     # Manejo centralizado de errores
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

O prueba un endpoint rápido:

```bash
curl -s http://localhost:8080/api/v1/tasks | jq
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
| `TaskController` | Web (MockMvc) |
| `GlobalExceptionHandler` | Web (MockMvc) |

---

## 📡 API Reference

Base URL: `http://localhost:8080/api/v1`

> La documentación interactiva completa está disponible en **http://localhost:8080/swagger-ui.html**

### `POST /tasks` — Crear tarea

```bash
curl -i -X POST http://localhost:8080/api/v1/tasks \
  -H 'Content-Type: application/json' \
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
curl -s http://localhost:8080/api/v1/tasks | jq
```

**Respuesta `200 OK`:** array de tareas.

---

### `PUT /tasks/{id}` — Actualizar tarea

```bash
curl -i -X PUT http://localhost:8080/api/v1/tasks/{id} \
  -H 'Content-Type: application/json' \
  -d '{"title":"Nuevo título","description":"Nueva descripción"}'
```

**Respuesta `200 OK`:** tarea actualizada.

---

### `PATCH /tasks/{id}/status` — Actualizar estado

```bash
curl -i -X PATCH http://localhost:8080/api/v1/tasks/{id}/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"IN_PROGRESS"}'
```

> **Estados válidos**: `TODO` | `IN_PROGRESS` | `DONE`

**Respuesta `200 OK`:** tarea con el nuevo estado.

---

### `DELETE /tasks/{id}` — Eliminar tarea

```bash
curl -i -X DELETE http://localhost:8080/api/v1/tasks/{id}
```

**Respuesta `204 No Content`**

---

### Códigos de error

| Código | Descripción |
|---|---|
| `400 Bad Request` | Datos de entrada inválidos (validación fallida) |
| `404 Not Found` | Tarea no encontrada |
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
5. Espera el deploy — la URL pública quedará disponible en el dashboard.

---

## 🔗 Links útiles (local)

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |

---

## 📝 Notas de desarrollo

- El proyecto usa **Java records** para el modelo de dominio (`JTask`), garantizando inmutabilidad.
- La clave de tarea (`taskKey`) se genera automáticamente con el formato `TASK-{n}`.
- El borrado lógico está previsto en el modelo (`deletedAt`), aunque el endpoint actual realiza hard delete.
- El esquema de base de datos se gestiona con `ddl-auto=update` — apto para desarrollo; en producción se recomienda migrar a **Flyway** o **Liquibase**.
