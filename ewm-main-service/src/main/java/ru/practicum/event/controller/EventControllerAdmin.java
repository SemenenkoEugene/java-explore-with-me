package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.EventState;
import ru.practicum.event.EventUpdateAdminRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.service.EventService;
import ru.practicum.util.ConstantsDate;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventControllerAdmin {
    private final EventService eventService;

    @GetMapping()
    public List<EventFullDto> get(@RequestParam(required = false) final List<Long> users,
                                  @RequestParam(required = false) final List<EventState> states,
                                  @RequestParam(required = false) final List<Long> categories,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = ConstantsDate.DATE_FORMAT) final LocalDateTime rangeStart,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = ConstantsDate.DATE_FORMAT) final LocalDateTime rangeEnd,
                                  @Valid @RequestParam(defaultValue = "0") @Min(0) final int from,
                                  @Valid @RequestParam(defaultValue = "10") @Min(1) final int size) {
        log.debug("Получен GET запрос на просмотр событий по фильтрам");
        return eventService.getAllByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patch(@PathVariable final long eventId,
                              @Valid @RequestBody final EventUpdateAdminRequest updateEventAdminRequest) {
        log.debug("Получен PATCH запрос на обновление события с ID {} администратором", eventId);
        return eventService.patchByAdmin(eventId, updateEventAdminRequest);
    }
}
