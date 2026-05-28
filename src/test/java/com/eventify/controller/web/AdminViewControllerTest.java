package com.eventify.controller.web;

import com.eventify.controller.AdminViewController;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminViewController.class)
class AdminViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private VenueService venueService;

    @Test
    void shouldReturnDashboardViewWithModelAttributes() throws Exception {

        Event event = new Event(1L, "Expo Tech", LocalDate.now(), "Evento de tecnología");
        Venue venue = new Venue(1L, "Centro Eventos", "Calle 10", 300);

        when(eventService.findAll()).thenReturn(List.of(event));
        when(venueService.findAll()).thenReturn(List.of(venue));

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("venues"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("venue"));
    }
}
