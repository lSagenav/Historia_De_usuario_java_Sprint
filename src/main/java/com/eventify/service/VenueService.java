package com.eventify.service;

import com.eventify.exception.BadRequestException;
import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;

    public Venue create(Venue venue) {
        validate(venue);
        return venueRepository.save(venue);
    }

    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    private void validate(Venue venue) {
        if (venue == null) {
            throw new BadRequestException("El lugar no puede ser nulo");
        }
        if (venue.getNombre() == null || venue.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del lugar no puede estar vacio");
        }
        if (venue.getCapacidad() == null || venue.getCapacidad() <= 0) {
            throw new BadRequestException("La capacidad debe ser mayor a cero");
        }
    }
}
