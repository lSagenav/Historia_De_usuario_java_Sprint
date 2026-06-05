package com.eventify.mapper;

import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Category;
import com.eventify.model.Venue;
import com.eventify.repository.CategoryRepository;
import com.eventify.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EventReferenceMapper {
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;

    public Venue mapVenue(Long venueId) {
        if (venueId == null) {
            return null;
        }
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("El venue con ID " + venueId + " no existe"));
    }

    public Set<Category> mapCategories(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));
        if (categories.size() != categoryIds.size()) {
            throw new ResourceNotFoundException("Una o mas categorias indicadas no existen");
        }
        return categories;
    }
}
