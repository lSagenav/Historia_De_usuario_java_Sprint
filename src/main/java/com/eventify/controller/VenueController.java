package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService service;

    @PostMapping
    public ResponseEntity<Venue> create(@RequestBody Venue venue) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(venue));
    }

    @GetMapping
    public ResponseEntity<Page<Venue>> findAll(@PageableDefault(size = 5, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venue> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venue> update(@PathVariable Long id, @RequestBody Venue venue) {
        return ResponseEntity.ok(service.update(id, venue));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
