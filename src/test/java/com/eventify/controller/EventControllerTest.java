package com.eventify.controller;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.dto.event.EventCreateDTO;
import com.eventify.dto.event.EventResponseDTO;
import com.eventify.exception.GlobalExceptionHandler;
import com.eventify.exception.ResourceNotFoundException;
import com.eventify.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
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
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void shouldReturnCreatedWithEventResponseDtoWhenEventIsRegistered() throws Exception {
        EventCreateDTO input = new EventCreateDTO();
        input.setNombre("Conferencia Tech");
        input.setFecha(LocalDate.now().plusDays(30));
        input.setDescripcion("Evento de tecnologia");
        input.setVenueId(1L);
        input.setCategoryIds(Set.of(1L, 3L));

        EventResponseDTO saved = new EventResponseDTO(
                1L,
                "Conferencia Tech",
                input.getFecha(),
                "Evento de tecnologia",
                "Movistar Arena",
                List.of("Concerts", "Conferences")
        );
        when(eventService.create(any(EventCreateDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Conferencia Tech"))
                .andExpect(jsonPath("$.venueName").value("Movistar Arena"))
                .andExpect(jsonPath("$.categoryNames[0]").value("Concerts"));
    }

    @Test
    void shouldReturnBadRequestProblemDetailWhenDtoIsInvalid() throws Exception {
        EventCreateDTO input = new EventCreateDTO();
        input.setNombre("");
        input.setFecha(LocalDate.now().minusDays(1));
        input.setDescripcion("Evento invalido");

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Datos de entrada invalidos"))
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.fecha").exists())
                .andExpect(jsonPath("$.errors.venueId").exists());
    }

    @Test
    void shouldReturnOkAndEmptySliceWhenNoEventsExist() throws Exception {
        Slice<EventSummaryDTO> emptySlice = new SliceImpl<>(List.of(), PageRequest.of(0, 20), false);
        when(eventService.searchSummaries(null, null, null, null, null, 0, 20)).thenReturn(emptySlice);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void shouldReturnNotFoundProblemDetailWhenEventDoesNotExist() throws Exception {
        when(eventService.findByIdResponse(9999L)).thenThrow(new ResourceNotFoundException("El evento con ID 9999 no existe"));

        mockMvc.perform(get("/api/events/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("El evento con ID 9999 no existe"))
                .andExpect(jsonPath("$.instance").value("/api/events/9999"));
    }
}
