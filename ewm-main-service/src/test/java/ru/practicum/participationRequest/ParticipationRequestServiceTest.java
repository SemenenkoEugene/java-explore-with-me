package ru.practicum.participationRequest;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.event.EventRepository;
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
}