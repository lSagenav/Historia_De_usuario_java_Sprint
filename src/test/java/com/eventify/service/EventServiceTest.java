package com.eventify.service;

import com.eventify.exception.BadRequestException;
import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void shouldCreateEventWhenDataIsValid() {
        Event input = new Event(null, "Festival", LocalDate.of(2026, 6, 20), "Evento cultural");
        Event saved = new Event(1L, "Festival", LocalDate.of(2026, 6, 20), "Evento cultural");
        when(eventRepository.save(input)).thenReturn(saved);

        Event result = eventService.create(input);

        assertEquals(1L, result.getId());
        assertEquals("Festival", result.getNombre());
        verify(eventRepository).save(input);
    }

    @Test
    void shouldThrowExceptionAndNotSaveWhenEventNameIsEmpty() {
        Event input = new Event(null, " ", LocalDate.of(2026, 6, 20), "Descripcion");

        assertThrows(BadRequestException.class, () -> eventService.create(input));

        verify(eventRepository, never()).save(any());
    }

    @Test
    void shouldReturnEmptyListWhenRepositoryHasNoEvents() {
        when(eventRepository.findAll()).thenReturn(List.of());

        List<Event> result = eventService.findAll();

        assertEquals(0, result.size());
        verify(eventRepository).findAll();
    }
}
