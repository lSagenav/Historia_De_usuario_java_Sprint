package com.eventify.service;

import com.eventify.exception.BadRequestException;
import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public Event create(Event event) {
        validate(event);
        return eventRepository.save(event);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    private void validate(Event event) {
        if (event == null) {
            throw new BadRequestException("El evento no puede ser nulo");
        }
        if (event.getNombre() == null || event.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del evento no puede estar vacio");
        }
    }
}
