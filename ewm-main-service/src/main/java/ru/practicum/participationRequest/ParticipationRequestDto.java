package ru.practicum.participationRequest;

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
public class ParticipationRequestDto {

    private Long id;

    private Long requester;

    private Long event;

    private ParticipationRequestState status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantsDate.DATE_FORMAT)
    private LocalDateTime created;
}
