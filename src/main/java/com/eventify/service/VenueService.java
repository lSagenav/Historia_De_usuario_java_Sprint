package com.eventify.service;

import com.eventify.exception.BadRequestException;
import com.eventify.exception.NotFoundException;
import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        return venueRepository.findAllByOrderByNombreAsc();
    }

    public Page<Venue> findPage(int page, int size, String sort) {
        Sort safeSort = parseSort(sort);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), safeSort);
        return venueRepository.findAll(pageable);
    }

    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe un venue con ID " + id));
    }

    public Venue update(Long id, Venue incoming) {
        validate(incoming);
        Venue current = findById(id);
        current.setNombre(incoming.getNombre());
        current.setDireccion(incoming.getDireccion());
        current.setCiudad(incoming.getCiudad());
        current.setCapacidad(incoming.getCapacidad());
        return venueRepository.save(current);
    }

    public void delete(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new NotFoundException("No existe un venue con ID " + id);
        }
        venueRepository.deleteById(id);
    }

    private void validate(Venue venue) {
        if (venue == null) {
            throw new BadRequestException("El lugar no puede ser nulo");
        }
        if (venue.getNombre() == null || venue.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del lugar no puede estar vacio");
        }
        if (venue.getDireccion() == null || venue.getDireccion().isBlank()) {
            throw new BadRequestException("La direccion del lugar no puede estar vacia");
        }
        if (venue.getCiudad() == null || venue.getCiudad().isBlank()) {
            throw new BadRequestException("La ciudad del lugar no puede estar vacia");
        }
        if (venue.getCapacidad() == null || venue.getCapacidad() <= 0) {
            throw new BadRequestException("La capacidad debe ser mayor a cero");
        }
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "nombre");
        }
        String[] parts = sort.split(",");
        String property = parts[0].isBlank() ? "nombre" : parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, property);
    }
}
