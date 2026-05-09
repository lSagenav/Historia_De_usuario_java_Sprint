package com.eventify.repository;

import com.eventify.model.Event;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EventRepository {
    private final Map<Long, Event> events = new LinkedHashMap<>();
    private long sequence = 1L;

    public Event save(Event event) {
        if (event.getId() == null) {
            event.setId(sequence++);
        }
        events.put(event.getId(), event);
        return event;
    }

    public List<Event> findAll() {
        return new ArrayList<>(events.values());
    }

    public void clear() {
        events.clear();
        sequence = 1L;
    }
}
