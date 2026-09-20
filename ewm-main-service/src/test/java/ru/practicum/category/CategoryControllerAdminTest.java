package ru.practicum.category;

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

import java.nio.charset.StandardCharsets;


@WebMvcTest(controllers = CategoryControllerAdmin.class)
class CategoryControllerAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @SneakyThrows
    @Test
    void create_allValid() {
        Mockito.when(categoryService.create(Mockito.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/categories")
                        .content(objectMapper.writeValueAsString(getValidCategoryDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(categoryService).create(Mockito.any());
        Mockito.verifyNoMoreInteractions(categoryService);
    }

    @SneakyThrows
    @Test
    void patch_allValid() {
        Mockito.when(categoryService.patch(Mockito.anyLong(), Mockito.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/categories/{catId}", 0)
                        .content(objectMapper.writeValueAsString(getValidCategoryDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(categoryService).patch(Mockito.anyLong(), Mockito.any());
        Mockito.verifyNoMoreInteractions(categoryService);
    }

    @SneakyThrows
    @Test
    void delete_allValid() {
        Mockito.doNothing().when(categoryService).delete(Mockito.anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/categories/{catId}", 0))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        Mockito.verify(categoryService).delete(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(categoryService);
    }

    private CategoryDto getValidCategoryDto() {
        return CategoryDto.builder()
                .name("Test category")
                .build();
    }
}