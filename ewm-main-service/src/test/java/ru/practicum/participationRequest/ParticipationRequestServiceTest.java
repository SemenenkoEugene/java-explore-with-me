package ru.practicum.participationRequest;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.category.Category;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.event.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.Location;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class ParticipationRequestServiceTest {

    private static final Long USER_ID = 1L;
    private static final LocalDateTime EVENT_DATE_TIMESTAMP = LocalDateTime.now();
    private static final LocalDateTime CREATED_ON_TIMESTAMP = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime PUBLISHED_ON_TIMESTAMP = LocalDateTime.now().plusDays(2);
    private static final ParticipationRequestDto PARTICIPATION_REQUEST_DTO = ParticipationRequestDto.builder().build();


    @Mock
    private ParticipationRequestRepository participationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ParticipationRequestMapper participationRequestMapper;

    @InjectMocks
    private ParticipationRequestServiceImpl participationRequestService;

    @BeforeEach
    void setUp() {
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(getEvent()));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(getParticipator()));
        Mockito.when(participationRequestRepository.findAllByRequesterId(Mockito.any())).thenReturn(List.of(getParticipationRequest()));
        Mockito.when(participationRequestRepository.findById(Mockito.any())).thenReturn(Optional.of(getParticipationRequest()));
        Mockito.when(participationRequestMapper.toDto(Mockito.any())).thenReturn(PARTICIPATION_REQUEST_DTO);

    }

    @Test
    void getAllTest() {

        final List<ParticipationRequestDto> actual = participationRequestService.getAll(USER_ID);

        Assertions.assertThat(actual).isEqualTo(List.of(PARTICIPATION_REQUEST_DTO));

        Mockito.verify(participationRequestRepository).findAllByRequesterId(Mockito.eq(USER_ID));
        Mockito.verifyNoMoreInteractions(userRepository, eventRepository, participationRequestRepository);
    }

    @Test
    void create_allValidTest() {
        Mockito.when(participationRequestRepository.save(Mockito.any())).thenReturn(new ParticipationRequest());

        final ParticipationRequestDto actual = participationRequestService.create(2, 1000);

        Assertions.assertThat(actual).isEqualTo(PARTICIPATION_REQUEST_DTO);

        Mockito.verify(userRepository).findById(Mockito.eq(2L));
        Mockito.verify(eventRepository).findById(Mockito.eq(1000L));
        Mockito.verify(participationRequestRepository).countByEventIdAndStatus(Mockito.eq(1000L), Mockito.eq(ParticipationRequestState.CONFIRMED));
        Mockito.verify(participationRequestRepository).save(Mockito.any(ParticipationRequest.class));
        Mockito.verifyNoMoreInteractions(userRepository, eventRepository, participationRequestRepository);
    }

    @Test
    void create_byInitiatorTest() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(getInitiator()));

        Assertions.assertThatThrownBy(() -> participationRequestService.create(USER_ID, 1000))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Event initiator cannot submit a participation request for own event");

        Mockito.verify(userRepository).findById(Mockito.eq(USER_ID));
        Mockito.verify(eventRepository).findById(Mockito.eq(1000L));
        Mockito.verifyNoMoreInteractions(userRepository, eventRepository, participationRequestRepository);
    }

    @Test
    void create_toNotPublishedEventTest() {
        final Event event = getEvent();
        event.setState(EventState.PENDING);

        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(event));

        Assertions.assertThatThrownBy(() -> participationRequestService.create(2, 1000))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Cannot participate in an unpublished event");

        Mockito.verify(userRepository).findById(Mockito.eq(2L));
        Mockito.verify(eventRepository).findById(Mockito.eq(1000L));
        Mockito.verifyNoMoreInteractions(userRepository, eventRepository, participationRequestRepository);
    }

    @Test
    void create_fullParticipationLimitTest() {
        Mockito.when(participationRequestRepository.countByEventIdAndStatus(Mockito.any(), Mockito.any())).thenReturn(100L);

        Assertions.assertThatThrownBy(() -> participationRequestService.create(2, 1000))
                .isInstanceOf(ConflictException.class)
                .hasMessage("The number of participation requests has exceeded the limit for the event");

        Mockito.verify(userRepository).findById(Mockito.eq(2L));
        Mockito.verify(eventRepository).findById(Mockito.eq(1000L));
        Mockito.verify(participationRequestRepository).countByEventIdAndStatus(Mockito.eq(1000L), Mockito.eq(ParticipationRequestState.CONFIRMED));
        Mockito.verifyNoMoreInteractions(userRepository, eventRepository, participationRequestRepository);
    }

    @Test
    void patch_allValidTest() {
        Mockito.when(participationRequestRepository.save(Mockito.any())).thenReturn(getParticipationRequest());

        final ParticipationRequestDto actual = participationRequestService.patch(2, 1000);

        Assertions.assertThat(actual).isEqualTo(PARTICIPATION_REQUEST_DTO);

        Mockito.verify(participationRequestRepository).findById(Mockito.eq(1000L));
        Mockito.verify(participationRequestRepository).save(Mockito.any(ParticipationRequest.class));
        Mockito.verifyNoMoreInteractions(userRepository, eventRepository, participationRequestRepository);
    }

    @Test
    void patch_notRequestOwnerTest() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(getInitiator()));

        Assertions.assertThatThrownBy(() -> participationRequestService.patch(USER_ID, 10000))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No events available for editing were found");

        Mockito.verify(participationRequestRepository).findById(Mockito.eq(10000L));
        Mockito.verifyNoMoreInteractions(userRepository, eventRepository, participationRequestRepository);
    }

    private User getInitiator() {
        final User user = new User();
        user.setId(1L);
        user.setName("Initiator");
        user.setEmail("Initiator@mail.com");
        return user;
    }

    private User getParticipator() {
        final User user = new User();
        user.setId(2L);
        user.setName("Participator");
        user.setEmail("Participator@mail.com");
        return user;
    }

    private Category getCategory() {
        final Category category = new Category();
        category.setId(10L);
        category.setName("TestCategory");
        return category;
    }

    private Location getLocation() {
        final Location location = new Location();
        location.setId(100L);
        location.setLat(100.1f);
        location.setLon(100.2f);
        return location;
    }

    private Event getEvent() {
        final Event event = new Event();
        event.setId(1000L);
        event.setInitiator(getInitiator());
        event.setCategory(getCategory());
        event.setLocation(getLocation());
        event.setTitle("TestTitle");
        event.setAnnotation("TestAnnotation");
        event.setDescription("TestDescr");
        event.setState(EventState.PUBLISHED);
        event.setEventDate(EVENT_DATE_TIMESTAMP);
        event.setCreatedOn(CREATED_ON_TIMESTAMP);
        event.setPublishedOn(PUBLISHED_ON_TIMESTAMP);
        event.setParticipantLimit(100);
        event.setPaid(true);
        event.setRequestModeration(true);
        return event;
    }

    private ParticipationRequest getParticipationRequest() {
        final ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setId(10000L);
        participationRequest.setEvent(getEvent());
        participationRequest.setRequester(getParticipator());
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(ParticipationRequestState.PENDING);
        return participationRequest;
    }
}