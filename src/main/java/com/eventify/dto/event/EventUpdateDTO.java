package com.eventify.dto.event;

import com.eventify.validation.NoPastEvents;
import com.eventify.validation.groups.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Schema(description = "DTO de entrada para actualizar eventos. El ID es obligatorio solo en flujo de actualizacion.")
public class EventUpdateDTO {
    @NotNull(groups = UpdateGroup.class, message = "es obligatorio para actualizar")
    @Schema(description = "ID del evento a actualizar", example = "1")
    private Long id;

    @NotBlank(message = "no debe estar vacio")
    @Size(min = 3, max = 150, message = "debe tener entre 3 y 150 caracteres")
    @Schema(description = "Nombre publico del evento", example = "Conferencia Tech", minLength = 3, maxLength = 150)
    private String nombre;

    @NotNull(message = "es obligatoria")
    @Future(message = "debe ser una fecha futura")
    @NoPastEvents
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Fecha programada del evento. Debe ser futura.", example = "2026-12-20")
    private LocalDate fecha;

    @Size(max = 800, message = "no debe superar 800 caracteres")
    @Schema(description = "Descripcion comercial del evento", example = "Evento de tecnologia para la comunidad", maxLength = 800)
    private String descripcion;

    @NotNull(message = "debe seleccionar un venue")
    @Schema(description = "ID del venue existente que sera asignado al evento", example = "1")
    private Long venueId;

    @Schema(description = "IDs de categorias existentes asignadas al evento", example = "[1, 3, 5]")
    private Set<Long> categoryIds = new HashSet<>();
}
