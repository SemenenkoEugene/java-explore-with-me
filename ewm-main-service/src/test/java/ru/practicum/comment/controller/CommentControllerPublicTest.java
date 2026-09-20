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
import ru.practicum.comment.service.CommentService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(controllers = CommentControllerPublic.class)
class CommentControllerPublicTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @SneakyThrows
    @Test
    void getAllByEventId_allValid() {
        final List<CommentDto> commentDtos = List.of(getValidCommentDto());
        Mockito.when(commentService.getAllByEventId(Mockito.anyLong())).thenReturn(commentDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/comments/{eventId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(commentDtos)));

        Mockito.verify(commentService).getAllByEventId(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(commentService);
    }

    private CommentDto getValidCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("Test comment content")
                .createdOn(LocalDateTime.now())
                .build();
    }
}