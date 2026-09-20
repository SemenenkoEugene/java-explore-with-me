package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.BadRequestException;
import ru.practicum.util.ConstantsDate;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class EventControllerPublic {

    private final EventService eventService;

    @GetMapping()
    public List<EventShortDto> getAll(@RequestParam(defaultValue = "") final String text,
                                      @RequestParam(required = false) final List<Long> categories,
                                      @RequestParam(required = false) final Boolean paid,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = ConstantsDate.DATE_FORMAT) final LocalDateTime rangeStart,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = ConstantsDate.DATE_FORMAT) final LocalDateTime rangeEnd,
                                      @RequestParam(defaultValue = "false") final boolean onlyAvailable,
                                      @RequestParam(defaultValue = "VIEWS") final SortMode sort,
                                      @Valid @RequestParam(defaultValue = "0") @Min(0) final int from,
                                      @Valid @RequestParam(defaultValue = "10") @Min(1) final int size,
                                      final HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Start date must be before end date");
        }
        log.debug("Получен GET запрос на просмотр событий по фильтрам");
        return eventService.getAllPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("{eventId}")
    public EventFullDto getById(@PathVariable final long eventId,
                                final HttpServletRequest request) {

        log.debug("Получен GET запрос на просмотр события по ID {}", eventId);
        return eventService.getByIdPublic(eventId, request);
    }

    public enum SortMode {
        EVENT_DATE,
        VIEWS
    }
}
