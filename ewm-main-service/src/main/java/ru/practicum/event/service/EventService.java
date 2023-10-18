package ru.practicum.event.service;

import ru.practicum.event.*;
import ru.practicum.event.controller.EventControllerPublic;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.participationRequest.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getAllByAdmin(List<Long> users,
                                     List<EventState> states,
                                     List<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     int from,
                                     int size);


    EventFullDto patchByAdmin(long eventId, EventUpdateAdminRequest eventUpdateAdminRequest);

    List<EventShortDto> getAllByInitiator(long userId, int from, int size);

    EventFullDto getByIdByInitiator(long userId, long eventId);

    List<ParticipationRequestDto> getParticipationRequestsByInitiator(long userId, long eventId);

    EventFullDto create(long userId, EventNewDto eventNewDto);

    EventFullDto patchByInitiator(long userId, long eventId, EventUpdateUserRequest updateEventUserRequest);

    EventRequestStatusUpdateResult patchParticipationRequestsByInitiator(long userId,
                                                                         long eventId,
                                                                         EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventShortDto> getAllPublic(String text,
                                     List<Long> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     boolean onlyAvailable,
                                     EventControllerPublic.SortMode sort,
                                     int from,
                                     int size,
                                     HttpServletRequest request);

    EventFullDto getByIdPublic(long eventId, HttpServletRequest request);
}
