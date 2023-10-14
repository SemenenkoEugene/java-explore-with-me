package ru.practicum.compilation.service;

import ru.practicum.compilation.CompilationUpdateRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(long compId);

    CompilationDto create(CompilationNewDto compilationNewDto);

    CompilationDto patch(long compId, CompilationUpdateRequest compilationUpdateRequest);

    void delete(long compId);
}
