package ru.practicum.participationRequest;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.category.Category;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.event.EventState;
import ru.practicum.location.Location;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipationRequestServiceTest {
    private final LocalDateTime eventDateTimestamp = LocalDateTime.now();
    private final LocalDateTime createdOnTimestamp = LocalDateTime.now().plusDays(1);
    private final LocalDateTime publishedOnTimestamp = LocalDateTime.now().plusDays(2);

    @InjectMocks
    private ParticipationRequestServiceImpl participationRequestService;

    @Mock
    private ParticipationRequestRepository participationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Test
    void getAllTest() {
        long userId = 1L;

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(new User()));
        when(participationRequestRepository.findAllByRequesterId(eq(userId))).thenReturn(new ArrayList<>());

        participationRequestService.getAll(userId);

        verify(userRepository, times(1)).findById(eq(userId));
        verify(participationRequestRepository, times(1)).findAllByRequesterId(eq(userId));
        verifyNoMoreInteractions(userRepository, eventRepository, participationRequestRepository);
    }

    @Test
    void create_allValidTest() {
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(getParticipator()));
        when(eventRepository.findById(eq(1000L))).thenReturn(Optional.of(getEvent()));
        when(participationRequestRepository.countByEventIdAndStatus(eq(1000L), eq(ParticipationRequestState.CONFIRMED))).thenReturn(90L);
        when(participationRequestRepository.save(any(ParticipationRequest.class))).thenReturn(new ParticipationRequest());

        participationRequestService.create(2, 1000);

        verify(userRepository, times(1)).findById(eq(2L));
        verify(eventRepository, times(1)).findById(eq(1000L));
        verify(participationRequestRepository, times(1)).countByEventIdAndStatus(eq(1000L), eq(ParticipationRequestState.CONFIRMED));
        verify(participationRequestRepository, times(1)).save(any(ParticipationRequest.class));
        verifyNoMoreInteractions(userRepository, eventRepository, participationRequestRepository);
    }

    private User getInitiator() {
        User user = new User();
        user.setId(1L);
        user.setName("Initiator");
        user.setEmail("Initiator@mail.com");
        return user;
    }

    private User getParticipator() {
        User user = new User();
        user.setId(2L);
        user.setName("Participator");
        user.setEmail("Participator@mail.com");
        return user;
    }

    private Category getCategory() {
        Category category = new Category();
        category.setId(10L);
        category.setName("TestCategory");
        return category;
    }

    private Location getLocation() {
        Location location = new Location();
        location.setId(100L);
        location.setLat(100.1f);
        location.setLon(100.2f);
        return location;
    }

    private Event getEvent() {
        Event event = new Event();
        event.setId(1000L);
        event.setInitiator(getInitiator());
        event.setCategory(getCategory());
        event.setLocation(getLocation());
        event.setTitle("TestTitle");
        event.setAnnotation("TestAnnotation");
        event.setDescription("TestDescr");
        event.setState(EventState.PUBLISHED);
        event.setEventDate(eventDateTimestamp);
        event.setCreatedOn(createdOnTimestamp);
        event.setPublishedOn(publishedOnTimestamp);
        event.setParticipantLimit(100);
        event.setPaid(true);
        event.setRequestModeration(true);
        return event;
    }

    private ParticipationRequest getParticipationRequest() {
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setId(10000L);
        participationRequest.setEvent(getEvent());
        participationRequest.setRequester(getParticipator());
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(ParticipationRequestState.PENDING);
        return participationRequest;
    }
}