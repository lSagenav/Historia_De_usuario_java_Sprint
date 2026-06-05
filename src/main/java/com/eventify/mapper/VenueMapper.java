package com.eventify.mapper;

import com.eventify.dto.venue.VenueCreateDTO;
import com.eventify.dto.venue.VenueResponseDTO;
import com.eventify.dto.venue.VenueUpdateDTO;
import com.eventify.model.Venue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VenueMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Venue toEntity(VenueCreateDTO dto);

    @Mapping(target = "events", ignore = true)
    Venue toEntity(VenueUpdateDTO dto);

    VenueResponseDTO toResponse(Venue venue);
}
