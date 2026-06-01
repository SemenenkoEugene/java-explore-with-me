package dto;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonTest
@ContextConfiguration(classes = {EndpointHitDto.class})
public class EndpointHitDtoTest {

    @Autowired
    private JacksonTester<EndpointHitDto> endpointHitDtoJacksonTester;

    @SneakyThrows
    @Test
    void endpointHitDtoTest() {
        final LocalDateTime timestamp = LocalDateTime.now();

        final EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("TestApp")
                .uri("TestUri")
                .ip("0.0.0.0")
                .hitTimestamp(timestamp)
                .build();

        final JsonContent<EndpointHitDto> jsonContent = endpointHitDtoJacksonTester.write(endpointHitDto);

        final String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.app").isEqualTo("TestApp");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.uri").isEqualTo("TestUri");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.ip").isEqualTo("0.0.0.0");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.timestamp").isEqualTo(formattedTimestamp);
    }
}
