package com.eventify.controller.web;

import com.eventify.controller.AdminViewController;
import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.CategoryService;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
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

    @MockBean
    private CategoryService categoryService;

    @Test
    void shouldReturnDashboardViewWithModelAttributes() throws Exception {
        Venue venue = new Venue(1L, "Centro Eventos", "Calle 10 # 20-30", "Bogota", 300);
        Category category = new Category(1L, "Concerts", "Musica");
        Event event = new Event(1L, "Expo Tech", LocalDate.now(), "Evento de tecnologia", true, venue, Set.of(category));
        Slice<Event> slice = new SliceImpl<>(List.of(event), PageRequest.of(0, 20), false);

        when(eventService.searchDetailed(isNull(), isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt())).thenReturn(slice);
        when(venueService.findAll()).thenReturn(List.of(venue));
        when(categoryService.findAll()).thenReturn(List.of(category));

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("eventSlice"))
                .andExpect(model().attributeExists("venues"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("venue"));
    }
}
