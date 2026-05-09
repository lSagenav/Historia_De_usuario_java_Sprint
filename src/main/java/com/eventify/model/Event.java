package com.eventify.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Evento registrado en el catalogo interno de Eventify")
public class Event {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @Schema(example = "Conferencia Tech")
    private String nombre;

    @Schema(example = "2026-06-20")
    private LocalDate fecha;

    @Schema(example = "Evento de tecnologia para la comunidad")
    private String descripcion;
}
