package ru.practicum.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "userName", source = "entity.user.name")
    CommentDto toDto(Comment entity);
}
