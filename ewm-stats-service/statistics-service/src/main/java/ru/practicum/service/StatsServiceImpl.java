package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

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
            if (uris == null) {
                return statsRepository.getAllUniqueStats(start, end);
            } else {
                return statsRepository.getAllUniqueStatsWithUris(start, end, uris);
            }
        } else {
            if (uris == null) {
                return statsRepository.getAllStats(start, end);
            } else {
                return statsRepository.getAllStatsWithUris(start, end, uris);
            }
        }
    }
}
