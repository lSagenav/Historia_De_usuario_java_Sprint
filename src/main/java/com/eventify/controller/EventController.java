package com.eventify.controller;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import com.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "Catalogo relacional de eventos con venues, categorias, filtros y borrado logico")
public class EventController {
    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Registrar evento relacional", description = "Registra un evento con Venue obligatorio y Categories opcionales. Al guardar se persisten los vinculos relacionales.")
    @ApiResponse(responseCode = "201", description = "Evento creado")
    public ResponseEntity<Event> create(@RequestBody Event event) {
        Event createdEvent = eventService.create(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping
    @Operation(summary = "Listar eventos detallados", description = "Retorna eventos activos ordenados por fecha descendente. @SQLRestriction oculta automaticamente eventos con active=false.")
    public ResponseEntity<List<Event>> findAll() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @GetMapping("/page")
    @Operation(summary = "Listar eventos con Page", description = "Endpoint acumulado de HU2: entrega metadatos de paginacion, total de elementos y total de paginas. Ej: ?page=0&size=5&sort=nombre,asc")
    public ResponseEntity<Page<Event>> findPage(
            @Parameter(description = "Pagina basada en cero") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamano de pagina") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo y direccion. Ej: nombre,asc o fecha,desc") @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(eventService.findPage(page, size, sort));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar evento por ID", description = "Retorna un evento activo por ID. Si no existe o fue desactivado retorna 404 Not Found.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    public ResponseEntity<Event> findById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evento", description = "Actualiza un evento existente validando previamente que el recurso exista. Si no existe retorna 404 Not Found.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento actualizado"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    public ResponseEntity<Event> update(@PathVariable Long id, @RequestBody Event event) {
        return ResponseEntity.ok(eventService.update(id, event));
    }

    @GetMapping("/summaries")
    @Operation(summary = "Buscar resumenes paginados", description = "Usa EventSummaryDTO record y Slice para listados masivos sin conteo total. Permite filtros parciales e insensibles por ciudad y categoria, capacidad minima y rango de fechas.")
    public ResponseEntity<Slice<EventSummaryDTO>> searchSummaries(
            @Parameter(description = "Ciudad parcial. Ej: bog encuentra Bogota") @RequestParam(required = false) String city,
            @Parameter(description = "Categoria parcial e insensible. Ej: rock encuentra Concierto de ROCK si la categoria coincide") @RequestParam(required = false) String category,
            @Parameter(description = "Capacidad minima del venue") @RequestParam(required = false) Integer minCapacity,
            @Parameter(description = "Fecha inicial inclusive, formato yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha final inclusive, formato yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Pagina basada en cero") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamano del fragmento Slice") @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(eventService.searchSummaries(city, category, minCapacity, startDate, endDate, page, size));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrado logico de evento", description = "No elimina fisicamente el registro. Ejecuta softDelete/deactivate cambiando active=false; las consultas normales lo excluyen automaticamente. Si el ID no existe retorna 404.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento desactivado"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        eventService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
