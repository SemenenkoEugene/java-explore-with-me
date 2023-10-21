package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.CommentDto;
import ru.practicum.comment.CommentUpdateRequest;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
public class CommentControllerAdmin {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto patch(@PathVariable long commentId,
                            @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
        return commentService.patchByAdmin(commentId, commentUpdateRequest);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long commentId) {
        commentService.deleteByAdmin(commentId);
    }
}
