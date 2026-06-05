package com.eventify.mapper;

import com.eventify.dto.category.CategoryCreateDTO;
import com.eventify.dto.category.CategoryResponseDTO;
import com.eventify.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Category toEntity(CategoryCreateDTO dto);

    CategoryResponseDTO toResponse(Category category);
}
