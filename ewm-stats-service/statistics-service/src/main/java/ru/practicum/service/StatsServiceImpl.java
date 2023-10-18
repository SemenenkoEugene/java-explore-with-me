package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.repository.StatsRepository;
import ru.practicum.repository.ViewStatsProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        statsRepository.save(EndpointHitMapper.INSTANCE.fromDto(endpointHitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        if (end != null && start != null) {
            if (!end.isAfter(start)) {
                throw new ValidationException("Invalid date range");
            }
        }

        List<ViewStatsProjection> results;
        if (unique) {
            results = statsRepository.findUniqueStats(start, end, uris);
        } else {
            results = statsRepository.findNotUniqueStats(start, end, uris);
        }
        return results.stream()
                .map(result -> ViewStatsDto.builder()
                        .app(result.getApp())
                        .uri(result.getUri())
                        .hits(result.getHits())
                        .build())
                .collect(Collectors.toList());
    }
}
