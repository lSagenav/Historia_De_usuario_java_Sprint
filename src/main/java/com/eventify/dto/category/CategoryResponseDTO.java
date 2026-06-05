package com.eventify.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de salida para categorias. No expone eventos asociados.")
public record CategoryResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "Concerts") String name,
        @Schema(example = "Eventos musicales y conciertos en vivo") String description
) {
}
