package com.eventify.config;

import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.CategoryService;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Set;

@Configuration
public class DataSeederConfig {

    @Bean
    @ConditionalOnProperty(name = "eventify.seed.enabled", havingValue = "true")
    public CommandLineRunner seedData(EventService eventService, VenueService venueService, CategoryService categoryService) {
        return args -> {
            Venue venue = venueService.create(new Venue(null, "Movistar Arena", "Diagonal 61C # 26-36", "Bogota", 14000));
            Category category = categoryService.create(new Category(null, "Concerts", "Eventos musicales en vivo"));
            Event event = new Event(null, "Concierto Rock", LocalDate.now().plusDays(30), "Evento musical de apertura");
            event.setVenue(venue);
            event.setCategories(Set.of(category));
            eventService.create(event);
        };
    }
}
