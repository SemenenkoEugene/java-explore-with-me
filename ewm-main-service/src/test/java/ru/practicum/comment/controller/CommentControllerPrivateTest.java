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
import ru.practicum.comment.CommentNewDto;
import ru.practicum.comment.CommentUpdateRequest;
import ru.practicum.comment.service.CommentService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@WebMvcTest(controllers = CommentControllerPrivate.class)
class CommentControllerPrivateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @SneakyThrows
    @Test
    void create_allValid() {
        final CommentDto commentDto = getValidCommentDto();
        Mockito.when(commentService.create(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/comments/{eventId}", 1L, 1L)
                        .content(objectMapper.writeValueAsString(getValidCommentNewDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(commentService).create(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
        Mockito.verifyNoMoreInteractions(commentService);
    }

    @SneakyThrows
    @Test
    void patch_allValid() {
        final CommentDto commentDto = getValidCommentDto();
        Mockito.when(commentService.patchByUser(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}/comments/{commentId}", 1L, 1L)
                        .content(objectMapper.writeValueAsString(getValidCommentUpdateRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Mockito.verify(commentService).patchByUser(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
        Mockito.verifyNoMoreInteractions(commentService);
    }

    @SneakyThrows
    @Test
    void delete_allValid() {
        Mockito.doNothing().when(commentService).deleteByUser(Mockito.anyLong(), Mockito.anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}/comments/{commentId}", 1L, 1L))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(commentService).deleteByUser(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(commentService);
    }

    private CommentDto getValidCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("Updated comment content")
                .createdOn(LocalDateTime.now())
                .build();
    }

    private CommentNewDto getValidCommentNewDto() {
        return CommentNewDto.builder()
                .text("New comment content")
                .build();
    }

    private CommentUpdateRequest getValidCommentUpdateRequest() {
        return CommentUpdateRequest.builder()
                .text("Updated comment content")
                .build();
    }
}