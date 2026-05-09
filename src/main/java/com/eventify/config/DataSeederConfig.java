package com.eventify.config;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataSeederConfig {

    @Bean
    @ConditionalOnProperty(name = "eventify.seed.enabled", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner seedData(EventService eventService, VenueService venueService) {
        return args -> {
            venueService.create(new Venue(null, "Movistar Arena", "Diagonal 61C #26-36, Bogota", 14000));
            venueService.create(new Venue(null, "Centro de Convenciones", "Av. Principal 123", 800));

            eventService.create(new Event(null, "Concierto Rock", LocalDate.now().plusDays(30), "Evento musical de apertura"));
            eventService.create(new Event(null, "Conferencia Tech", LocalDate.now().plusDays(45), "Evento de tecnologia y comunidad"));
        };
    }
}
