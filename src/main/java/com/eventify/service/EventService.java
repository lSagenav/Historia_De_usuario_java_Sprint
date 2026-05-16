package com.eventify.service;

import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repository;

    public Event create(Event event) {
        validate(event);
        return repository.save(event);
    }

    public Page<Event> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));
    }

    public Event update(Long id, Event event) {
        validate(event);
        Event existing = findById(id);
        existing.setNombre(event.getNombre());
        existing.setFecha(event.getFecha());
        existing.setDescripcion(event.getDescripcion());
        return repository.save(existing);
    }

    public void delete(Long id) {
        Event existing = findById(id);
        repository.delete(existing);
    }

    private void validate(Event event) {
        if (event.getNombre() == null || event.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del evento es obligatorio");
        }
    }
}
