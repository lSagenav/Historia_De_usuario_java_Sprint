package com.eventify.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "DTO de salida para eventos. Aplana venue y categorias para no exponer la estructura JPA.")
public record EventResponseDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "Concierto de ROCK") String nombre,
        @Schema(example = "2026-12-20") LocalDate fecha,
        @Schema(example = "Evento de tecnologia para la comunidad") String descripcion,
        @Schema(example = "Movistar Arena") String venueName,
        @Schema(example = "[\"Concerts\", \"Conferences\"]") List<String> categoryNames
) {
}
