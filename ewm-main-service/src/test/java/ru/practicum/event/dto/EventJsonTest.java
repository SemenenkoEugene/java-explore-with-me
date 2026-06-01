package ru.practicum.event.dto;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.category.CategoryDto;
import ru.practicum.event.EventRequestStatusUpdateRequest;
import ru.practicum.event.EventState;
import ru.practicum.event.EventUpdateAdminRequest;
import ru.practicum.event.EventUpdateUserRequest;
import ru.practicum.location.LocationDto;
import ru.practicum.user.UserShortDto;
import ru.practicum.util.ConstantsDate;

import java.time.LocalDateTime;
import java.util.Arrays;

@JsonTest
class EventJsonTest {

    private final LocalDateTime eventDateTimestamp = LocalDateTime.now();
    private final LocalDateTime createdOnTimestamp = LocalDateTime.now().plusDays(1);
    private final LocalDateTime publishedOnTimestamp = LocalDateTime.now().plusDays(2);

    @Autowired
    private JacksonTester<EventFullDto> eventFullDtoJacksonTester;

    @Autowired
    private JacksonTester<EventNewDto> eventNewDtoJacksonTester;

    @Autowired
    private JacksonTester<EventRequestStatusUpdateRequest> eventRequestStatusUpdateRequestJacksonTester;

    @Autowired
    private JacksonTester<EventShortDto> eventShortDtoJacksonTester;

    @Autowired
    private JacksonTester<EventUpdateAdminRequest> eventUpdateAdminRequestJacksonTester;

    @Autowired
    private JacksonTester<EventUpdateUserRequest> eventUpdateUserRequestJacksonTester;

