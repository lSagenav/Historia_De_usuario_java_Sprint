package com.eventify.controller;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.dto.event.EventCreateDTO;
import com.eventify.dto.event.EventResponseDTO;
import com.eventify.dto.event.EventUpdateDTO;
import com.eventify.service.EventService;
import com.eventify.validation.groups.UpdateGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.groups.Default;
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
@Tag(name = "Eventos", description = "Catalogo relacional desacoplado con DTOs, validacion, filtros, records y borrado logico")
public class EventController {
    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Registrar evento con DTO", description = "Recibe EventCreateDTO, valida con Jakarta Bean Validation y retorna EventResponseDTO sin exponer entidades JPA.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento creado", content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Errores de validacion", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Venue o categoria no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Error inesperado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<EventResponseDTO> create(@Valid @RequestBody EventCreateDTO event) {
        EventResponseDTO createdEvent = eventService.create(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping
    @Operation(summary = "Listar eventos masivos con EventSummaryDTO", description = "Mantiene el record EventSummaryDTO de semana 4 con Slice para listados masivos. No expone entidades JPA y evita el conteo total de registros.")
    public ResponseEntity<Slice<EventSummaryDTO>> findAll(
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

    @GetMapping("/page")
    @Operation(summary = "Listar eventos con Page y DTO", description = "Endpoint acumulado de HU2: entrega metadatos de paginacion, total de elementos y total de paginas usando EventResponseDTO.")
    public ResponseEntity<Page<EventResponseDTO>> findPage(
            @Parameter(description = "Pagina basada en cero") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamano de pagina") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo y direccion. Ej: nombre,asc o fecha,desc") @RequestParam(defaultValue = "fecha,desc") String sort
    ) {
        return ResponseEntity.ok(eventService.findPageResponses(page, size, sort));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar detalle con EventResponseDTO", description = "Retorna venueName y categoryNames como Strings planos. Si el evento no existe o fue desactivado retorna ProblemDetail 404.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado", content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<EventResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findByIdResponse(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evento con DTO", description = "Valida EventUpdateDTO usando Validation Groups. El ID del cuerpo debe coincidir con el ID del path.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento actualizado", content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Errores de validacion", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Evento, venue o categoria no encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<EventResponseDTO> update(@PathVariable Long id, @Validated({Default.class, UpdateGroup.class}) @RequestBody EventUpdateDTO event) {
        return ResponseEntity.ok(eventService.update(id, event));
    }

    @GetMapping("/summaries")
    @Operation(summary = "Buscar resumenes paginados", description = "Mantiene el record EventSummaryDTO de semana 4 con Slice para listados masivos. Permite filtros parciales e insensibles por ciudad y categoria, capacidad minima y rango de fechas.")
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
    @Operation(summary = "Borrado logico de evento", description = "No elimina fisicamente el registro. Ejecuta softDelete/deactivate cambiando active=false; las consultas normales lo excluyen automaticamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento desactivado"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        eventService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
