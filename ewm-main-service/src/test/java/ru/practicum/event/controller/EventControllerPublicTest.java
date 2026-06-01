package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import java.util.Collections;
import java.util.List;

@WebMvcTest(controllers = EventControllerPublic.class)
class EventControllerPublicTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @SneakyThrows
    @Test
    void getAll_allValid() {
        final List<EventShortDto> events = Collections.singletonList(buildEvent());
        Mockito.when(eventService.getAllPublic(
                Mockito.anyString(), Mockito.anyList(), Mockito.anyBoolean(), Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(HttpServletRequest.class)
        )).thenReturn(events);

        mockMvc.perform(MockMvcRequestBuilders.get("/events")
                        .param("paid", "true")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(eventService).getAllPublic(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any()
        );
        Mockito.verifyNoMoreInteractions(eventService);
    }

    @SneakyThrows
    @Test
    void getById_allValid() {
        Mockito.when(eventService.getByIdPublic(Mockito.anyLong(), Mockito.any(HttpServletRequest.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/events/{eventId}", 0))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(eventService).getByIdPublic(Mockito.anyLong(), Mockito.any());
        Mockito.verifyNoMoreInteractions(eventService);
    }

    private EventShortDto buildEvent() {
        return EventShortDto.builder()
                .id(1L)
                .paid(true)
                .build();
    }
}