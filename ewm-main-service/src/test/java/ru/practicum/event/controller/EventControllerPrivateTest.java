package ru.practicum.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.service.EventServiceImpl;
import ru.practicum.location.LocationDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventControllerPrivate.class)
class EventControllerPrivateTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventServiceImpl eventService;

    @Test
    public void getAll_allValid() throws Exception {
        when(eventService.getAllByInitiator(anyLong(), anyInt(), anyInt())).thenReturn(null);

        mockMvc.perform(get("/users/{userId}/events", 0))
                .andExpect(status().is2xxSuccessful());

        verify(eventService, times(1)).getAllByInitiator(anyLong(), anyInt(), anyInt());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void getById_allValid() throws Exception {
        when(eventService.getByIdByInitiator(anyLong(), anyLong())).thenReturn(null);

        mockMvc.perform(get("/users/{userId}/events/{eventId}", 0, 0))
                .andExpect(status().is2xxSuccessful());

        verify(eventService, times(1)).getByIdByInitiator(anyLong(), anyLong());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void getParticipationRequestsByInitiator_allValid() throws Exception {
        when(eventService.getParticipationRequestsByInitiator(anyLong(), anyLong())).thenReturn(null);

        mockMvc.perform(get("/users/{userId}/events/{eventId}/requests", 0, 0))
                .andExpect(status().is2xxSuccessful());

        verify(eventService, times(1)).getParticipationRequestsByInitiator(anyLong(), anyLong());
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void create_allValid() throws Exception {
        when(eventService.create(anyLong(), any())).thenReturn(null);

        mockMvc.perform(post("/users/{userId}/events", 0)
                        .content(objectMapper.writeValueAsString(getValidEventNewDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        verify(eventService, times(1)).create(anyLong(), any());
        verifyNoMoreInteractions(eventService);
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
}