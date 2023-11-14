package ru.practicum.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryControllerPublic.class)
class CategoryControllerPublicTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryServiceImpl categoryService;

    @Test
    public void getAll_getValid() throws Exception {
        when(categoryService.getAll(anyInt(), anyInt())).thenReturn(null);

        mockMvc.perform(get("/categories"))
                .andExpect(status().is2xxSuccessful());

        verify(categoryService, times(1)).getAll(anyInt(), anyInt());
        verifyNoMoreInteractions(categoryService);
    }
}