package com.eventify.repository;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EventRepositoryDataJpaTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldPersistEventWithRelationsAndFindByPartialCityAndCategory() {
        Venue venue = venueRepository.save(new Venue(null, "Arena Test", "Calle 100 # 20-30", "Bogota", 1000));
        Category category = categoryRepository.save(new Category(null, "Rock", "Conciertos de rock"));
        Event event = new Event(null, "Concierto de ROCK", LocalDate.of(2026, 7, 10), "Prueba de repositorio");
        event.setVenue(venue);
        event.setCategories(Set.of(category));
        eventRepository.saveAndFlush(event);

        Slice<EventSummaryDTO> result = eventRepository.searchSummaries("bog", "rock", 500, null, null, PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).eventName()).isEqualTo("Concierto de ROCK");
        assertThat(result.getContent().get(0).city()).isEqualTo("Bogota");
    }

    @Test
    void shouldHideSoftDeletedEventsFromNormalQueries() {
        Venue venue = venueRepository.save(new Venue(null, "Arena Test", "Calle 100 # 20-30", "Bogota", 1000));
        Event event = new Event(null, "Evento Inactivo", LocalDate.of(2026, 7, 10), "Soft delete");
        event.setVenue(venue);
        event = eventRepository.saveAndFlush(event);

        eventRepository.softDeleteById(event.getId());
        eventRepository.flush();
        entityManager.clear();

        assertThat(eventRepository.findById(event.getId())).isEmpty();
    }
}
