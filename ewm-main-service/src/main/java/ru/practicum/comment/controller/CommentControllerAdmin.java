package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.CommentDto;
import ru.practicum.comment.CommentUpdateRequest;
import ru.practicum.comment.service.CommentService;


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
