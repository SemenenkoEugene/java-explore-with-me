package ru.practicum.participationRequest;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> getAll(long userId);

    ParticipationRequestDto create(long userId, long eventId);

    ParticipationRequestDto patch(long userId, long requestId);
}
