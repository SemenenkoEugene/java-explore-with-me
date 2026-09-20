package ru.practicum.controller;

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
import ru.practicum.EndpointHitDto;
import ru.practicum.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebMvcTest(controllers = StatsController.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StatsService statsService;

    @SneakyThrows
    @Test
    void getStats_allValid() {
        final String start = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        final String end = LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        final String uris = "Uri1, Uri2";
        final String unique = "false";

        Mockito.when(statsService.findStats(Mockito.any(), Mockito.any(), Mockito.anyList(), Mockito.anyBoolean())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/stats")
                        .param("start", start)
                        .param("end", end)
                        .param("uris", uris)
                        .param("unique", unique))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(statsService).findStats(Mockito.any(), Mockito.any(), Mockito.anyList(), Mockito.anyBoolean());
        Mockito.verifyNoMoreInteractions(statsService);
    }

    @SneakyThrows
    @Test
    void getStats_onlyRequiredValid() {
        final String start = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        final String end = LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Mockito.when(statsService.findStats(Mockito.any(), Mockito.any(), Mockito.anyList(), Mockito.anyBoolean())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/stats")
                        .param("start", start)
                        .param("end", end))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(statsService).findStats(LocalDateTime.now().plusDays(1).withNano(0),
                LocalDateTime.now().plusDays(2).withNano(0),
                null,
                false);
        Mockito.verifyNoMoreInteractions(statsService);
    }

    @SneakyThrows
    @Test
    void getStats_noRequiredParam() {
        final String start = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        mockMvc.perform(MockMvcRequestBuilders.get("/stats")
                        .param("start", start))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        Mockito.verifyNoMoreInteractions(statsService);
    }

    @SneakyThrows
    @Test
    void createEndpointHit_allValid() {
        final EndpointHitDto endpointHitDto = getValidEndpointHitDto();

        Mockito.doNothing().when(statsService).saveHit(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .content(objectMapper.writeValueAsString(endpointHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(statsService).saveHit(Mockito.any());
        Mockito.verifyNoMoreInteractions(statsService);
    }

    @SneakyThrows
    @Test
    void createEndpointHit_blankApp() {
        final EndpointHitDto endpointHitDto = getValidEndpointHitDto();
        endpointHitDto.setIp("     ");

        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .content(objectMapper.writeValueAsString(endpointHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        Mockito.verifyNoMoreInteractions(statsService);
    }

    @SneakyThrows
    @Test
    void createEndpointHit_blankUri() {
        final EndpointHitDto endpointHitDto = getValidEndpointHitDto();
        endpointHitDto.setUri("     ");

        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .content(objectMapper.writeValueAsString(endpointHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        Mockito.verifyNoMoreInteractions(statsService);
    }

    @SneakyThrows
    @Test
    void createEndpointHit_blankIp() {
        final EndpointHitDto endpointHitDto = getValidEndpointHitDto();
        endpointHitDto.setIp("     ");

        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .content(objectMapper.writeValueAsString(endpointHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        Mockito.verifyNoMoreInteractions(statsService);
    }

    @SneakyThrows
    @Test
    void createEndpointHit_nullTimestamp() {
        final EndpointHitDto endpointHitDto = getValidEndpointHitDto();
        endpointHitDto.setHitTimestamp(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .content(objectMapper.writeValueAsString(endpointHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        Mockito.verifyNoMoreInteractions(statsService);
    }

    private EndpointHitDto getValidEndpointHitDto() {
        return EndpointHitDto.builder()
                .app("TestApp")
                .uri("TestUri")
                .ip("0.0.0.0")
                .hitTimestamp(LocalDateTime.now())
                .build();
    }
}