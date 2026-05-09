package com.eventify.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Lugar disponible para realizar eventos")
public class Venue {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @Schema(example = "Centro de Convenciones")
    private String nombre;

    @Schema(example = "Av. Principal 123")
    private String direccion;

    @Schema(example = "800")
    private Integer capacidad;
}
