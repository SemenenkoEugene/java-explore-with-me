package ru.practicum.compilation.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.category.CategoryDto;
import ru.practicum.compilation.CompilationUpdateRequest;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.UserShortDto;
import ru.practicum.util.ConstantsDate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CompilationJsonTest {
    private final LocalDateTime testTimestamp = LocalDateTime.now();

    @Autowired
    private JacksonTester<CompilationDto> compilationDtoJacksonTester;

    @Autowired
    private JacksonTester<CompilationNewDto> compilationNewDtoJacksonTester;

    @Autowired
    private JacksonTester<CompilationUpdateRequest> compilationUpdateRequestJacksonTester;

    @Test
    void compilationDtoTest() throws IOException {
        CompilationDto compilationDto = CompilationDto.builder()
                .id(1L)
                .title("TestCompilation")
                .pinned(true)
                .events(getEvents())
                .build();

        JsonContent<CompilationDto> jsonContent = compilationDtoJacksonTester.write(compilationDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualTo("TestCompilation");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.pinned").isEqualTo(true);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.events[0].id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.events[0].title").isEqualTo("TestEvent");
        assertThat(jsonContent).extractingJsonPathStringValue("$.events[0].annotation").isEqualTo("TestAnnotation");
        assertThat(jsonContent).extractingJsonPathStringValue("$.events[0].eventDate").isEqualTo(testTimestamp.format(ConstantsDate.getDefaultDateTimeFormatter()));
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.events[0].paid").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.events[0].confirmedRequests").isEqualTo(1000);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.events[0].views").isEqualTo(10000);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.events[0].initiator.id").isEqualTo(10);
        assertThat(jsonContent).extractingJsonPathStringValue("$.events[0].initiator.name").isEqualTo("TestUser");

        assertThat(jsonContent).extractingJsonPathNumberValue("$.events[0].category.id").isEqualTo(100);
        assertThat(jsonContent).extractingJsonPathStringValue("$.events[0].category.name").isEqualTo("TestCategory");
    }

    @Test
    void compilationNewDtoTest() throws IOException {
        CompilationNewDto compilationNewDto = CompilationNewDto.builder()
                .title("TestCompilationNew")
                .pinned(true)
                .events(Arrays.asList(1L, 2L, 3L))
                .build();

        JsonContent<CompilationNewDto> jsonContent = compilationNewDtoJacksonTester.write(compilationNewDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualTo("TestCompilationNew");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.pinned").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathArrayValue("$.events").containsExactly(1, 2, 3);
    }

    private List<EventShortDto> getEvents() {
        return Collections.singletonList(
                EventShortDto.builder()
                        .id(1L)
                        .initiator(getUserShortDto())
                        .category(getCategory())
                        .title("TestEvent")
                        .annotation("TestAnnotation")
                        .eventDate(testTimestamp)
                        .paid(true)
                        .confirmedRequests(1000L)
                        .views(10000L)
                        .build()
        );
    }

    private CategoryDto getCategory() {
        return CategoryDto.builder()
                .id(100L)
                .name("TestCategory")
                .build();
    }

    private UserShortDto getUserShortDto() {
        return UserShortDto.builder()
                .id(10L)
                .name("TestUser")
                .build();
    }
}