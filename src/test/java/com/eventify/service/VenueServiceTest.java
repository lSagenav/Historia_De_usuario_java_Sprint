package com.eventify.service;

import com.eventify.exception.BadRequestException;
import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    @Test
    void shouldCreateVenueWhenDataIsValid() {
        Venue input = new Venue(null, "Teatro Central", "Calle 10", 500);
        Venue saved = new Venue(1L, "Teatro Central", "Calle 10", 500);
        when(venueRepository.save(input)).thenReturn(saved);

        Venue result = venueService.create(input);

        assertEquals(1L, result.getId());
        assertEquals("Teatro Central", result.getNombre());
        verify(venueRepository).save(input);
    }

    @Test
    void shouldThrowExceptionAndNotSaveWhenCapacityIsInvalid() {
        Venue input = new Venue(null, "Teatro Central", "Calle 10", 0);

        assertThrows(BadRequestException.class, () -> venueService.create(input));

        verify(venueRepository, never()).save(any());
    }

    @Test
    void shouldReturnEmptyListWhenRepositoryHasNoVenues() {
        when(venueRepository.findAll()).thenReturn(List.of());

        List<Venue> result = venueService.findAll();

        assertEquals(0, result.size());
        verify(venueRepository).findAll();
    }
}
