package com.eventify.dto.venue;

import com.eventify.validation.groups.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "DTO de entrada para actualizar venues. El ID es obligatorio solo en actualizacion.")
public class VenueUpdateDTO {
    @NotNull(groups = UpdateGroup.class, message = "es obligatorio para actualizar")
    @Schema(description = "ID del venue a actualizar", example = "1")
    private Long id;

    @NotBlank(message = "no debe estar vacio")
    @Size(min = 3, max = 150, message = "debe tener entre 3 y 150 caracteres")
    @Schema(description = "Nombre del lugar", example = "Movistar Arena", minLength = 3, maxLength = 150)
    private String nombre;

    @NotBlank(message = "no debe estar vacia")
    @Size(max = 220, message = "no debe superar 220 caracteres")
    @Schema(description = "Direccion del lugar", example = "Diagonal 61C # 26-36", maxLength = 220)
    private String direccion;

    @NotBlank(message = "no debe estar vacia")
    @Size(max = 180, message = "no debe superar 180 caracteres")
    @Schema(description = "Ciudad donde se ubica el venue", example = "Bogota", maxLength = 180)
    private String ciudad;

    @NotNull(message = "es obligatoria")
    @Min(value = 1, message = "debe ser mayor a cero")
    @Max(value = 100000, message = "no debe superar 100000")
    @Schema(description = "Capacidad maxima del venue", example = "14000", minimum = "1", maximum = "100000")
    private Integer capacidad;
}
