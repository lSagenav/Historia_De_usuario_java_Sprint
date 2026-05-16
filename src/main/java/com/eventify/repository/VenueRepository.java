package com.eventify.repository;

import com.eventify.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByNombreContainingIgnoreCase(String nombre);
}
