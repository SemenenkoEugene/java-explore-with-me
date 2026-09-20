package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.Compilation;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.CompilationRepository;
import ru.practicum.compilation.CompilationUpdateRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(final Boolean pinned, final int from, final int size) {
        final Pageable pageable = PageRequest.of(from, size);

        return compilationRepository.findAllByPublic(pinned, pageable).stream()
                .map(compilationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(final long compId) {
        final Compilation compilation = findCompilationById(compId);
        return compilationMapper.toDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto create(final CompilationNewDto compilationNewDto) {
        final List<Event> events;

        if (CollectionUtils.isNotEmpty(compilationNewDto.getEvents())) {
            events = eventRepository.findAllById(compilationNewDto.getEvents());
        } else {
            events = Collections.emptyList();
        }

        if (Objects.isNull(compilationNewDto.getPinned())) {
            compilationNewDto.setPinned(false);
        }

        final Compilation compilation = compilationMapper.fromDto(compilationNewDto, events);

        final Compilation saved = compilationRepository.save(compilation);

        return compilationMapper.toDto(saved);
    }

    @Override
    @Transactional
    public CompilationDto patch(final long compId, final CompilationUpdateRequest compilationUpdateRequest) {
        final Compilation compilation = findCompilationById(compId);

        if (Objects.nonNull(compilationUpdateRequest.getEvents())) {
            final List<Event> eventList = eventRepository.findAllById(compilationUpdateRequest.getEvents());
            compilation.setEvents(eventList);
        }

        if (Objects.nonNull(compilationUpdateRequest.getPinned())) {
            compilation.setPinned(compilationUpdateRequest.getPinned());
        }

        if (Objects.nonNull(compilationUpdateRequest.getTitle())) {
            compilation.setTitle(compilationUpdateRequest.getTitle());
        }

        return compilationMapper.toDto(compilation);
    }

    @Override
    @Transactional
    public void delete(final long compId) {
        final Compilation compilation = findCompilationById(compId);
        compilationRepository.deleteById(compilation.getId());
    }

    private Compilation findCompilationById(final long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=%d was not found".formatted(id)));
    }
}
