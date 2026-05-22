# jtaskboard

Endpoint inicial para crear tareas estilo Jira con arquitectura modular/hexagonal por feature.

## Requisitos

- Java 21
- Docker + Docker Compose

## Levantar PostgreSQL

```bash
docker compose up -d
```

## Ejecutar pruebas

```bash
./mvnw test
```

## Ejecutar aplicación

```bash
./mvnw spring-boot:run
```

## Documentación Swagger

- UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Probar endpoint

```bash
curl -i -X POST http://localhost:8080/api/v1/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Configurar CI","description":"Pipeline con GitHub Actions"}'
```

