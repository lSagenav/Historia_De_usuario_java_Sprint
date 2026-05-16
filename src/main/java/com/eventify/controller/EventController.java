package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "CRUD completo de eventos")
public class EventController {

    private final EventService service;

    @PostMapping
    @Operation(summary = "Registrar evento")
    @ApiResponse(responseCode = "201", description = "Evento creado")
    public ResponseEntity<Event> create(@RequestBody Event event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(event));
    }

    @GetMapping
    @Operation(summary = "Listar eventos paginados")
    public ResponseEntity<Page<Event>> findAll(@Parameter(hidden = true)
                                               @PageableDefault(size = 5, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar evento por ID")
    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    public ResponseEntity<Event> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evento")
    public ResponseEntity<Event> update(@PathVariable Long id, @RequestBody Event event) {
        return ResponseEntity.ok(service.update(id, event));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar evento")
    @ApiResponse(responseCode = "204", description = "Evento eliminado")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
