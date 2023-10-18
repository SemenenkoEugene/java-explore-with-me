package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.CompilationUpdateRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationControllerAdmin {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody CompilationNewDto compilationNewDto) {
        log.debug("Получен POST запрос на создание подборки событий {}", compilationNewDto.toString());
        return compilationService.create(compilationNewDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto patch(@PathVariable long compId,
                                @Valid @RequestBody CompilationUpdateRequest compilationUpdateRequest) {
        log.debug("Получен PATCH запрос на обновление подборки событий {} с ID {}", compilationUpdateRequest.toString(), compId);
        return compilationService.patch(compId, compilationUpdateRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long compId) {
        log.debug("Получен DELETE запрос на удаление подборки событий с ID {}", compId);
        compilationService.delete(compId);
    }
}
