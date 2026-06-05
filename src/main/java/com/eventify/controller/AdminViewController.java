package com.eventify.controller;

import com.eventify.dto.event.EventCreateDTO;
import com.eventify.dto.venue.VenueCreateDTO;
import com.eventify.exception.BusinessRuleViolationException;
import com.eventify.exception.DuplicateResourceException;
import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Event;
import com.eventify.service.CategoryService;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

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
        populateDashboardModel(city, category, minCapacity, startDate, endDate, page, size, model);
        return "admin/dashboard";
    }

    @PostMapping("/events")
    public String saveEvent(@Valid @ModelAttribute("event") EventCreateDTO event,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateDashboardModel(null, null, null, null, null, 0, 20, model);
            return "admin/dashboard";
        }
        try {
            eventService.createFromForm(event);
            redirectAttributes.addFlashAttribute("successMessage", "Evento creado correctamente");
            return "redirect:/admin";
        } catch (ResourceNotFoundException | BusinessRuleViolationException exception) {
            bindingResult.reject("event.error", exception.getMessage());
            populateDashboardModel(null, null, null, null, null, 0, 20, model);
            return "admin/dashboard";
        }
    }

    @PostMapping("/venues")
    public String saveVenue(@Valid @ModelAttribute("venue") VenueCreateDTO venue,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateDashboardModel(null, null, null, null, null, 0, 20, model);
            return "admin/dashboard";
        }
        try {
            venueService.create(venue);
            redirectAttributes.addFlashAttribute("successMessage", "Venue creado correctamente");
            return "redirect:/admin";
        } catch (DuplicateResourceException | BusinessRuleViolationException exception) {
            bindingResult.reject("venue.error", exception.getMessage());
            populateDashboardModel(null, null, null, null, null, 0, 20, model);
            return "admin/dashboard";
        }
    }

    private void populateDashboardModel(String city,
                                        String category,
                                        Integer minCapacity,
                                        LocalDate startDate,
                                        LocalDate endDate,
                                        int page,
                                        int size,
                                        Model model) {
        Slice<Event> events = eventService.searchDetailed(city, category, minCapacity, startDate, endDate, page, size);
        model.addAttribute("events", events.getContent());
        model.addAttribute("eventSlice", events);
        model.addAttribute("venues", venueService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        if (!model.containsAttribute("event")) {
            model.addAttribute("event", new EventCreateDTO());
        }
        if (!model.containsAttribute("venue")) {
            model.addAttribute("venue", new VenueCreateDTO());
        }
        model.addAttribute("city", city);
        model.addAttribute("category", category);
        model.addAttribute("minCapacity", minCapacity);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("page", Math.max(page, 0));
        model.addAttribute("size", size);
    }
}
