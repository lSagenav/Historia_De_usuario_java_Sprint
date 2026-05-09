package com.eventify.controller;

import com.eventify.exception.BadRequestException;
import com.eventify.exception.GlobalExceptionHandler;
import com.eventify.model.Event;
import com.eventify.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new EventController(eventService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void shouldReturnCreatedWhenEventIsRegistered() throws Exception {
        Event input = new Event(null, "Conferencia Tech", LocalDate.of(2026, 6, 20), "Evento de tecnologia");
        Event saved = new Event(1L, "Conferencia Tech", LocalDate.of(2026, 6, 20), "Evento de tecnologia");
        when(eventService.create(input)).thenReturn(saved);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Conferencia Tech"));
    }

    @Test
    void shouldReturnBadRequestWhenServiceRejectsInvalidEvent() throws Exception {
        Event input = new Event(null, "", LocalDate.of(2026, 6, 20), "Evento invalido");
        when(eventService.create(input)).thenThrow(new BadRequestException("El nombre del evento no puede estar vacio"));

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El nombre del evento no puede estar vacio"));
    }

    @Test
    void shouldReturnOkAndEmptyListWhenNoEventsExist() throws Exception {
        when(eventService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
