# Eventify - HU M6.1S1 a M6.1S4

Proyecto Spring Boot alineado con las 4 historias de usuario acumuladas hasta Semana 4. Mantiene la base MVC/API/Swagger/testing de HU1, evoluciona a JPA + CRUD + paginacion de HU2, agrega panel administrativo Thymeleaf de HU3 e integra el modelo relacional avanzado, Flyway, soft delete, DTO record, Slice y filtros de HU4.

## Requisitos

- Java 21
- Maven 3.9+

## Ejecutar

```bash
mvn spring-boot:run
```

La aplicacion queda disponible en:

- Panel admin: `http://localhost:8080/admin`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

Base de datos local MySQL:

```txt
Database: eventify_db
Host: localhost
Port: 3306
User: root
Password: 123$qwe
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
- CRUD de eventos: `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`.
- CRUD de venues: `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`.
- Respuestas `404 Not Found` para IDs inexistentes.
- Endpoint con `Page` y metadatos: `GET /api/events/page?page=0&size=5&sort=nombre,asc`.
- Endpoint con `Page` para venues: `GET /api/venues/page?page=0&size=10&sort=nombre,asc`.
- Tests `@DataJpaTest` para persistencia, consultas y soft delete.

### HU3 - Panel administrativo visual

- Vista Thymeleaf en `src/main/resources/templates/admin/dashboard.html`.
- Uso de `th:each`, `th:text`, `th:if`, `th:unless`, `th:action` y `th:object`.
- Formularios web para crear eventos y venues.
- Controlador MVC `AdminViewController` bajo rutas `/admin`.
- Patron Post-Redirect-Get tras guardar.
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

Ejemplo POST/PUT con relaciones existentes:

```json
{
  "nombre": "Concierto de ROCK",
  "fecha": "2026-07-15",
  "descripcion": "Evento musical principal",
  "venue": { "id": 1 },
  "categories": [ { "id": 1 } ]
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

Ejemplo Venue:

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

## Notas

El seed anterior por `CommandLineRunner` queda desactivado por defecto con `eventify.seed.enabled=false`, porque el seed oficial de Semana 4 esta en Flyway `V3` para evitar duplicados.
