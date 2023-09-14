package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ViewStatsDto;

@UtilityClass
public class ViewStatsMapper {
    public ViewStatsDto toDto(ViewStatsDto dto) {
        return ViewStatsDto.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .hits(dto.getHits())
                .build();
    }
}
