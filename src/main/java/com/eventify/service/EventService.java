package com.eventify.service;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.dto.event.EventCreateDTO;
import com.eventify.dto.event.EventResponseDTO;
import com.eventify.dto.event.EventUpdateDTO;
import com.eventify.exception.BadRequestException;
import com.eventify.exception.BusinessRuleViolationException;
import com.eventify.exception.NotFoundException;
import com.eventify.exception.ResourceNotFoundException;
import com.eventify.mapper.EventMapper;
import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.CategoryRepository;
import com.eventify.repository.EventRepository;
import com.eventify.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventResponseDTO create(EventCreateDTO dto) {
        Event event = eventMapper.toEntity(dto);
        event.setActive(true);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toResponse(savedEvent);
    }

    @Transactional
    public Event create(Event event) {
        validate(event);
        event.setActive(event.getActive() == null ? true : event.getActive());
        resolveManagedRelations(event);
        return eventRepository.save(event);
    }

    @Transactional
    public EventResponseDTO createFromForm(EventCreateDTO dto) {
        return create(dto);
    }

    @Transactional
    public Event createFromForm(Event event, Long venueId, List<Long> categoryIds) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new NotFoundException("Debes seleccionar un venue valido"));
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds == null ? List.of() : categoryIds));
        event.setVenue(venue);
        event.setCategories(categories);
        return create(event);
    }

    @Transactional(readOnly = true)
    public List<Event> findAll() {
        return eventRepository.findAllByOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public List<EventResponseDTO> findAllResponses() {
        return eventRepository.findAllByOrderByFechaDesc()
                .stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<Event> findPage(int page, int size, String sort) {
        Sort safeSort = parseSort(sort);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), safeSort);
        return eventRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<EventResponseDTO> findPageResponses(int page, int size, String sort) {
        return findPage(page, size, sort).map(eventMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El evento con ID " + id + " no existe"));
    }

    @Transactional(readOnly = true)
    public EventResponseDTO findByIdResponse(Long id) {
        return eventMapper.toResponse(findById(id));
    }

    @Transactional
    public Event update(Long id, Event incoming) {
        validate(incoming);
        Event current = findById(id);
        current.setNombre(incoming.getNombre());
        current.setFecha(incoming.getFecha());
        current.setDescripcion(incoming.getDescripcion());
        current.setVenue(incoming.getVenue());
        current.setCategories(incoming.getCategories());
        current.setActive(true);
        resolveManagedRelations(current);
        return eventRepository.save(current);
    }

    @Transactional
    public EventResponseDTO update(Long id, EventUpdateDTO dto) {
        if (dto.getId() != null && !dto.getId().equals(id)) {
            throw new BusinessRuleViolationException("El ID del path no coincide con el ID del cuerpo de la peticion");
        }
        Event current = findById(id);
        Event mappedEvent = eventMapper.toEntity(dto);
        current.setNombre(mappedEvent.getNombre());
        current.setFecha(mappedEvent.getFecha());
        current.setDescripcion(mappedEvent.getDescripcion());
        current.setVenue(mappedEvent.getVenue());
        current.setCategories(mappedEvent.getCategories());
        current.setActive(true);
        return eventMapper.toResponse(eventRepository.save(current));
    }

    @Transactional(readOnly = true)
    public Slice<EventSummaryDTO> searchSummaries(String city, String category, Integer minCapacity, LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        return eventRepository.searchSummaries(normalize(city), normalize(category), minCapacity, startDate, endDate, pageable);
    }

    @Transactional(readOnly = true)
    public Slice<Event> searchDetailed(String city, String category, Integer minCapacity, LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        return eventRepository.searchDetailed(normalize(city), normalize(category), minCapacity, startDate, endDate, pageable);
    }

    @Transactional
    public void softDelete(Long id) {
        if (eventRepository.softDeleteById(id) == 0) {
            throw new ResourceNotFoundException("El evento con ID " + id + " no existe");
        }
    }

    private void validate(Event event) {
        if (event == null) {
            throw new BadRequestException("El evento no puede ser nulo");
        }
        if (event.getNombre() == null || event.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del evento no puede estar vacio");
        }
        if (event.getFecha() == null) {
            throw new BadRequestException("La fecha del evento es obligatoria");
        }
        if (event.getVenue() == null) {
            throw new BadRequestException("El evento debe tener un venue asignado");
        }
    }

    private void resolveManagedRelations(Event event) {
        if (event.getVenue() != null && event.getVenue().getId() != null) {
            Venue managedVenue = venueRepository.findById(event.getVenue().getId())
                    .orElseThrow(() -> new NotFoundException("El venue indicado no existe"));
            event.setVenue(managedVenue);
        }
        if (event.getCategories() != null && !event.getCategories().isEmpty()) {
            Set<Category> managedCategories = new HashSet<>();
            for (Category category : event.getCategories()) {
                if (category.getId() != null) {
                    Category managedCategory = categoryRepository.findById(category.getId())
                            .orElseThrow(() -> new NotFoundException("Una categoria indicada no existe"));
                    managedCategories.add(managedCategory);
                } else {
                    managedCategories.add(category);
                }
            }
            event.setCategories(managedCategories);
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "fecha");
        }
        String[] parts = sort.split(",");
        String property = parts[0].isBlank() ? "fecha" : parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, property);
    }
}
