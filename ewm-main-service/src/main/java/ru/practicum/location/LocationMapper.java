package ru.practicum.location;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LocationMapper {

    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    LocationDto toDto(Location entity);

    @Mapping(target = "id", ignore = true)
    Location fromDto(LocationDto dto);
}
