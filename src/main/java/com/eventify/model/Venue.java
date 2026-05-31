package com.eventify.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "venues")
@Schema(description = "Lugar fisico disponible para realizar eventos")
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 150)
    @Schema(example = "Centro de Convenciones")
    private String nombre;

    @Column(nullable = false, length = 220)
    @Schema(example = "Calle 26 # 62-47")
    private String direccion;

    @Column(nullable = false, length = 180)
    @Schema(example = "Bogota")
    private String ciudad;

    @Column(nullable = false)
    @Schema(example = "800")
    private Integer capacidad;

    @JsonIgnore
    @OneToMany(mappedBy = "venue")
    @ToString.Exclude
    private Set<Event> events = new HashSet<>();

    public Venue(Long id, String nombre, String direccion, String ciudad, Integer capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.capacidad = capacidad;
    }
}
