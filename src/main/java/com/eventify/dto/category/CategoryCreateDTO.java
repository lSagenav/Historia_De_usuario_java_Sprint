package com.eventify.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "DTO de entrada para crear categorias.")
public class CategoryCreateDTO {
    @NotBlank(message = "no debe estar vacio")
    @Size(min = 3, max = 120, message = "debe tener entre 3 y 120 caracteres")
    @Schema(description = "Nombre unico de la categoria", example = "Concerts", minLength = 3, maxLength = 120)
    private String name;

    @Size(max = 500, message = "no debe superar 500 caracteres")
    @Schema(description = "Descripcion de la categoria", example = "Eventos musicales y conciertos en vivo", maxLength = 500)
    private String description;
}
