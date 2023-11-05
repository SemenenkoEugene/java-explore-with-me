package dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ViewStatsDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = {ViewStatsDto.class})
public class ViewStatsDtoTest {

    @Autowired
    private JacksonTester<ViewStatsDto> viewStatsDtoJacksonTester;

    @Test
    void endpointHitDtoTest() throws IOException {
        ViewStatsDto viewStatsDto = ViewStatsDto.builder()
                .app("TestApp")
                .uri("TestUri")
                .hits(100L)
                .build();

        JsonContent<ViewStatsDto> jsonContent = viewStatsDtoJacksonTester.write(viewStatsDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.app").isEqualTo("TestApp");
        assertThat(jsonContent).extractingJsonPathStringValue("$.uri").isEqualTo("TestUri");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.hits").isEqualTo(100);
    }
}
