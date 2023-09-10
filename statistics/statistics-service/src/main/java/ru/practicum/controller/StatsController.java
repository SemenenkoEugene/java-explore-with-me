package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    private final StatsService statsService;

    @PostMapping("/hit")
    public void saveHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Создание записи для статистики {}", endpointHitDto.toString());
        statsService.saveHit(endpointHitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> findStats(
            @RequestParam(name = "start") @DateTimeFormat(pattern = DATE_TIME) LocalDateTime start,
            @RequestParam(name = "end") @DateTimeFormat(pattern = DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Статистика получена для start={},end={},uris={},unique={}", start.toString(), end.toString(), uris, unique);
        List<String> urisList = uris != null ? uris : Collections.emptyList();
        return statsService.findStats(start, end, urisList, unique);
    }
}
