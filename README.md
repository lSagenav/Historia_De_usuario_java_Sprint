# Eventify - HU M6.1S1 a M6.1S5

Proyecto Spring Boot alineado con las historias de usuario acumuladas hasta Semana 5. Mantiene la base MVC/API/Swagger/testing de HU1, evoluciona a JPA + CRUD + paginacion de HU2, agrega panel administrativo Thymeleaf de HU3, integra relaciones/Flyway/soft delete/Slice de HU4 y suma desacoplamiento completo con DTOs, MapStruct, Jakarta Validation, validadores personalizados y manejo global de errores con Problem Details en HU5.

## Requisitos

- Java 21
- Maven 3.9+
- MySQL local si vas a ejecutar con la configuracion actual de `application.properties`

## Ejecutar

```bash
mvn spring-boot:run
```

La aplicacion queda disponible en:

- Panel admin: `http://localhost:8080/admin`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Base de datos local MySQL configurada:

```txt
Database: eventify_db
Host: localhost
Port: 3306
User: configurar en src/main/resources/application.properties
Password: configurar en src/main/resources/application.properties
```

## Ejecutar pruebas

```bash
mvn test
```

## Cobertura funcional por HU

### HU1 - Cimiento arquitectonico

- Spring Boot MVC con `spring-boot-starter-web` y Lombok.
- Capas separadas: `controller`, `service`, `repository`, `model`.
- Inyeccion por constructor con `@RequiredArgsConstructor`.
- `@Configuration` con `@Bean CommandLineRunner` opcional para seed inicial.
- API REST con estados `201 Created` y `200 OK`.
- Swagger/OpenAPI en `/swagger-ui.html`.
- Tests unitarios con JUnit 5 y Mockito para servicios.

### HU2 - Persistencia, CRUD y paginacion

- Entidades JPA con `@Entity`, `@Table`, `@Id`, `@GeneratedValue` y restricciones `@Column`.
- Repositorios `JpaRepository`.
- CRUD de eventos y venues.
- Respuestas `404 Not Found` para IDs inexistentes.
- Endpoint con `Page` y metadatos: `GET /api/events/page?page=0&size=5&sort=nombre,asc`.
- Endpoint con `Page` para venues: `GET /api/venues/page?page=0&size=10&sort=nombre,asc`.
- Tests `@DataJpaTest` para persistencia, consultas y soft delete.

### HU3 - Panel administrativo visual

- Vista Thymeleaf en `src/main/resources/templates/admin/dashboard.html`.
- Uso de `th:each`, `th:text`, `th:if`, `th:unless`, `th:action`, `th:object`, `th:errors` y `th:errorclass`.
- Formularios web para crear eventos y venues.
- Controlador MVC `AdminViewController` bajo rutas `/admin`.
- Patron Post-Redirect-Get tras guardar correctamente.
- Separacion entre `/api/` y `/admin/`.
- Tests MockMvc para rutas de interfaz.

### HU4 - Relaciones, consultas y migraciones

- `Venue` conserva `direccion` de HU1 y agrega `ciudad` obligatoria de HU4.
- `Event` tiene soft delete con `active` y `@SQLRestriction("active = true")`.
- Nueva entidad `Category`.
- Relacion obligatoria `ManyToOne Event -> Venue`.
- Relacion `ManyToMany Event <-> Category` con tabla `events_categories`, `event_id` y `category_id`.
- `EventSummaryDTO` implementado como `record` para listados livianos.
- Busqueda paginada con `Slice<T>` sin conteo total en `/api/events/summaries`.
- Filtros parciales e insensibles por ciudad/categoria, capacidad minima y rango de fechas.
- Ordenamiento cronologico descendente.
- Carga optimizada con `@EntityGraph` y JPQL con `JOIN`.
- Logging SQL formateado y parametros visibles.
- Flyway activo con `ddl-auto=validate`.
- Migraciones:
  - `V1__estructura_inicial.sql`
  - `V2__evolucion_relacional.sql`
  - `V3__seed_masivo.sql` con 200 eventos, 10 venues y 7 categorias.
- Panel admin con selector de venue, checkboxes de categorias y paginacion Anterior/Siguiente conservando filtros.

### HU5 - DTOs, MapStruct, validacion y errores globales

