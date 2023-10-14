package ru.practicum.participationRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.event.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getAll(long userId) {
        findUserById(userId);
        return participationRequestRepository.findAllByRequesterId(userId).stream()
                .map(ParticipationRequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto create(long userId, long eventId) {
        User requester = findUserById(userId);
        Event event = findEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event initiator cannot submit a participation request for own event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Cannot participate in an unpublished event");
        }

        if (event.getParticipantLimit() > 0) {
            if (event.getParticipantLimit() <= participationRequestRepository.countByEventIdAndStatus(eventId, ParticipationRequestState.CONFIRMED)) {
                throw new ConflictException("The number of participation requests has exceeded the limit for the event");
            }
        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(requester);
        participationRequest.setEvent(event);
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(event.getRequestModeration() && !event.getParticipantLimit().equals(0) ? ParticipationRequestState.PENDING : ParticipationRequestState.CONFIRMED);

        return ParticipationRequestMapper.INSTANCE.toDto(participationRequestRepository.save(participationRequest));
    }

    public ParticipationRequestDto patch(long userId, long requestId) {
        findUserById(userId);
        ParticipationRequest participationRequest = findParticipationRequestById(requestId);

        if (!participationRequest.getRequester().getId().equals(userId)) {
            throw new NotFoundException("No events available for editing were found");
        }

        participationRequest.setStatus(ParticipationRequestState.CANCELED);

        return ParticipationRequestMapper.INSTANCE.toDto(participationRequestRepository.save(participationRequest));
    }

    private ParticipationRequest findParticipationRequestById(long id) {
        return participationRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Participation request with id=" + id + " was not found"));
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }

    private Event findEventById(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
    }
}
