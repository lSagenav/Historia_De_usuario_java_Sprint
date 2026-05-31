package com.eventify.service;

import com.eventify.exception.BadRequestException;
import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.CategoryRepository;
import com.eventify.repository.EventRepository;
import com.eventify.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    @Mock
    private VenueRepository venueRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void shouldCreateEventWhenDataIsValid() {
        Venue venue = new Venue(1L, "Teatro Central", "Calle 1 # 2-3", "Bogota", 500);
        Category category = new Category(1L, "Concerts", "Musica");
        Event input = new Event(null, "Festival", LocalDate.of(2026, 6, 20), "Evento cultural", true, venue, Set.of(category));
        Event saved = new Event(1L, "Festival", LocalDate.of(2026, 6, 20), "Evento cultural", true, venue, Set.of(category));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(eventRepository.save(input)).thenReturn(saved);

        Event result = eventService.create(input);

        assertEquals(1L, result.getId());
        assertEquals("Festival", result.getNombre());
        verify(eventRepository).save(input);
    }

    @Test
    void shouldCreateEventFromFormWhenRelationsAreValid() {
        Venue venue = new Venue(1L, "Teatro Central", "Calle 1 # 2-3", "Bogota", 500);
        Category category = new Category(1L, "Concerts", "Musica");
        Event input = new Event(null, "Festival", LocalDate.of(2026, 6, 20), "Evento cultural");
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.createFromForm(input, 1L, List.of(1L));

        assertEquals(venue, result.getVenue());
        assertEquals(1, result.getCategories().size());
        verify(eventRepository).save(input);
    }

    @Test
    void shouldThrowExceptionAndNotSaveWhenEventNameIsEmpty() {
        Event input = new Event(null, " ", LocalDate.of(2026, 6, 20), "Descripcion");
        input.setVenue(new Venue(1L, "Teatro", "Calle 4 # 5-6", "Bogota", 500));

        assertThrows(BadRequestException.class, () -> eventService.create(input));

        verify(eventRepository, never()).save(any());
    }

    @Test
    void shouldReturnEmptyListWhenRepositoryHasNoEvents() {
        when(eventRepository.findAllByOrderByFechaDesc()).thenReturn(List.of());

        List<Event> result = eventService.findAll();

        assertEquals(0, result.size());
        verify(eventRepository).findAllByOrderByFechaDesc();
    }
}
