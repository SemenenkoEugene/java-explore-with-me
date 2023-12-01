package ru.practicum.compilation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.compilation.service.CompilationServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompilationControllerPublic.class)
class CompilationControllerPublicTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompilationServiceImpl compilationService;

    @Test
    public void getAll_allValid() throws Exception {
        when(compilationService.getAll(any(), anyInt(), anyInt())).thenReturn(null);

        mockMvc.perform(get("/compilations"))
                .andExpect(status().is2xxSuccessful());

        verify(compilationService, times(1)).getAll(any(), anyInt(), anyInt());
        verifyNoMoreInteractions(compilationService);
    }
}