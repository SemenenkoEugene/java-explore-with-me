package ru.practicum.comment.service;

import ru.practicum.comment.CommentDto;
import ru.practicum.comment.CommentNewDto;
import ru.practicum.comment.CommentUpdateRequest;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllByEventId(long eventId);

    CommentDto create(long userId, long eventId, CommentNewDto dto);

    CommentDto patchByUser(long userId, long commentId, CommentUpdateRequest updateRequest);

    CommentDto patchByAdmin(long commentId, CommentUpdateRequest updateRequest);

    void deleteByUser(long userId, long commentId);

    void deleteByAdmin(long commentId);
}
