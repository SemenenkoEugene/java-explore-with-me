package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);

        return compilationRepository.findAllByPublic(pinned, pageable).stream()
                .map(CompilationMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(long compId) {
        return CompilationMapper.INSTANCE.toDto(findCompilationById(compId));
    }

    @Override
    @Transactional
    public CompilationDto create(CompilationNewDto compilationNewDto) {
        List<Event> events = compilationNewDto.getEvents() != null && !compilationNewDto.getEvents().isEmpty() ?
                eventRepository.findAllById(compilationNewDto.getEvents()) :
                Collections.emptyList();

        if (compilationNewDto.getPinned() == null) {
            compilationNewDto.setPinned(false);
        }

        return CompilationMapper.INSTANCE.toDto(compilationRepository.save(CompilationMapper.INSTANCE.fromDto(compilationNewDto, events)));
    }

    @Override
    @Transactional
    public CompilationDto patch(long compId, CompilationUpdateRequest compilationUpdateRequest) {
        Compilation compilation = findCompilationById(compId);

        if (compilationUpdateRequest.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(compilationUpdateRequest.getEvents()));
        }

        Optional.ofNullable(compilationUpdateRequest.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(compilationUpdateRequest.getPinned()).ifPresent(compilation::setPinned);

        return CompilationMapper.INSTANCE.toDto(compilation);
    }

    @Override
    @Transactional
    public void delete(long compId) {
        findCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    private Compilation findCompilationById(long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
    }
}
