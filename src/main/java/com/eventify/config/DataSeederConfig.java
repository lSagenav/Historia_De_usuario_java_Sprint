package com.eventify.config;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.EventRepository;
import com.eventify.repository.VenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataSeederConfig {

    @Bean
    @ConditionalOnProperty(name = "eventify.seed.enabled", havingValue = "true")
    CommandLineRunner seedData(EventRepository eventRepository, VenueRepository venueRepository) {
        return args -> {
            if (eventRepository.count() == 0) {
                eventRepository.save(Event.builder()
                        .nombre("Tech Conference")
                        .fecha(LocalDate.now().plusDays(10))
                        .descripcion("Evento de tecnologia")
                        .build());
            }

            if (venueRepository.count() == 0) {
                venueRepository.save(Venue.builder()
                        .nombre("Centro de Eventos Medellin")
                        .direccion("El Poblado")
                        .capacidad(500)
                        .build());
            }
        };
    }
}
