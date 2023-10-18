package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventRequestStatusUpdateRequest;
import ru.practicum.event.EventRequestStatusUpdateResult;
import ru.practicum.event.EventUpdateUserRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.participationRequest.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventControllerPrivate {
    private final EventService eventService;

    @GetMapping()
    public List<EventShortDto> getAll(@PathVariable long userId,
                                      @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                      @Valid @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Получен GET запрос на просмотр событий, добавленных пользователем с ID {}", userId);
        return eventService.getAllByInitiator(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable long userId,
                                @PathVariable long eventId) {
        log.debug("Получен GET запрос на просмотр события {}, добавленного пользователем с ID {}", eventId, userId);
        return eventService.getByIdByInitiator(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsByInitiator(@PathVariable long userId,
                                                                             @PathVariable long eventId) {
        log.debug("Получен GET запрос от инициатора {} события {} на просмотр запросов на участие в событии", userId, eventId);
        return eventService.getParticipationRequestsByInitiator(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable long userId,
                               @Valid @RequestBody EventNewDto eventNewDto) {
        log.debug("Получен POST запрос на добавление категории {} от пользователя с ID {}", eventNewDto.toString(), userId);
        return eventService.create(userId, eventNewDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventInfo(@PathVariable long userId,
                                       @PathVariable long eventId,
                                       @Valid @RequestBody EventUpdateUserRequest updateEventUserRequest) {
        log.debug("Получен PATCH запрос на обновление события {} пользователем с ID {}", eventId, userId);
        return eventService.patchByInitiator(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchEventRequests(@PathVariable long userId,
                                                             @PathVariable long eventId,
                                                             @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.debug("Получен PATCH запрос на обновление статусов запросов на участие в событии {}", eventId);
        return eventService.patchParticipationRequestsByInitiator(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
