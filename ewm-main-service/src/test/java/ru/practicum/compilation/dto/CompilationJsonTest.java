package ru.practicum.compilation.dto;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JsonTest
class CompilationJsonTest {
    private final LocalDateTime testTimestamp = LocalDateTime.now();

    @Autowired
    private JacksonTester<CompilationDto> compilationDtoJacksonTester;

    @Autowired
    private JacksonTester<CompilationNewDto> compilationNewDtoJacksonTester;

    @Autowired
    private JacksonTester<CompilationUpdateRequest> compilationUpdateRequestJacksonTester;

    @SneakyThrows
    @Test
    void compilationDtoTest() {
        final CompilationDto compilationDto = CompilationDto.builder()
                .id(1L)
                .title("TestCompilation")
                .pinned(true)
                .events(getEvents())
                .build();

        final JsonContent<CompilationDto> jsonContent = compilationDtoJacksonTester.write(compilationDto);

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualTo("TestCompilation");
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.pinned").isEqualTo(true);

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.events[0].id").isEqualTo(1);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.events[0].title").isEqualTo("TestEvent");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.events[0].annotation").isEqualTo("TestAnnotation");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.events[0].eventDate").isEqualTo(testTimestamp.format(ConstantsDate.getDefaultDateTimeFormatter()));
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.events[0].paid").isEqualTo(true);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.events[0].confirmedRequests").isEqualTo(1000);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.events[0].views").isEqualTo(10000);

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.events[0].initiator.id").isEqualTo(10);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.events[0].initiator.name").isEqualTo("TestUser");

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.events[0].category.id").isEqualTo(100);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.events[0].category.name").isEqualTo("TestCategory");
    }

    @SneakyThrows
    @Test
    void compilationNewDtoTest() {
        final CompilationNewDto compilationNewDto = CompilationNewDto.builder()
                .title("TestCompilationNew")
                .pinned(true)
                .events(Arrays.asList(1L, 2L, 3L))
                .build();

        final JsonContent<CompilationNewDto> jsonContent = compilationNewDtoJacksonTester.write(compilationNewDto);

        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualTo("TestCompilationNew");
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.pinned").isEqualTo(true);
        Assertions.assertThat(jsonContent).extractingJsonPathArrayValue("$.events").containsExactly(1, 2, 3);
    }

    @SneakyThrows
    @Test
    void compilationUpdateRequestTest() {
        final CompilationUpdateRequest compilationUpdateRequest = CompilationUpdateRequest.builder()
                .title("TestCompilationUpdate")
                .pinned(false)
                .events(Arrays.asList(4L, 5L, 6L))
                .build();

        final JsonContent<CompilationUpdateRequest> jsonContent = compilationUpdateRequestJacksonTester.write(compilationUpdateRequest);

        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualTo("TestCompilationUpdate");
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.pinned").isEqualTo(false);
        Assertions.assertThat(jsonContent).extractingJsonPathArrayValue("$.events").containsExactly(4, 5, 6);
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