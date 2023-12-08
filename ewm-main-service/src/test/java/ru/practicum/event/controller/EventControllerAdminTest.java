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

@WebMvcTest(controllers = EventControllerAdmin.class)
class EventControllerAdminTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventServiceImpl eventService;

    @Test
    public void get_allValid() throws Exception {
        when(eventService.getAllByAdmin(
                any(), any(), any(), any(), any(), anyInt(), anyInt()
        )).thenReturn(null);

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().is2xxSuccessful());

        verify(eventService, times(1)).getAllByAdmin(
                any(), any(), any(), any(), any(), anyInt(), anyInt()
        );
        verifyNoMoreInteractions(eventService);
    }


}