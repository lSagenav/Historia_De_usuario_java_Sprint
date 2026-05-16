package com.eventify.service;

import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository repository;

    public Venue create(Venue venue) {
        if (venue.getNombre() == null || venue.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del venue es obligatorio");
        }
        return repository.save(venue);
    }

    public Page<Venue> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Venue findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue no encontrado con id: " + id));
    }

    public Venue update(Long id, Venue venue) {
        Venue existing = findById(id);
        existing.setNombre(venue.getNombre());
        existing.setDireccion(venue.getDireccion());
        existing.setCapacidad(venue.getCapacidad());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
