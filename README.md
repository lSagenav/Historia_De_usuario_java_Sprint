# Eventify - HU M6.1S1

Proyecto Spring Boot para la historia de usuario **Cimiento Arquitectonico de Eventify - Catalogo Base y Estereotipos**.

## Incluye

- Spring MVC con `spring-boot-starter-web`.
- Lombok para reducir codigo repetitivo.
- Arquitectura por capas:
  - `@RestController`
  - `@Service`
  - `@Repository`
  - `@Configuration`
  - `@Bean`
- Repositories en memoria con `Map`.
- Inyeccion por constructor usando `final` + `@RequiredArgsConstructor`.
- Swagger/OpenAPI con Springdoc.
- Manejo controlado de errores con `@RestControllerAdvice`.
- Pruebas unitarias con JUnit 5 y Mockito sin levantar todo el contexto de Spring.

## Requisitos

- Java 21
- Maven 3.9+

## Ejecutar

```bash
mvn spring-boot:run
```

Swagger:

```txt
http://localhost:8080/swagger-ui.html
```

## Ejecutar pruebas

```bash
mvn test
```

## Probar escenario de catalogo vacio

El seeder esta activo por defecto en `src/main/resources/application.properties`:

```properties
eventify.seed.enabled=true
```

Para probar catalogo vacio, ejecuta:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--eventify.seed.enabled=false
```

O cambia temporalmente la propiedad a:

```properties
eventify.seed.enabled=false
```

## Endpoints

### Eventos

```http
GET /api/events
POST /api/events
```

Ejemplo POST:

```json
{
  "nombre": "Conferencia Tech",
  "fecha": "2026-06-20",
  "descripcion": "Evento de tecnologia"
}
```

### Lugares / Venues

```http
GET /api/venues
POST /api/venues
```

Ejemplo POST:

```json
{
  "nombre": "Centro de Convenciones",
  "direccion": "Av. Principal 123",
  "capacidad": 800
}
```

## Criterios de aceptacion cubiertos

1. Registro exitoso: `POST /api/events` retorna `201 Created` y almacena en repository.
2. Registro invalido: nombre vacio lanza `BadRequestException` y no guarda en repository.
3. Catalogo vacio: con seeder desactivado, `GET /api/events` retorna `[]` y `200 OK`.
4. Documentacion: Swagger disponible en `/swagger-ui.html` con endpoints GET y POST.
