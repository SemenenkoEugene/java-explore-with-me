package ru.practicum.compilation.controller;

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
import ru.practicum.compilation.CompilationUpdateRequest;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.service.CompilationService;

import java.nio.charset.StandardCharsets;

@WebMvcTest(controllers = CompilationControllerAdmin.class)
class CompilationControllerAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CompilationService compilationService;

    @SneakyThrows
    @Test
    public void create_allValid() {
        Mockito.when(compilationService.create(Mockito.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/compilations")
                        .content(objectMapper.writeValueAsString(getValidCompilationNewDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(compilationService).create(Mockito.any());
        Mockito.verifyNoMoreInteractions(compilationService);

    }

    @SneakyThrows
    @Test
    public void patch_allValid() {
        Mockito.when(compilationService.patch(Mockito.anyLong(), Mockito.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/compilations/{compId}", 0)
                        .content(objectMapper.writeValueAsString(CompilationUpdateRequest.builder().build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(compilationService).patch(Mockito.anyLong(), Mockito.any());
        Mockito.verifyNoMoreInteractions(compilationService);
    }

    @SneakyThrows
    @Test
    public void delete_allValid() {
        Mockito.doNothing().when(compilationService).delete(Mockito.anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/compilations/{compId}", 0))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(compilationService).delete(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(compilationService);
    }

    private CompilationNewDto getValidCompilationNewDto() {
        return CompilationNewDto.builder()
                .title("Test title")
                .build();
    }
}