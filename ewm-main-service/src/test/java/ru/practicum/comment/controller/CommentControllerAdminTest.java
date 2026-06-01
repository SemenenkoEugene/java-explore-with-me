package ru.practicum.comment.controller;

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
import ru.practicum.comment.CommentDto;
import ru.practicum.comment.CommentUpdateRequest;
import ru.practicum.comment.service.CommentService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@WebMvcTest(controllers = CommentControllerAdmin.class)
class CommentControllerAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @SneakyThrows
    @Test
    void patch_allValid() {
        final CommentDto commentDto = getValidCommentDto();
        Mockito.when(commentService.patchByAdmin(Mockito.anyLong(), Mockito.any())).thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/comments/{commentId}", 1L)
                        .content(objectMapper.writeValueAsString(getValidCommentUpdateRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(commentService).patchByAdmin(Mockito.anyLong(), Mockito.any());
        Mockito.verifyNoMoreInteractions(commentService);
    }

    @SneakyThrows
    @Test
    void delete_allValid() {
        Mockito.doNothing().when(commentService).deleteByAdmin(Mockito.anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/comments/{commentId}", 1L))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(commentService).deleteByAdmin(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(commentService);
    }

    private CommentDto getValidCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("Updated comment content")
                .createdOn(LocalDateTime.now())
                .build();
    }

    private CommentUpdateRequest getValidCommentUpdateRequest() {
        return CommentUpdateRequest.builder()
                .text("Updated comment content")
                .build();
    }
}