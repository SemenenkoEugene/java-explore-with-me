package ru.practicum.event.service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryRepository;
import ru.practicum.event.*;
import ru.practicum.event.controller.EventControllerPublic;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.Location;
import ru.practicum.location.LocationDto;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationRepository;
import ru.practicum.participationRequest.*;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;
import ru.practicum.util.ConstantsDate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    public static final int TWO_HOURS = 2;
    public static final int ONE_HOURS = 1;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final ParticipationRequestMapper participationRequestMapper;

    @Value("${STATS_SERVER_URL}")
    private String statClientUrl;

    private StatsClient statsClient;

    @PostConstruct
    private void init() {
        statsClient = new StatsClient(statClientUrl);
    }

    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByAdmin(final List<Long> users, final List<EventState> states, final List<Long> categories,
                                            final LocalDateTime rangeStart, final LocalDateTime rangeEnd, final int from, final int size) {
        final Pageable pageable = PageRequest.of(from, size);

        final LocalDateTime actualRangeStart = rangeStart == null
                ? LocalDateTime.now()
                : rangeStart;

        final LocalDateTime actualRangeEnd = rangeEnd == null
                ? ConstantsDate.getMaxDateTime()
                : rangeEnd;


        final Page<Event> page = eventRepository.findAllByAdmin(users, states, categories, actualRangeStart, actualRangeEnd, pageable);

        final List<String> eventUrls = page.getContent().stream()
                .map(event -> "/events/" + event.getId())
                .toList();

        final List<ViewStatsDto> viewStatsDtos = statsClient.findStats(actualRangeStart.format(ConstantsDate.getDefaultDateTimeFormatter()),
                actualRangeEnd.format(ConstantsDate.getDefaultDateTimeFormatter()), eventUrls, true);

        return page.getContent().stream()
                .map(eventMapper::toFullDto)
                .peek(dto -> {
                    final Optional<ViewStatsDto> matchingStats = viewStatsDtos.stream()
                            .filter(statsDto -> statsDto.getUri().equals("/events/" + dto.getId()))
                            .findFirst();
                    dto.setViews(matchingStats
                            .map(ViewStatsDto::getHits)
                            .orElse(0L));
                })
                .peek(dto -> dto.setConfirmedRequests(participationRequestRepository.countByEventIdAndStatus(dto.getId(), ParticipationRequestState.CONFIRMED)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiator(final long userId, final int from, final int size) {
        final Pageable pageable = PageRequest.of(from, size);
        final Page<Event> page = eventRepository.findAllByInitiatorId(userId, pageable);

        return page.getContent().stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventFullDto getByIdByInitiator(final long userId, final long eventId) {
        final Event event = findEventById(eventId);
        checkInitiator(userId, eventId, event.getInitiator().getId());

        return eventMapper.toFullDto(event);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getParticipationRequestsByInitiator(final long userId, final long eventId) {
        findUserById(userId);
        final Event event = findEventById(eventId);

        return participationRequestRepository.findAllByEventId(event.getId()).stream()
                .map(participationRequestMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getAllPublic(final String text, final List<Long> categories, final Boolean paid,
                                            final LocalDateTime rangeStart, final LocalDateTime rangeEnd,
                                            final boolean onlyAvailable, final EventControllerPublic.SortMode sort,
                                            final int from, final int size, final HttpServletRequest request) {
        final List<Long> actualCategories = categories != null && categories.size() == 1 && categories.getFirst().equals(0L)
                ? null
                : categories;

        final LocalDateTime actualRangeStart =
                rangeStart == null ? LocalDateTime.now() : rangeStart;

        final LocalDateTime actualRangeEnd =
                rangeEnd == null ? ConstantsDate.getMaxDateTime() : rangeEnd;


        List<Event> eventList = eventRepository.getAllPublic(text, actualCategories, paid, actualRangeStart, actualRangeEnd);

        if (onlyAvailable) {
            eventList = eventList.stream()
                    .filter(event -> event.getParticipantLimit().equals(0)
                            || event.getParticipantLimit() < participationRequestRepository.countByEventIdAndStatus(event.getId(), ParticipationRequestState.CONFIRMED))
                    .toList();
        }

        final List<String> eventUrls = eventList.stream()
                .map(event -> "/events/" + event.getId())
                .toList();

        final List<ViewStatsDto> viewStatsDtos = statsClient.findStats(actualRangeStart.format(ConstantsDate.getDefaultDateTimeFormatter()),
                actualRangeEnd.format(ConstantsDate.getDefaultDateTimeFormatter()), eventUrls, true);

        final List<EventShortDto> eventShortDtoList = new ArrayList<>(eventList.stream()
                .map(eventMapper::toShortDto)
                .peek(dto -> {
                    final Optional<ViewStatsDto> matchingStats = viewStatsDtos.stream()
                            .filter(statsDto -> statsDto.getUri().equals("/events/" + dto.getId()))
                            .findFirst();
                    dto.setViews(matchingStats.map(ViewStatsDto::getHits).orElse(0L));
                })
                .peek(dto -> dto.setConfirmedRequests(participationRequestRepository.countByEventIdAndStatus(dto.getId(), ParticipationRequestState.CONFIRMED)))
                .toList());

        switch (sort) {
            case EVENT_DATE -> eventShortDtoList
                    .sort(Comparator.comparing(EventShortDto::getEventDate));
            case VIEWS -> eventShortDtoList
                    .sort(Comparator.comparing(EventShortDto::getViews)
                            .reversed());
        }

        if (from >= eventShortDtoList.size()) {
            return Collections.emptyList();
        }

        final int toIndex = Math.min(from + size, eventShortDtoList.size());
        return eventShortDtoList.subList(from, toIndex);
    }

    @Transactional(readOnly = true)
    public EventFullDto getByIdPublic(final long eventId, final HttpServletRequest request) {
        final Event event = findEventById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=%d was not found".formatted(eventId));
        }

        final List<String> eventUrls = Collections.singletonList("/events/" + event.getId());

        final List<ViewStatsDto> viewStatsDtos = statsClient.findStats(ConstantsDate.getMinDateTime().format(ConstantsDate.getDefaultDateTimeFormatter()),
                ConstantsDate.getMaxDateTime().plusYears(1).format(ConstantsDate.getDefaultDateTimeFormatter()), eventUrls, true);

        final EventFullDto dto = eventMapper.toFullDto(event);
        dto.setViews(viewStatsDtos.isEmpty()
                ? 0L
                : viewStatsDtos.getFirst().getHits());
        dto.setConfirmedRequests(participationRequestRepository.countByEventIdAndStatus(dto.getId(), ParticipationRequestState.CONFIRMED));

        return dto;
    }

    @Transactional
    public EventFullDto create(final long userId, final EventNewDto eventNewDto) {
        if (LocalDateTime.now().plusHours(TWO_HOURS).isAfter(eventNewDto.getEventTimestamp())) {
            throw new ConflictException("The event date must be 2 hours from the current time or later.");
        }

        final User user = findUserById(userId);
        final Category category = findCategoryById(eventNewDto.getCategory());
        final Location location = handleLocationDto(eventNewDto.getLocation());

        final Event event = eventMapper.fromDto(eventNewDto, category, location);

        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        if (Objects.isNull(eventNewDto.getPaid())) {
            event.setPaid(false);
        }

        if (Objects.isNull(eventNewDto.getParticipantLimit())) {
            event.setParticipantLimit(0);
        }

        if (Objects.isNull(eventNewDto.getRequestModeration())) {
            event.setRequestModeration(true);
        }
        final Event saved = eventRepository.save(event);

        return eventMapper.toFullDto(saved);
    }

    @Transactional
    public EventFullDto patchByAdmin(final long eventId, final EventUpdateAdminRequest updateEventAdminRequest) {
        Event event = findEventById(eventId);

        if (updateEventAdminRequest.getEventTimestamp() != null && LocalDateTime.now().plusHours(ONE_HOURS).isAfter(updateEventAdminRequest.getEventTimestamp())) {
            throw new ConflictException("The event date must be 1 hours from the current time or later.");
        }

        if (ObjectUtils.isNotEmpty(updateEventAdminRequest.getStateAction())) {
            if (updateEventAdminRequest.getStateAction() == EventUpdateAdminRequest.StateAction.PUBLISH_EVENT
                    && event.getState() != EventState.PENDING) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: %s".formatted(event.getState()));
            }

            if (updateEventAdminRequest.getStateAction() == EventUpdateAdminRequest.StateAction.REJECT_EVENT
                    && event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Cannot reject the event because it's not in the right state: %s".formatted(event.getState()));
            }
        }

        if (ObjectUtils.isNotEmpty(updateEventAdminRequest.getCategory())) {
            event.setCategory(findCategoryById(updateEventAdminRequest.getCategory()));
        }

        if (ObjectUtils.isNotEmpty(updateEventAdminRequest.getLocation())) {
            event.setLocation(handleLocationDto(updateEventAdminRequest.getLocation()));
        }

        Optional.ofNullable(updateEventAdminRequest.getTitle())
                .ifPresent(event::setTitle);
        Optional.ofNullable(updateEventAdminRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventAdminRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventAdminRequest.getEventTimestamp()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventAdminRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventAdminRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventAdminRequest.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (ObjectUtils.isNotEmpty(updateEventAdminRequest.getStateAction())) {
            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT -> {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> event.setState(EventState.CANCELED);
            }
        }

        event = eventRepository.save(event);

        return eventMapper.toFullDto(event);
    }

    @Transactional
    public EventFullDto patchByInitiator(final long userId, final long eventId, final EventUpdateUserRequest updateEventUserRequest) {
        Event event = findEventById(eventId);
        checkInitiator(userId, eventId, event.getInitiator().getId());

        if (ObjectUtils.isNotEmpty(updateEventUserRequest.getEventTimestamp()) && LocalDateTime.now().plusHours(2).isAfter(updateEventUserRequest.getEventTimestamp())) {
            throw new ConflictException("The event date must be 2 hours from the current time or later.");
        }

        if (!(event.getState().equals(EventState.CANCELED) || event.getState().equals(EventState.PENDING))) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (ObjectUtils.isNotEmpty(updateEventUserRequest.getCategory())) {
            event.setCategory(findCategoryById(updateEventUserRequest.getCategory()));
        }

        if (ObjectUtils.isNotEmpty(updateEventUserRequest.getLocation())) {
            event.setLocation(handleLocationDto(updateEventUserRequest.getLocation()));
        }

        Optional.ofNullable(updateEventUserRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventUserRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventUserRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventUserRequest.getEventTimestamp()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventUserRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventUserRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventUserRequest.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (ObjectUtils.isNotEmpty(updateEventUserRequest.getStateAction())) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }

        event = eventRepository.save(event);

        return eventMapper.toFullDto(event);
    }

    @Transactional
    public EventRequestStatusUpdateResult patchParticipationRequestsByInitiator(final long userId, final long eventId,
                                                                                final EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        findUserById(userId);
        final Event event = findEventById(eventId);

        long confirmLimit = event.getParticipantLimit() - participationRequestRepository.countByEventIdAndStatus(eventId, ParticipationRequestState.CONFIRMED);

        if (confirmLimit <= 0) {
            throw new ConflictException("The participant limit has been reached");
        }

        final List<ParticipationRequest> requestList = participationRequestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        final List<Long> notFoundIds = eventRequestStatusUpdateRequest.getRequestIds().stream()
                .filter(requestId -> requestList.stream().noneMatch(request -> request.getId().equals(requestId)))
                .toList();

        if (ObjectUtils.isNotEmpty(notFoundIds)) {
            throw new NotFoundException("Participation request with id=%s was not found".formatted(notFoundIds));
        }

        final EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        for (ParticipationRequest req : requestList) {
            if (!req.getEvent().getId().equals(eventId)) {
                throw new NotFoundException("Participation request with id=%d was not found".formatted(req.getId()));
            }

            if (confirmLimit <= 0) {
                req.setStatus(ParticipationRequestState.REJECTED);
                result.getRejectedRequests().add(participationRequestMapper.toDto(req));
                continue;
            }

            switch (eventRequestStatusUpdateRequest.getStatus()) {
                case CONFIRMED -> {
                    req.setStatus(ParticipationRequestState.CONFIRMED);
                    result.getConfirmedRequests().add(participationRequestMapper.toDto(req));
                    confirmLimit--;
                }
                case REJECTED -> {
                    req.setStatus(ParticipationRequestState.REJECTED);
                    result.getRejectedRequests().add(participationRequestMapper.toDto(req));
                }
            }
        }

        participationRequestRepository.saveAll(requestList);

        return result;
    }

    private Event findEventById(final long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=%d was not found".formatted(id)));
    }

    private User findUserById(final long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=%d was not found".formatted(id)));
    }

    private Category findCategoryById(final long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=%d was not found".formatted(id)));
    }

    private void checkInitiator(final long userId, final long eventId, final long initiatorId) {
        if (userId != initiatorId) {
            throw new NotFoundException("Event with id=%d was not found".formatted(eventId));
        }
    }

    private Location handleLocationDto(final LocationDto locationDto) {
        final Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        return location != null ? location : locationRepository.save(locationMapper.fromDto(locationDto));
    }
}