- DTOs de entrada y salida para `Event`, `Venue` y `Category`.
- `EventCreateDTO` y `EventUpdateDTO` reciben `venueId` y `categoryIds` en lugar de entidades JPA completas.
- `EventResponseDTO` devuelve `venueName` y `categoryNames` como datos planos.
- `VenueCreateDTO`, `VenueUpdateDTO`, `VenueResponseDTO` separan la API de la entidad `Venue`.
- `CategoryCreateDTO` y `CategoryResponseDTO` evitan exponer relaciones internas.
- MapStruct integrado con `@Mapper(componentModel = "spring")`.
- `EventReferenceMapper` resuelve relaciones por ID usando repositorios.
- Validacion Jakarta en DTOs: `@NotBlank`, `@NotNull`, `@Size`, `@Future`, `@Min`, `@Max`.
- Validador personalizado `@NoPastEvents` con `NoPastEventsValidator`.
- Validation Groups para actualizacion: el ID es obligatorio en `EventUpdateDTO` y `VenueUpdateDTO`.
- Excepciones de dominio: `ResourceNotFoundException`, `DuplicateResourceException`, `BusinessRuleViolationException`.
- `@RestControllerAdvice` centralizado con `ProblemDetail` RFC 7807.
- Errores de validacion retornan `400 Bad Request` con mapa `errors` campo -> mensaje.
- Recursos no encontrados retornan `404` con `type`, `title`, `status`, `detail` e `instance`.
- Duplicados retornan `409 Conflict`.
- Thymeleaf muestra errores inline con `th:errors` y resalta campos con `th:errorclass="campo-invalido"`.
- Mensajes flash de exito en operaciones del panel administrativo.
- Swagger documenta DTOs, constraints y respuestas de error.

## Endpoints principales

### Eventos

```http
GET /api/events
GET /api/events/{id}
GET /api/events/page?page=0&size=5&sort=nombre,asc
GET /api/events/summaries?city=bog&category=rock&page=0&size=20
POST /api/events
PUT /api/events/{id}
DELETE /api/events/{id}
```

Ejemplo POST `/api/events` con DTO de entrada:

```json
{
  "nombre": "Concierto de ROCK",
  "fecha": "2026-12-20",
  "descripcion": "Evento musical principal",
  "venueId": 1,
  "categoryIds": [1, 3, 5]
}
```

Ejemplo respuesta `EventResponseDTO`:

```json
{
  "id": 1,
  "nombre": "Concierto de ROCK",
  "fecha": "2026-12-20",
  "descripcion": "Evento musical principal",
  "venueName": "Movistar Arena",
  "categoryNames": ["Concerts", "Conferences", "Festivals"]
}
```

Ejemplo PUT `/api/events/1`:

```json
{
  "id": 1,
  "nombre": "Conferencia Tech Actualizada",
  "fecha": "2026-12-22",
  "descripcion": "Evento actualizado",
  "venueId": 2,
  "categoryIds": [2, 3]
}
```

### Venues

```http
GET /api/venues
GET /api/venues/{id}
GET /api/venues/page?page=0&size=10&sort=nombre,asc
POST /api/venues
PUT /api/venues/{id}
DELETE /api/venues/{id}
```

Ejemplo POST `/api/venues`:

```json
{
  "nombre": "Centro Cultural",
  "direccion": "Calle 10 # 20-30",
  "ciudad": "Bogota",
  "capacidad": 600
}
```

### Categorias

```http
GET /api/categories
POST /api/categories
```

Ejemplo POST `/api/categories`:

```json
{
  "name": "Technology",
  "description": "Eventos de desarrollo, software e innovacion"
}
```

## Ejemplo de error RFC 7807

```json
{
  "type": "https://eventify.local/problems/400",
  "title": "Datos de entrada invalidos",
  "status": 400,
  "detail": "La peticion contiene campos con errores de validacion",
  "instance": "/api/events",
  "timestamp": "2026-06-05T11:30:00",
  "errors": {
    "nombre": "no debe estar vacio",
    "fecha": "debe ser una fecha futura"
  }
}
```

## Notas

El seed anterior por `CommandLineRunner` queda desactivado por defecto con `eventify.seed.enabled=false`, porque el seed oficial de Semana 4 esta en Flyway `V3` para evitar duplicados.

En este entorno no se incluye Maven Wrapper. Si tu equipo no tiene Maven instalado, genera o agrega `mvnw` antes de ejecutar comandos Maven.