    @SneakyThrows
    @Test
    void eventFullDtoTest() {
        final EventFullDto eventFullDto = EventFullDto.builder()
                .id(1L)
                .initiator(getUserShortDto())
                .category(getCategoryDto())
                .location(getLocationDto())
                .title("TestTitle")
                .annotation("TestAnnotation")
                .description("TestDescr")
                .state(EventState.PENDING)
                .eventDate(eventDateTimestamp)
                .createdOn(createdOnTimestamp)
                .publishedOn(publishedOnTimestamp)
                .participantLimit(15)
                .paid(true)
                .requestModeration(false)
                .confirmedRequests(150L)
                .views(1500L)
                .build();

        final JsonContent<EventFullDto> jsonContent = eventFullDtoJacksonTester.write(eventFullDto);

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.initiator.id").isEqualTo(10);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.initiator.name").isEqualTo("TestUser");
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.category.id").isEqualTo(100);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.category.name").isEqualTo("TestCategory");
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.location.lat").isEqualTo(1000.0);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.location.lon").isEqualTo(10000.0);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualTo("TestTitle");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.annotation").isEqualTo("TestAnnotation");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("TestDescr");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.state").isEqualTo("PENDING");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.eventDate")
                .isEqualTo(eventDateTimestamp.format(ConstantsDate.getDefaultDateTimeFormatter()));
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.createdOn")
                .isEqualTo(createdOnTimestamp.format(ConstantsDate.getDefaultDateTimeFormatter()));
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.publishedOn")
                .isEqualTo(publishedOnTimestamp.format(ConstantsDate.getDefaultDateTimeFormatter()));
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(15);
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.paid").isEqualTo(true);
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.requestModeration").isEqualTo(false);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.confirmedRequests").isEqualTo(150);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.views").isEqualTo(1500);
    }

    @SneakyThrows
    @Test
    void eventNewDtoTest() {
        final EventNewDto eventNewDto = EventNewDto.builder()
                .category(1L)
                .location(getLocationDto())
                .title("TestTitle")
                .annotation("TestAnnotation")
                .description("TestDescr")
                .eventTimestamp(eventDateTimestamp)
                .participantLimit(15)
                .paid(true)
                .requestModeration(false)
                .build();

        final JsonContent<EventNewDto> jsonContent = eventNewDtoJacksonTester.write(eventNewDto);

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.category").isEqualTo(1);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.location.lat").isEqualTo(1000.0);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.location.lon").isEqualTo(10000.0);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualTo("TestTitle");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.annotation").isEqualTo("TestAnnotation");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("TestDescr");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.eventDate").isEqualTo(eventDateTimestamp.format(ConstantsDate.getDefaultDateTimeFormatter()));
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(15);
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.paid").isEqualTo(true);
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.requestModeration").isEqualTo(false);
    }

    @SneakyThrows
    @Test
    void eventRequestStatusUpdateRequestTest() {
        final EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(Arrays.asList(1L, 2L, 3L))
                .status(EventRequestStatusUpdateRequest.StateAction.CONFIRMED)
                .build();

        final JsonContent<EventRequestStatusUpdateRequest> jsonContent =
                eventRequestStatusUpdateRequestJacksonTester.write(eventRequestStatusUpdateRequest);

        Assertions.assertThat(jsonContent).extractingJsonPathArrayValue("$.requestIds").containsExactly(1, 2, 3);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.status").isEqualTo("CONFIRMED");
    }

    @SneakyThrows
    @Test
    void eventShortDtoTest() {
        final EventShortDto eventShortDto = EventShortDto.builder()
                .id(1L)
                .initiator(getUserShortDto())
                .category(getCategoryDto())
                .title("TestTitle")
                .annotation("TestAnnotation")
                .eventDate(LocalDateTime.now())
                .paid(true)
                .confirmedRequests(1000L)
                .views(10000L)
                .build();

        final JsonContent<EventShortDto> jsonContent = eventShortDtoJacksonTester.write(eventShortDto);

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.initiator.id").isEqualTo(10);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.initiator.name").isEqualTo("TestUser");
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.category.id").isEqualTo(100);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.category.name").isEqualTo("TestCategory");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualTo("TestTitle");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.annotation").isEqualTo("TestAnnotation");
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.paid").isEqualTo(true);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.confirmedRequests").isEqualTo(1000);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.views").isEqualTo(10000);
    }

    @SneakyThrows
    @Test
    void eventUpdateAdminRequestTest() {
        final EventUpdateAdminRequest eventUpdateAdminRequest = EventUpdateAdminRequest.builder()
                .category(1L)
                .location(getLocationDto())
                .title("TestTitle")
                .annotation("TestAnnotation")
                .description("TestDescr")
                .eventTimestamp(eventDateTimestamp)
                .participantLimit(15)
                .paid(true)
                .requestModeration(false)
                .stateAction(EventUpdateAdminRequest.StateAction.PUBLISH_EVENT)
                .build();

        final JsonContent<EventUpdateAdminRequest> jsonContent = eventUpdateAdminRequestJacksonTester.write(eventUpdateAdminRequest);

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.category").isEqualTo(1);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.location.lat").isEqualTo(1000.0);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.location.lon").isEqualTo(10000.0);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualTo("TestTitle");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.annotation").isEqualTo("TestAnnotation");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("TestDescr");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.eventDate").isEqualTo(eventDateTimestamp.format(ConstantsDate.getDefaultDateTimeFormatter()));
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(15);
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.paid").isEqualTo(true);
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.requestModeration").isEqualTo(false);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.stateAction").isEqualTo("PUBLISH_EVENT");
    }

    @SneakyThrows
    @Test
    void eventUpdateUserRequestTest() {
        final EventUpdateUserRequest eventUpdateUserRequest = EventUpdateUserRequest.builder()
                .category(1L)
                .location(getLocationDto())
                .title("TestTitle")
                .annotation("TestAnnotation")
                .description("TestDescr")
                .eventTimestamp(eventDateTimestamp)
                .participantLimit(15)
                .paid(true)
                .requestModeration(false)
                .stateAction(EventUpdateUserRequest.StateAction.SEND_TO_REVIEW)
                .build();

        final JsonContent<EventUpdateUserRequest> jsonContent = eventUpdateUserRequestJacksonTester.write(eventUpdateUserRequest);

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.category").isEqualTo(1);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.location.lat").isEqualTo(1000.0);
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.location.lon").isEqualTo(10000.0);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.title").isEqualTo("TestTitle");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.annotation").isEqualTo("TestAnnotation");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("TestDescr");
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.eventDate").isEqualTo(eventDateTimestamp.format(ConstantsDate.getDefaultDateTimeFormatter()));
        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(15);
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.paid").isEqualTo(true);
        Assertions.assertThat(jsonContent).extractingJsonPathBooleanValue("$.requestModeration").isEqualTo(false);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.stateAction").isEqualTo("SEND_TO_REVIEW");
    }

    private LocationDto getLocationDto() {
        return LocationDto.builder()
                .lat(1000f)
                .lon(10000f)
                .build();
    }

    private CategoryDto getCategoryDto() {
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