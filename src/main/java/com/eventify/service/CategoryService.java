package com.eventify.service;

import com.eventify.exception.BadRequestException;
import com.eventify.model.Category;
import com.eventify.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category create(Category category) {
        validate(category);
        return categoryRepository.save(category);
    }

    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    private void validate(Category category) {
        if (category == null) {
            throw new BadRequestException("La categoria no puede ser nula");
        }
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BadRequestException("El nombre de la categoria no puede estar vacio");
        }
    }
}
