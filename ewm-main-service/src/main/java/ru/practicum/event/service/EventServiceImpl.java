package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.EventRequestStatusUpdateRequest;
import ru.practicum.event.EventRequestStatusUpdateResult;
import ru.practicum.event.EventState;
import ru.practicum.event.EventUpdateAdminRequest;
import ru.practicum.event.EventUpdateUserRequest;
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
import ru.practicum.participationRequest.ParticipationRequest;
import ru.practicum.participationRequest.ParticipationRequestDto;
import ru.practicum.participationRequest.ParticipationRequestMapper;
import ru.practicum.participationRequest.ParticipationRequestRepository;
import ru.practicum.participationRequest.ParticipationRequestState;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;
import ru.practicum.util.ConstantsDate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Value("${STATS_SERVER_URL:http://localhost:9090}")
    private String statClientUrl;

    private StatsClient statsClient;

    @PostConstruct
    private void init() {
        statsClient = new StatsClient(statClientUrl);
    }

    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByAdmin(List<Long> users,
                                            List<EventState> states,
                                            List<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            int from,
                                            int size) {
        Pageable pageable = PageRequest.of(from, size);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd == null) {
            rangeEnd = ConstantsDate.getMaxDateTime();
        }

        Page<Event> page = eventRepository.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, pageable);

        List<String> eventUrls = page.getContent().stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        List<ViewStatsDto> viewStatsDtos = statsClient.findStats(rangeStart.format(ConstantsDate.getDefaultDateTimeFormatter()),
                rangeEnd.format(ConstantsDate.getDefaultDateTimeFormatter()), eventUrls, true);

        return page.getContent().stream()
                .map(EventMapper.INSTANCE::toFullDto)
                .peek(dto -> {
                    Optional<ViewStatsDto> matchingStats = viewStatsDtos.stream()
                            .filter(statsDto -> statsDto.getUri().equals("/events/" + dto.getId()))
                            .findFirst();
                    dto.setViews(matchingStats.map(ViewStatsDto::getHits).orElse(0L));
                })
                .peek(dto -> dto.setConfirmedRequests(participationRequestRepository.countByEventIdAndStatus(dto.getId(), ParticipationRequestState.CONFIRMED)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiator(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Event> page = eventRepository.findAllByInitiatorId(userId, pageable);

        return page.getContent().stream()
                .map(EventMapper.INSTANCE::toShortDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullDto getByIdByInitiator(long userId, long eventId) {
        Event event = findEventById(eventId);
        checkInitiator(userId, eventId, event.getInitiator().getId());

        return EventMapper.INSTANCE.toFullDto(event);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getParticipationRequestsByInitiator(long userId, long eventId) {
        findUserById(userId);
        findEventById(eventId);

        return participationRequestRepository.findAllByEventId(eventId).stream()
                .map(ParticipationRequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getAllPublic(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            boolean onlyAvailable,
                                            EventControllerPublic.SortMode sort,
                                            int from,
                                            int size,
                                            HttpServletRequest request) {
        if (categories != null && categories.size() == 1 && categories.get(0).equals(0L)) {
            categories = null;
        }

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd == null) {
            rangeEnd = ConstantsDate.getMaxDateTime();
        }

        List<Event> eventList = eventRepository.getAllPublic(text, categories, paid, rangeStart, rangeEnd);

        if (onlyAvailable) {
            eventList = eventList.stream()
                    .filter(event -> event.getParticipantLimit().equals(0)
                                     || event.getParticipantLimit() < participationRequestRepository.countByEventIdAndStatus(event.getId(), ParticipationRequestState.CONFIRMED))
                    .collect(Collectors.toList());
        }

        List<String> eventUrls = eventList.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        List<ViewStatsDto> viewStatsDtos = statsClient.findStats(rangeStart.format(ConstantsDate.getDefaultDateTimeFormatter()),
                rangeEnd.format(ConstantsDate.getDefaultDateTimeFormatter()), eventUrls, true);

        List<EventShortDto> eventShortDtoList = eventList.stream()
                .map(EventMapper.INSTANCE::toShortDto)
                .peek(dto -> {
                    Optional<ViewStatsDto> matchingStats = viewStatsDtos.stream()
                            .filter(statsDto -> statsDto.getUri().equals("/events/" + dto.getId()))
                            .findFirst();
                    dto.setViews(matchingStats.map(ViewStatsDto::getHits).orElse(0L));
                })
                .peek(dto -> dto.setConfirmedRequests(participationRequestRepository.countByEventIdAndStatus(dto.getId(), ParticipationRequestState.CONFIRMED)))
                .collect(Collectors.toList());

        switch (sort) {
            case EVENT_DATE:
                eventShortDtoList.sort(Comparator.comparing(EventShortDto::getEventDate));
                break;
            case VIEWS:
                Collections.sort(eventShortDtoList, Comparator.comparing(EventShortDto::getViews).reversed());
                break;
        }

        if (from >= eventShortDtoList.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(from + size, eventShortDtoList.size());
        return eventShortDtoList.subList(from, toIndex);
    }

    @Transactional(readOnly = true)
    public EventFullDto getByIdPublic(long eventId, HttpServletRequest request) {
        Event event = findEventById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        List<String> eventUrls = Collections.singletonList("/events/" + event.getId());

        List<ViewStatsDto> viewStatsDtos = statsClient.findStats(ConstantsDate.getMinDateTime().format(ConstantsDate.getDefaultDateTimeFormatter()),
                ConstantsDate.getMaxDateTime().plusYears(1).format(ConstantsDate.getDefaultDateTimeFormatter()), eventUrls, true);

        EventFullDto dto = EventMapper.INSTANCE.toFullDto(event);
        dto.setViews(viewStatsDtos.isEmpty() ? 0L : viewStatsDtos.get(0).getHits());
        dto.setConfirmedRequests(participationRequestRepository.countByEventIdAndStatus(dto.getId(), ParticipationRequestState.CONFIRMED));

        return dto;
    }

    @Transactional
    public EventFullDto create(long userId, EventNewDto eventNewDto) {
        if (LocalDateTime.now().plusHours(TWO_HOURS).isAfter(eventNewDto.getEventTimestamp())) {
            throw new ConflictException("The event date must be 2 hours from the current time or later.");
        }

        User user = findUserById(userId);
        Category category = findCategoryById(eventNewDto.getCategory());
        Location location = handleLocationDto(eventNewDto.getLocation());

        Event event = EventMapper.INSTANCE.fromDto(eventNewDto, category, location);

        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        if (eventNewDto.getPaid() == null) {
            event.setPaid(false);
        }

        if (eventNewDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }

        if (eventNewDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        return EventMapper.INSTANCE.toFullDto(eventRepository.save(event));
    }

    @Transactional
    public EventFullDto patchByAdmin(long eventId, EventUpdateAdminRequest updateEventAdminRequest) {
        Event event = findEventById(eventId);

        if (updateEventAdminRequest.getEventTimestamp() != null && LocalDateTime.now().plusHours(ONE_HOURS).isAfter(updateEventAdminRequest.getEventTimestamp())) {
            throw new ConflictException("The event date must be 1 hours from the current time or later.");
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(EventUpdateAdminRequest.StateAction.PUBLISH_EVENT) &&
                !event.getState().equals(EventState.PENDING)) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState());
            }

            if (updateEventAdminRequest.getStateAction().equals(EventUpdateAdminRequest.StateAction.REJECT_EVENT) &&
                event.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictException("Cannot reject the event because it's not in the right state: " + event.getState());
            }
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(findCategoryById(updateEventAdminRequest.getCategory()));
        }

        if (updateEventAdminRequest.getLocation() != null) {
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

        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        event = eventRepository.save(event);

        return EventMapper.INSTANCE.toFullDto(event);
    }

    @Transactional
    public EventFullDto patchByInitiator(long userId, long eventId, EventUpdateUserRequest updateEventUserRequest) {
        Event event = findEventById(eventId);
        checkInitiator(userId, eventId, event.getInitiator().getId());

        if (updateEventUserRequest.getEventTimestamp() != null && LocalDateTime.now().plusHours(2).isAfter(updateEventUserRequest.getEventTimestamp())) {
            throw new ConflictException("The event date must be 2 hours from the current time or later.");
        }

        if (!(event.getState().equals(EventState.CANCELED) ||
              event.getState().equals(EventState.PENDING))) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(findCategoryById(updateEventUserRequest.getCategory()));
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(handleLocationDto(updateEventUserRequest.getLocation()));
        }

        Optional.ofNullable(updateEventUserRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventUserRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventUserRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventUserRequest.getEventTimestamp()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventUserRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventUserRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventUserRequest.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        event = eventRepository.save(event);

        return EventMapper.INSTANCE.toFullDto(event);
    }

    @Transactional
    public EventRequestStatusUpdateResult patchParticipationRequestsByInitiator(long userId,
                                                                                long eventId,
                                                                                EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        findUserById(userId);
        Event event = findEventById(eventId);

        long confirmLimit = event.getParticipantLimit() - participationRequestRepository.countByEventIdAndStatus(eventId, ParticipationRequestState.CONFIRMED);

        if (confirmLimit <= 0) {
            throw new ConflictException("The participant limit has been reached");
        }

        List<ParticipationRequest> requestList = participationRequestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        List<Long> notFoundIds = eventRequestStatusUpdateRequest.getRequestIds().stream()
                .filter(requestId -> requestList.stream().noneMatch(request -> request.getId().equals(requestId)))
                .collect(Collectors.toList());

        if (!notFoundIds.isEmpty()) {
            throw new NotFoundException("Participation request with id=" + notFoundIds + " was not found");
        }

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        for (ParticipationRequest req : requestList) {
            if (!req.getEvent().getId().equals(eventId)) {
                throw new NotFoundException("Participation request with id=" + req.getId() + " was not found");
            }

            if (confirmLimit <= 0) {
                req.setStatus(ParticipationRequestState.REJECTED);
                result.getRejectedRequests().add(ParticipationRequestMapper.INSTANCE.toDto(req));
                continue;
            }

            switch (eventRequestStatusUpdateRequest.getStatus()) {
                case CONFIRMED:
                    req.setStatus(ParticipationRequestState.CONFIRMED);
                    result.getConfirmedRequests().add(ParticipationRequestMapper.INSTANCE.toDto(req));
                    confirmLimit--;
                    break;
                case REJECTED:
                    req.setStatus(ParticipationRequestState.REJECTED);
                    result.getRejectedRequests().add(ParticipationRequestMapper.INSTANCE.toDto(req));
                    break;
            }
        }

        participationRequestRepository.saveAll(requestList);

        return result;
    }

    private Event findEventById(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }

    private Category findCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
    }

    private void checkInitiator(long userId, long eventId, long initiatorId) {
        if (userId != initiatorId) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
    }

    private Location handleLocationDto(LocationDto locationDto) {
        Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        return location != null ? location : locationRepository.save(LocationMapper.INSTANCE.fromDto(locationDto));
    }
}
