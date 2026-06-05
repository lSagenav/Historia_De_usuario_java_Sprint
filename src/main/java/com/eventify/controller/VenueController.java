package com.eventify.controller;

import com.eventify.dto.venue.VenueCreateDTO;
import com.eventify.dto.venue.VenueResponseDTO;
import com.eventify.dto.venue.VenueUpdateDTO;
import com.eventify.service.VenueService;
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

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
@Tag(name = "Lugares", description = "Endpoints desacoplados para registrar y consultar venues mediante DTOs")
public class VenueController {
    private final VenueService venueService;

    @PostMapping
    @Operation(summary = "Registrar lugar con DTO", description = "Valida y registra un venue sin exponer entidades JPA ni eventos asociados")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venue creado", content = @Content(schema = @Schema(implementation = VenueResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Errores de validacion", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Venue duplicado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<VenueResponseDTO> create(@Valid @RequestBody VenueCreateDTO venue) {
        VenueResponseDTO createdVenue = venueService.create(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
    }

    @GetMapping
    @Operation(summary = "Listar lugares como DTOs", description = "Retorna todos los lugares ordenados por nombre")
    public ResponseEntity<List<VenueResponseDTO>> findAll() {
        return ResponseEntity.ok(venueService.findAllResponses());
    }

    @GetMapping("/page")
    @Operation(summary = "Listar lugares con Page", description = "Entrega metadatos de paginacion y ordenamiento. Ej: ?page=0&size=10&sort=nombre,asc")
    public ResponseEntity<Page<VenueResponseDTO>> findPage(
            @Parameter(description = "Pagina basada en cero") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamano de pagina") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo y direccion. Ej: nombre,asc") @RequestParam(defaultValue = "nombre,asc") String sort
    ) {
        return ResponseEntity.ok(venueService.findPageResponses(page, size, sort));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar lugar por ID", description = "Retorna 404 ProblemDetail si el venue no existe")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Venue encontrado"), @ApiResponse(responseCode = "404", description = "Venue no encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ResponseEntity<VenueResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.findByIdResponse(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar lugar con DTO", description = "Actualiza un venue existente validando con Validation Groups que el ID del cuerpo sea obligatorio")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Venue actualizado"), @ApiResponse(responseCode = "404", description = "Venue no encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ResponseEntity<VenueResponseDTO> update(@PathVariable Long id, @Validated({Default.class, UpdateGroup.class}) @RequestBody VenueUpdateDTO venue) {
        return ResponseEntity.ok(venueService.update(id, venue));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar lugar", description = "Borrado fisico requerido en HU2 para venues; retorna 204 si fue exitoso y 404 si no existe")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Venue eliminado"), @ApiResponse(responseCode = "404", description = "Venue no encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
