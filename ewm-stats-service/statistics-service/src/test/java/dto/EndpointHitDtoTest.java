package dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.EndpointHitDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = {EndpointHitDto.class})
public class EndpointHitDtoTest {

    @Autowired
    private JacksonTester<EndpointHitDto> endpointHitDtoJacksonTester;

    @Test
    void endpointHitDtoTest() throws IOException {
        LocalDateTime timestamp = LocalDateTime.now();

        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("TestApp")
                .uri("TestUri")
                .ip("0.0.0.0")
                .hitTimestamp(timestamp)
                .build();

        JsonContent<EndpointHitDto> jsonContent = endpointHitDtoJacksonTester.write(endpointHitDto);

        String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        assertThat(jsonContent).extractingJsonPathStringValue("$.app").isEqualTo("TestApp");
        assertThat(jsonContent).extractingJsonPathStringValue("$.uri").isEqualTo("TestUri");
        assertThat(jsonContent).extractingJsonPathStringValue("$.ip").isEqualTo("0.0.0.0");
        assertThat(jsonContent).extractingJsonPathStringValue("$.timestamp").isEqualTo(formattedTimestamp);
    }
}
