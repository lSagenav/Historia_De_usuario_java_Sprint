package com.eventify.repository;

import com.eventify.model.Venue;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class VenueRepository {
    private final Map<Long, Venue> venues = new LinkedHashMap<>();
    private long sequence = 1L;

    public Venue save(Venue venue) {
        if (venue.getId() == null) {
            venue.setId(sequence++);
        }
        venues.put(venue.getId(), venue);
        return venue;
    }

    public List<Venue> findAll() {
        return new ArrayList<>(venues.values());
    }

    public void clear() {
        venues.clear();
        sequence = 1L;
    }
}
