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
import ru.practicum.event.EventUpdateAdminRequest;
import ru.practicum.event.service.EventService;

import java.nio.charset.StandardCharsets;

@WebMvcTest(controllers = EventControllerAdmin.class)
class EventControllerAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @SneakyThrows
    @Test
    void get_allValid() {
        Mockito.when(eventService.getAllByAdmin(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()
        )).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/events"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(eventService).getAllByAdmin(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()
        );
        Mockito.verifyNoMoreInteractions(eventService);
    }

    @SneakyThrows
    @Test
    void patch_allValid() {
        Mockito.when(eventService.patchByAdmin(Mockito.anyLong(), Mockito.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/events/{eventId}", 0)
                        .content(objectMapper.writeValueAsString(EventUpdateAdminRequest.builder().build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(eventService).patchByAdmin(Mockito.anyLong(), Mockito.any());
        Mockito.verifyNoMoreInteractions(eventService);
    }
}