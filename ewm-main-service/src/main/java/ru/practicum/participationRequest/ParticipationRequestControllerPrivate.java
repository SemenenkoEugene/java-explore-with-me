package ru.practicum.participationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
@Slf4j
public class ParticipationRequestControllerPrivate {

    private final ParticipationRequestService participationRequestService;

    @GetMapping()
    public List<ParticipationRequestDto> getAll(@PathVariable long userId) {
        log.debug("Получен GET запрос на просмотр запросов пользователя с ID {} на участие в событиях", userId);
        return participationRequestService.getAll(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable long userId,
                                          @RequestParam long eventId) {
        log.debug("Получен POST запрос на создание запроса на участие в событии {} от пользователя {}", eventId, userId);
        return participationRequestService.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto patch(@PathVariable long userId,
                                         @PathVariable long requestId) {
        log.debug("Получен PATCH запрос на отмену запроса {} на участие в событии пользователем с ID {}", requestId, userId);
        return participationRequestService.patch(userId, requestId);
    }
}
