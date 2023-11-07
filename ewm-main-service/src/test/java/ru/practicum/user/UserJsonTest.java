package ru.practicum.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class UserJsonTest {

    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;

    @Autowired
    private JacksonTester<UserShortDto> userShortDtoJacksonTester;

    @Test
    void userDtoTest() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("TestName")
                .email("TestEmail")
                .build();

        JsonContent<UserDto> jsonContent = userDtoJacksonTester.write(userDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("TestName");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("TestEmail");
    }

}