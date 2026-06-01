package ru.practicum.category;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = CategoryControllerPublic.class)
class CategoryControllerPublicTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @SneakyThrows
    @Test
    public void getAll_allValid() {
        Mockito.when(categoryService.getAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/categories"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(categoryService).getAll(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(categoryService);
    }

    @SneakyThrows
    @Test
    public void getById_allValid() {
        Mockito.when(categoryService.getById(Mockito.anyLong())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/categories/{catId}", 0))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(categoryService).getById(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(categoryService);
    }
}