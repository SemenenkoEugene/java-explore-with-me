package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.CommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentControllerPublic {

    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentDto> getAllByEventId(@PathVariable long eventId) {
        return commentService.getAllByEventId(eventId);
    }
}
