package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
@Tag(name = "Lugares", description = "Endpoints para registrar y consultar venues con direccion, ciudad y capacidad")
public class VenueController {
    private final VenueService venueService;

    @PostMapping
    @Operation(summary = "Registrar lugar", description = "Valida y registra un venue con direccion, ciudad y capacidad obligatorias")
    @ApiResponse(responseCode = "201", description = "Venue creado")
    public ResponseEntity<Venue> create(@RequestBody Venue venue) {
        Venue createdVenue = venueService.create(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
    }

    @GetMapping
    @Operation(summary = "Listar lugares", description = "Retorna todos los lugares ordenados por nombre")
    public ResponseEntity<List<Venue>> findAll() {
        return ResponseEntity.ok(venueService.findAll());
    }

    @GetMapping("/page")
    @Operation(summary = "Listar lugares con Page", description = "Entrega metadatos de paginacion y ordenamiento. Ej: ?page=0&size=10&sort=nombre,asc")
    public ResponseEntity<Page<Venue>> findPage(
            @Parameter(description = "Pagina basada en cero") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamano de pagina") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo y direccion. Ej: nombre,asc") @RequestParam(defaultValue = "nombre,asc") String sort
    ) {
        return ResponseEntity.ok(venueService.findPage(page, size, sort));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar lugar por ID", description = "Retorna 404 si el venue no existe")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Venue encontrado"), @ApiResponse(responseCode = "404", description = "Venue no encontrado")})
    public ResponseEntity<Venue> findById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar lugar", description = "Actualiza un venue existente validando que el ID exista")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Venue actualizado"), @ApiResponse(responseCode = "404", description = "Venue no encontrado")})
    public ResponseEntity<Venue> update(@PathVariable Long id, @RequestBody Venue venue) {
        return ResponseEntity.ok(venueService.update(id, venue));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar lugar", description = "Borrado fisico requerido en HU2 para venues; retorna 204 si fue exitoso y 404 si no existe")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Venue eliminado"), @ApiResponse(responseCode = "404", description = "Venue no encontrado")})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
