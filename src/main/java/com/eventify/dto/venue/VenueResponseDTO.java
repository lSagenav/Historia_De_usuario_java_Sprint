package com.eventify.dto.venue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de salida para venues. No incluye eventos asociados para evitar ciclos y datos internos.")
public record VenueResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "Movistar Arena") String nombre,
        @Schema(example = "Diagonal 61C # 26-36") String direccion,
        @Schema(example = "Bogota") String ciudad,
        @Schema(example = "14000") Integer capacidad
) {
}
