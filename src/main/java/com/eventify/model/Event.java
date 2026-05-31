package com.eventify.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "events")
@SQLRestriction("active = true")
@Schema(description = "Evento registrado en el catalogo de Eventify. Usa borrado logico: los inactivos no se retornan en consultas normales.")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 150)
    @Schema(example = "Conferencia Tech")
    private String nombre;

    @Column(nullable = false)
    @Schema(example = "2026-06-20")
    private LocalDate fecha;

    @Column(length = 800)
    @Schema(example = "Evento de tecnologia para la comunidad")
    private String descripcion;

    @Column(nullable = false)
    @Schema(description = "Bandera de borrado logico. false significa inactivo y queda oculto por @SQLRestriction.", example = "true")
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "venue_id", nullable = false)
    @Schema(description = "Venue obligatorio asignado al evento")
    @ToString.Exclude
    private Venue venue;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "events_categories",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Schema(description = "Categorias tematicas asignadas al evento")
    @ToString.Exclude
    private Set<Category> categories = new HashSet<>();

    public Event(Long id, String nombre, LocalDate fecha, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.active = true;
    }

    public Event(Long id, String nombre, LocalDate fecha, String descripcion, Boolean active, Venue venue, Set<Category> categories) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.active = active == null ? true : active;
        this.venue = venue;
        this.categories = categories == null ? new HashSet<>() : categories;
    }

    public void deactivate() {
        this.active = false;
    }
}
