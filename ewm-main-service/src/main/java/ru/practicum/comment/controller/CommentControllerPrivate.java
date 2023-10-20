package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.CommentDto;
import ru.practicum.comment.CommentNewDto;
import ru.practicum.comment.CommentUpdateRequest;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentControllerPrivate {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable long userId,
                             @PathVariable long eventId,
                             @Valid @RequestBody CommentNewDto commentNewDto) {
        return commentService.create(userId, eventId, commentNewDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto patch(@PathVariable long userId,
                            @PathVariable long commentId,
                            @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
        return commentService.patchByUser(userId, commentId, commentUpdateRequest);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId, @PathVariable long commentId) {
        commentService.deleteByUser(userId, commentId);
    }
}
