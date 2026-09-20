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

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Override
    public List<ParticipationRequestDto> getAll(final long userId) {
        return participationRequestRepository.findAllByRequesterId(userId).stream()
                .map(participationRequestMapper::toDto)
                .toList();
    }

    public ParticipationRequestDto create(final long userId, final long eventId) {
        final User requester = findUserById(userId);
        final Event event = findEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event initiator cannot submit a participation request for own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot participate in an unpublished event");
        }

        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= participationRequestRepository.countByEventIdAndStatus(eventId, ParticipationRequestState.CONFIRMED)) {
            throw new ConflictException("The number of participation requests has exceeded the limit for the event");
        }

        final ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(requester);
        participationRequest.setEvent(event);
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(event.getRequestModeration() && !event.getParticipantLimit().equals(0) ? ParticipationRequestState.PENDING : ParticipationRequestState.CONFIRMED);

        final ParticipationRequest saved = participationRequestRepository.save(participationRequest);

        return participationRequestMapper.toDto(saved);
    }

    public ParticipationRequestDto patch(final long userId, final long requestId) {
        final ParticipationRequest participationRequest = findParticipationRequestById(requestId);

        if (!participationRequest.getRequester().getId().equals(userId)) {
            throw new NotFoundException("No events available for editing were found");
        }

        participationRequest.setStatus(ParticipationRequestState.CANCELED);

        final ParticipationRequest saved = participationRequestRepository.save(participationRequest);

        return participationRequestMapper.toDto(saved);
    }

    private ParticipationRequest findParticipationRequestById(final long id) {
        return participationRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Participation request with id=%d was not found".formatted(id)));
    }

    private User findUserById(final long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=%d was not found".formatted(id)));
    }

    private Event findEventById(final long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=%d was not found".formatted(id)));
    }
}
