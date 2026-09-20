package ru.practicum.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.event.EventRequestStatusUpdateRequest;
import ru.practicum.event.EventUpdateUserRequest;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.service.EventService;
import ru.practicum.location.LocationDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

@WebMvcTest(controllers = EventControllerPrivate.class)
class EventControllerPrivateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @SneakyThrows
    @Test
    void getAll_allValid() {
        Mockito.when(eventService.getAllByInitiator(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/events", 0))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(eventService).getAllByInitiator(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(eventService);
    }

    @SneakyThrows
    @Test
    void getById_allValid() {
        Mockito.when(eventService.getByIdByInitiator(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/events/{eventId}", 0, 0))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(eventService).getByIdByInitiator(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(eventService);
    }

    @SneakyThrows
    @Test
    void getParticipationRequestsByInitiator_allValid() {
        Mockito.when(eventService.getParticipationRequestsByInitiator(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/events/{eventId}/requests", 0, 0))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(eventService).getParticipationRequestsByInitiator(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(eventService);
    }

    @SneakyThrows
    @Test
    void create_allValid() {
        Mockito.when(eventService.create(Mockito.anyLong(), Mockito.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/events", 0)
                        .content(objectMapper.writeValueAsString(getValidEventNewDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(eventService).create(Mockito.anyLong(), Mockito.any());
        Mockito.verifyNoMoreInteractions(eventService);
    }

    @SneakyThrows
    @Test
    void patchEventInfo_allValid() {
        Mockito.when(eventService.patchByInitiator(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}/events/{eventId}", 0, 0)
                        .content(objectMapper.writeValueAsString(EventUpdateUserRequest.builder().build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(eventService).patchByInitiator(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
        Mockito.verifyNoMoreInteractions(eventService);
    }

    @SneakyThrows
    @Test
    void patchEventRequests_allValid() {
        Mockito.when(eventService.patchParticipationRequestsByInitiator(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}/events/{eventId}/requests", 0, 0)
                        .content(objectMapper.writeValueAsString(getValidEventRequestStatusUpdateRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(eventService).patchParticipationRequestsByInitiator(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
        Mockito.verifyNoMoreInteractions(eventService);
    }

    private EventNewDto getValidEventNewDto() {
        return EventNewDto.builder()
                .category(0L)
                .location(getValidLocationDto())
                .title("Test title")
                .annotation("Test annotation with 20 min symbols")
                .description("Test description with 20 min symbols")
                .eventTimestamp(LocalDateTime.now().plusDays(1))
                .build();
    }

    private LocationDto getValidLocationDto() {
        return LocationDto.builder()
                .lat(0f)
                .lon(0f)
                .build();
    }

    private EventRequestStatusUpdateRequest getValidEventRequestStatusUpdateRequest() {
        return EventRequestStatusUpdateRequest.builder()
                .requestIds(new ArrayList<>())
                .status(EventRequestStatusUpdateRequest.StateAction.CONFIRMED)
                .build();
    }
}