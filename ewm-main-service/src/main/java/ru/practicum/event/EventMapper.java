package ru.practicum.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.category.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.location.Location;

@Mapper
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventDate", source = "dto.eventTimestamp")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    Event fromDto(EventNewDto dto, Category category, Location location);

    EventFullDto toFullDto(Event entity);

    EventShortDto toShortDto(Event entity);
}
