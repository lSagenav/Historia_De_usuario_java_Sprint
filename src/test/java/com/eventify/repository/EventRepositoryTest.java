package com.eventify.repository;

import com.eventify.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    private EventRepository repository;

    @Test
    void shouldSaveAndFindByNombreContaining() {
        Event event = Event.builder()
                .nombre("Spring Boot Conf")
                .fecha(LocalDate.now())
                .descripcion("Evento")
                .build();

        repository.save(event);
        List<Event> results = repository.findByNombreContainingIgnoreCase("spring");

        assertFalse(results.isEmpty());
    }
}
