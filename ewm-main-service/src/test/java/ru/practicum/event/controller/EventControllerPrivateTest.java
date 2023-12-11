package ru.practicum.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.event.service.EventServiceImpl;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}