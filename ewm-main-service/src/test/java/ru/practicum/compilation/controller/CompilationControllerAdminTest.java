package ru.practicum.compilation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.service.CompilationServiceImpl;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompilationControllerAdmin.class)
class CompilationControllerAdminTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompilationServiceImpl compilationService;

    @Test
    public void create_allValid() throws Exception {
        when(compilationService.create(any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .content(objectMapper.writeValueAsString(getValidCompilationNewDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        verify(compilationService, times(1)).create(any());
        verifyNoMoreInteractions(compilationService);

    }

    private CompilationNewDto getValidCompilationNewDto() {
        return CompilationNewDto.builder()
                .title("Test title")
                .build();
    }
}