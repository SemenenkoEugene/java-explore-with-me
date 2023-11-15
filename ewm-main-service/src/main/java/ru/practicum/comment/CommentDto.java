package ru.practicum.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.util.ConstantsDate;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private String userName;
    private String text;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantsDate.DATE_FORMAT)
    private LocalDateTime createdOn;
}
