package com.eventify.mapper;

import com.eventify.dto.event.EventCreateDTO;
import com.eventify.dto.event.EventResponseDTO;
import com.eventify.dto.event.EventUpdateDTO;
import com.eventify.model.Category;
import com.eventify.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = EventReferenceMapper.class)
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", expression = "java(true)")
    @Mapping(source = "venueId", target = "venue")
    @Mapping(source = "categoryIds", target = "categories")
    Event toEntity(EventCreateDTO dto);

    @Mapping(target = "active", expression = "java(true)")
    @Mapping(source = "venueId", target = "venue")
    @Mapping(source = "categoryIds", target = "categories")
    Event toEntity(EventUpdateDTO dto);

    @Mapping(target = "venueName", source = "venue.nombre")
    @Mapping(target = "categoryNames", expression = "java(toCategoryNames(event.getCategories()))")
    EventResponseDTO toResponse(Event event);

    default List<String> toCategoryNames(Set<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }
        return categories.stream()
                .map(Category::getName)
                .sorted(Comparator.naturalOrder())
                .toList();
    }
}
