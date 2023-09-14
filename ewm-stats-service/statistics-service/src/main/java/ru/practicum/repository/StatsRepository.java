package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.ViewStatsDto(eh.app, eh.uri, COUNT (eh.ip)) " +
            "from EndpointHit as eh " +
            "where eh.timestamp >= ?1 and eh.timestamp <= ?2 and eh.uri in ?3 " +
            "group by eh.app, eh.uri " +
            "order by COUNT (eh.ip) desc ")
    List<ViewStatsDto> getAllStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ViewStatsDto(eh.app,eh.uri,COUNT(DISTINCT(eh.ip))) " +
            "from EndpointHit  as eh " +
            "where eh.timestamp >= ?1 and eh.timestamp <= ?2 and eh.uri in ?3 " +
            "group by eh.app, eh.uri " +
            "order by COUNT(DISTINCT(eh.ip))")
    List<ViewStatsDto> getAllUniqueStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ViewStatsDto(eh.app, eh.uri, COUNT(eh.ip)) " +
            "from EndpointHit as eh " +
            "where eh.timestamp >= ?1 and eh.timestamp <= ?2 " +
            "group by eh.app, eh.uri " +
            "order by COUNT(eh.ip) desc ")
    List<ViewStatsDto> getAllStats(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ViewStatsDto(eh.app, eh.uri, COUNT(DISTINCT(eh.ip))) " +
            "from EndpointHit as eh " +
            "where eh.timestamp >= ?1 and eh.timestamp <= ?2 " +
            "group by eh.app, eh.uri " +
            "order by COUNT(DISTINCT(eh.ip)) desc ")
    List<ViewStatsDto> getAllUniqueStats(LocalDateTime start, LocalDateTime end);
}
