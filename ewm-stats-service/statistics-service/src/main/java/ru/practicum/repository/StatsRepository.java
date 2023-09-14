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
    @Query("SELECT eh.app AS app, eh.uri AS uri, COUNT(eh.ip) AS hits " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end " +
            "GROUP BY eh.app, eh.uri, eh.ip " +
            "ORDER BY hits DESC ")
    List<ViewStatsDto> countByTimestamp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(DISTINCT h.ip) AS hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri, h.ip " +
            "ORDER BY hits DESC "
    )
    List<ViewStatsDto> countByTimestampUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(DISTINCT h.ip) AS hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "AND h.uri IN (?3) " +
            "GROUP BY h.app, h.uri ORDER BY hits DESC ")
    List<ViewStatsDto> findStatWithUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(h.ip) AS hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "AND h.uri IN (?3) " +
            "GROUP BY h.app, h.uri ORDER BY hits DESC ")
    List<ViewStatsDto> findStatNotUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}
