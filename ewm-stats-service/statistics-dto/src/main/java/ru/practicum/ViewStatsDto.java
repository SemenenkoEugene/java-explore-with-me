package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewStatsDto {
    private String app; //Название сервиса
    private String uri; //URI сервиса
    private Long hits;  //Количество просмотров
}
