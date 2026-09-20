package ru.practicum.participationRequest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

@JsonTest
class ParticipationRequestJsonTest {

    @Autowired
    private JacksonTester<ParticipationRequestDto> participationRequestDtoJacksonTester;

    @Test
    void participationRequestDtoTest() throws IOException {
        final ParticipationRequestDto participationRequestDto = ParticipationRequestDto.builder()
                .id(1L)
                .requester(10L)
                .event(100L)
                .status(ParticipationRequestState.CONFIRMED)
                .build();

        final JsonContent<ParticipationRequestDto> jsonContent = participationRequestDtoJacksonTester.write(participationRequestDto);

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.requester").isEqualTo(10);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.event").isEqualTo(100);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.status").isEqualTo("CONFIRMED");
    }
}