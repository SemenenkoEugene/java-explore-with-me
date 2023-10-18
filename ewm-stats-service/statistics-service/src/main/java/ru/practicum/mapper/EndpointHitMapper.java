package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@Mapper
public interface EndpointHitMapper {

    EndpointHitMapper INSTANCE = Mappers.getMapper(EndpointHitMapper.class);

    EndpointHit fromDto(EndpointHitDto endpointHitDto);

    EndpointHitDto toDto(EndpointHit endpointHit);
}
