package ru.practicum.event.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.event.service.EventServiceImpl;

import static org.mockito.Mockito.*;
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
        when(eventService.getAllPublic(
                any(), any(), any(), any(), any(), anyBoolean(), any(), anyInt(), anyInt(), any()
        )).thenReturn(null);

        mockMvc.perform(get("/events"))
                .andExpect(status().is2xxSuccessful());

        verify(eventService, times(1)).getAllPublic(
                any(), any(), any(), any(), any(), anyBoolean(), any(), anyInt(), anyInt(), any()
        );
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void getById_allValid() throws Exception {
        when(eventService.getByIdPublic(anyLong(), any())).thenReturn(null);

        mockMvc.perform(get("/events/{eventId}", 0))
                .andExpect(status().is2xxSuccessful());

        verify(eventService, times(1)).getByIdPublic(anyLong(), any());
        verifyNoMoreInteractions(eventService);
    }
}