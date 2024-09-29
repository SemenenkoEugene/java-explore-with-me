package ru.practicum.event.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventControllerPublic.class)
class EventControllerPublicTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventServiceImpl eventService;

    @Test
    public void getAll_allValid() throws Exception {
        List<EventShortDto> events = Collections.singletonList(buildEvent());
        when(eventService.getAllPublic(
                anyString(), anyList(), anyBoolean(), any(), any(), anyBoolean(), any(), anyInt(), anyInt(), any(HttpServletRequest.class)
        )).thenReturn(events);

        mockMvc.perform(get("/events")
                        .param("paid", "true")
                        .contentType("application/json"))
                .andExpect(status().is2xxSuccessful());

        verify(eventService, times(1)).getAllPublic(
                any(), any(), any(), any(), any(), anyBoolean(), any(), anyInt(), anyInt(), any()
        );
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void getById_allValid() throws Exception {
        when(eventService.getByIdPublic(anyLong(), any(HttpServletRequest.class))).thenReturn(null);

        mockMvc.perform(get("/events/{eventId}", 0))
                .andExpect(status().is2xxSuccessful());

        verify(eventService, times(1)).getByIdPublic(anyLong(), any());
        verifyNoMoreInteractions(eventService);
    }

    private EventShortDto buildEvent() {
        return EventShortDto.builder()
                .id(1L)
                .paid(true)
                .build();
    }
}