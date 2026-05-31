package com.eventify.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Record liviano para listados masivos de eventos. Aplana evento, venue y ciudad sin cargar entidades completas.")
public record EventSummaryDTO(
        @Schema(example = "1") Long id,
        @Schema(example = "Concierto de ROCK") String eventName,
        @Schema(example = "2026-07-15") LocalDate date,
        @Schema(example = "Movistar Arena") String venueName,
        @Schema(example = "Bogota") String city
) {
}
