package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.category.CategoryDto;
import ru.practicum.user.UserShortDto;
import ru.practicum.util.ConstantsDate;

import java.time.LocalDateTime;

@Data
@Builder
public class EventShortDto {

    private Long id;

    private UserShortDto initiator;
    private CategoryDto category;

    private String title;
    private String annotation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantsDate.DATE_FORMAT)
    private LocalDateTime eventDate;

    private Boolean paid;

    private Long confirmedRequests;
    private Long views;
}
