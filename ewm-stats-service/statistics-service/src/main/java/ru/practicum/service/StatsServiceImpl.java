package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit hit = HitMapper.toHit(endpointHitDto);
        statsRepository.save(hit);
    }

    @Override
    public List<ViewStatsDto> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            List<EndpointHit> uniqueHits = statsRepository.findDistinctFirstByUriInAndTimestampBetween(uris, start, end);
            return mapStatsByHits(uniqueHits);
        }
        if (!uris.isEmpty()) {
            List<EndpointHit> hits = statsRepository.findAllByUriInAndTimestampBetween(uris, start, end);
            return mapStatsByHits(hits);
        } else {
            List<EndpointHit> hitsWithoutUris = statsRepository.findAllByTimestampBetween(start, end);
            return mapStatsByHitsWithoutUris(hitsWithoutUris);
        }

    }

    private List<ViewStatsDto> mapStatsByHitsWithoutUris(List<EndpointHit> hitsWithoutUris) {
        Map<String, Long> uriCounts = hitsWithoutUris.stream()
                .collect(Collectors.groupingBy(EndpointHit::getUri, Collectors.counting()));
        List<ViewStatsDto> stats = new ArrayList<>();
        for (EndpointHit hit : hitsWithoutUris) {
            ViewStatsDto stat = HitMapper.toViewDto(hit);
            Long uriCount = uriCounts.get(stat.getUri());
            stat.setHits(uriCount);
            stats.add(stat);
        }
        return stats.stream()
                .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                .collect(Collectors.toList());
    }

    private ArrayList<ViewStatsDto> mapStatsByHits(List<EndpointHit> uniqueHits) {
        Map<String, Long> uriCounts = uniqueHits.stream()
                .collect(Collectors.groupingBy(EndpointHit::getUri, Collectors.counting()));
        Set<ViewStatsDto> stats = new TreeSet<>(Comparator.comparing(ViewStatsDto::getHits).reversed());
        for (EndpointHit uniqueHit : uniqueHits) {
            ViewStatsDto stat = HitMapper.toViewDto(uniqueHit);
            Long uriCount = uriCounts.get(stat.getUri());
            stat.setHits(uriCount);
            stats.add(stat);
        }
        return new ArrayList<>(stats);
    }
}
