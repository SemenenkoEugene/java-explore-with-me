package ru.practicum.category;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category fromDto(CategoryDto categoryDto);
}
