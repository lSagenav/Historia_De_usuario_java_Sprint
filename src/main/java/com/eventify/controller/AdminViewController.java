package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final EventService eventService;
    private final VenueService venueService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("events", eventService.findAll());
        model.addAttribute("venues", venueService.findAll());
        model.addAttribute("event", new Event());
        model.addAttribute("venue", new Venue());
        return "admin/dashboard";
    }

    @PostMapping("/events")
    public String saveEvent(@ModelAttribute Event event) {
        eventService.create(event);
        return "redirect:/admin";
    }

    @PostMapping("/venues")
    public String saveVenue(@ModelAttribute Venue venue) {
        venueService.create(venue);
        return "redirect:/admin";
    }
}
