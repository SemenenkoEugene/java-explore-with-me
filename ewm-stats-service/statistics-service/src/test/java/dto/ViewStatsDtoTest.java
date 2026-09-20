package dto;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ViewStatsDto;

@JsonTest
@ContextConfiguration(classes = {ViewStatsDto.class})
public class ViewStatsDtoTest {

    @Autowired
    private JacksonTester<ViewStatsDto> viewStatsDtoJacksonTester;

    @SneakyThrows
    @Test
    void endpointHitDtoTest() {
        final ViewStatsDto viewStatsDto = ViewStatsDto.builder()
                .app("TestApp")
                .uri("TestUri")
                .hits(100L)
                .build();

        final JsonContent<ViewStatsDto> jsonContent = viewStatsDtoJacksonTester.write(viewStatsDto);

        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.app").isEqualTo("TestApp");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.uri").isEqualTo("TestUri");
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.hits").isEqualTo(100);
    }
}
