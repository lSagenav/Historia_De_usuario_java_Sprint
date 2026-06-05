package com.eventify.service;

import com.eventify.dto.category.CategoryCreateDTO;
import com.eventify.dto.category.CategoryResponseDTO;
import com.eventify.exception.BadRequestException;
import com.eventify.exception.DuplicateResourceException;
import com.eventify.mapper.CategoryMapper;
import com.eventify.model.Category;
import com.eventify.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponseDTO create(CategoryCreateDTO dto) {
        validateDuplicateName(dto.getName());
        Category category = categoryMapper.toEntity(dto);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public Category create(Category category) {
        validate(category);
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAllResponses() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    private void validate(Category category) {
        if (category == null) {
            throw new BadRequestException("La categoria no puede ser nula");
        }
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BadRequestException("El nombre de la categoria no puede estar vacio");
        }
    }

    private void validateDuplicateName(String name) {
        if (name != null && categoryRepository.existsByNameIgnoreCase(name.trim())) {
            throw new DuplicateResourceException("Ya existe una categoria con el nombre " + name);
        }
    }
}
