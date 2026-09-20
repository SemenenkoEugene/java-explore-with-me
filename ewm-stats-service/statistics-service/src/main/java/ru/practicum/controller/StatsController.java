package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody final EndpointHitDto endpointHitDto) {
        log.info("Создание записи для статистики {}", endpointHitDto.toString());
        statsService.saveHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> findStats(
            @RequestParam(name = "start", required = false) @DateTimeFormat(pattern = DATE_TIME) final LocalDateTime start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(pattern = DATE_TIME) final LocalDateTime end,
            @RequestParam(required = false) final List<String> uris,
            @RequestParam(defaultValue = "false") final boolean unique) {
        if (start == null) {
            throw new ValidationException("Required request parameter 'start' for method parameter type LocalDateTime is not present");
        }
        if (end == null) {
            throw new ValidationException("Required request parameter 'end' for method parameter type LocalDateTime is not present");
        }
        log.info("Статистика получена для start={},end={},uris={},unique={}", start, end, uris, unique);
        return statsService.findStats(start, end, uris, unique);
    }
}
