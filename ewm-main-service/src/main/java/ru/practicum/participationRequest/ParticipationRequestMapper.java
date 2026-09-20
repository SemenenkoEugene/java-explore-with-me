package ru.practicum.participationRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(target = "requester", source = "participationRequest.requester.id")
    @Mapping(target = "event", source = "participationRequest.event.id")
    ParticipationRequestDto toDto(ParticipationRequest participationRequest);
}
