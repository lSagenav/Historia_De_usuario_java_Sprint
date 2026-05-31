package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.CategoryService;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final EventService eventService;
    private final VenueService venueService;
    private final CategoryService categoryService;

    @GetMapping
    public String dashboard(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        Slice<Event> events = eventService.searchDetailed(city, category, minCapacity, startDate, endDate, page, size);
        model.addAttribute("events", events.getContent());
        model.addAttribute("eventSlice", events);
        model.addAttribute("venues", venueService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("event", new Event());
        model.addAttribute("venue", new Venue());
        model.addAttribute("city", city);
        model.addAttribute("category", category);
        model.addAttribute("minCapacity", minCapacity);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("page", Math.max(page, 0));
        model.addAttribute("size", size);
        return "admin/dashboard";
    }

    @PostMapping("/events")
    public String saveEvent(@ModelAttribute Event event,
                            @RequestParam Long venueId,
                            @RequestParam(required = false) List<Long> categoryIds) {
        eventService.createFromForm(event, venueId, categoryIds);
        return "redirect:/admin";
    }

    @PostMapping("/venues")
    public String saveVenue(@ModelAttribute Venue venue) {
        venueService.create(venue);
        return "redirect:/admin";
    }
}
