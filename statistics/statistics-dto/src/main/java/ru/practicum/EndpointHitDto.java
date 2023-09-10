package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {

    private Long id;
    @NotBlank
    private String app; //Идентификатор сервиса для которого записывается информация
    @NotBlank
    @NotEmpty
    private String uri; //URI для которого был осуществлен запрос
    @NotBlank
    private String ip;  //IP-адрес пользователя, осуществившего запрос
    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp; //Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
}
