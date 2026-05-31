package com.eventify.repository;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Override
    @EntityGraph(attributePaths = {"venue", "categories"})
    Page<Event> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"venue", "categories"})
    java.util.Optional<Event> findById(Long id);

    @EntityGraph(attributePaths = {"venue", "categories"})
    List<Event> findAllByOrderByFechaDesc();

    List<Event> findByNombreContainingIgnoreCase(String nombre);

    @Query("""
            select distinct new com.eventify.dto.EventSummaryDTO(e.id, e.nombre, e.fecha, v.nombre, v.ciudad)
            from Event e
            join e.venue v
            left join e.categories c
            where (:city is null or lower(v.ciudad) like lower(concat('%', :city, '%')))
              and (:category is null or lower(c.name) like lower(concat('%', :category, '%')))
              and (:minCapacity is null or v.capacidad >= :minCapacity)
              and (:startDate is null or e.fecha >= :startDate)
              and (:endDate is null or e.fecha <= :endDate)
            order by e.fecha desc
            """)
    Slice<EventSummaryDTO> searchSummaries(
            @Param("city") String city,
            @Param("category") String category,
            @Param("minCapacity") Integer minCapacity,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"venue", "categories"})
    @Query("""
            select distinct e
            from Event e
            join e.venue v
            left join e.categories c
            where (:city is null or lower(v.ciudad) like lower(concat('%', :city, '%')))
              and (:category is null or lower(c.name) like lower(concat('%', :category, '%')))
              and (:minCapacity is null or v.capacidad >= :minCapacity)
              and (:startDate is null or e.fecha >= :startDate)
              and (:endDate is null or e.fecha <= :endDate)
            order by e.fecha desc
            """)
    Slice<Event> searchDetailed(
            @Param("city") String city,
            @Param("category") String category,
            @Param("minCapacity") Integer minCapacity,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Modifying
    @Query("update Event e set e.active = false where e.id = :id")
    int softDeleteById(@Param("id") Long id);
}
