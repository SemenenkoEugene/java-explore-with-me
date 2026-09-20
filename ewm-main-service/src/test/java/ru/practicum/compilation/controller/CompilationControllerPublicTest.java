package ru.practicum.compilation.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.compilation.service.CompilationService;

@WebMvcTest(controllers = CompilationControllerPublic.class)
class CompilationControllerPublicTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompilationService compilationService;

    @SneakyThrows
    @Test
    public void getAll_allValid() {
        Mockito.when(compilationService.getAll(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/compilations"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(compilationService).getAll(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(compilationService);
    }

    @SneakyThrows
    @Test
    public void getById_allValid() {
        Mockito.when(compilationService.getById(Mockito.anyLong())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/compilations/{compId}", 0))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(compilationService).getById(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(compilationService);
    }
}