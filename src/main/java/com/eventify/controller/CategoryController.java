package com.eventify.controller;

import com.eventify.dto.category.CategoryCreateDTO;
import com.eventify.dto.category.CategoryResponseDTO;
import com.eventify.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Catalogo de categorias tematicas desacoplado mediante DTOs")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Registrar categoria con DTO", description = "Crea una categoria tematica para clasificar eventos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria creada", content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Errores de validacion", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Categoria duplicada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryCreateDTO category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(category));
    }

    @GetMapping
    @Operation(summary = "Listar categorias", description = "Retorna categorias disponibles ordenadas alfabeticamente")
    public ResponseEntity<List<CategoryResponseDTO>> findAll() {
        return ResponseEntity.ok(categoryService.findAllResponses());
    }
}
