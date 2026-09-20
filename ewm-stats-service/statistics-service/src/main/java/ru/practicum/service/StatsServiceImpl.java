package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;
import ru.practicum.repository.ViewStatsProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;

    @Override
    @Transactional
    public void saveHit(final EndpointHitDto endpointHitDto) {
        final EndpointHit endpointHit = endpointHitMapper.fromDto(endpointHitDto);
        statsRepository.save(endpointHit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> findStats(final LocalDateTime start, final LocalDateTime end, final List<String> uris, final boolean unique) {

        if (Objects.nonNull(end) && Objects.nonNull(start) && end.isBefore(start)) {
            throw new ValidationException("Invalid date range");
        }

        final List<ViewStatsProjection> results;

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
                .toList();
    }
}
