package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.*;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public List<CommentDto> getAllByEventId(final long eventId) {
        final List<Comment> commentList = commentRepository.findAllByEventId(eventId);

        return Optional.ofNullable(commentList).orElseGet(List::of).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public CommentDto create(final long userId, final long eventId, final CommentNewDto dto) {
        final User user = findUserById(userId);
        final Event event = findEventById(eventId);

        final Comment comment = makeComment(user, event, dto);

        final Comment saved = commentRepository.save(comment);

        return commentMapper.toDto(saved);
    }


    @Transactional
        public CommentDto patchByUser(final long userId, final long commentId, final CommentUpdateRequest updateRequest) {
        final User user = findUserById(userId);
        final Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("User id=%d not owner of Comment id=%d".formatted(user.getId(), comment.getId()));
        }

        if (Objects.nonNull(updateRequest.getText())) {
            comment.setText(updateRequest.getText());
        }

        return commentMapper.toDto(comment);
    }

    @Transactional
    public CommentDto patchByAdmin(final long commentId, final CommentUpdateRequest updateRequest) {
        final Comment comment = findCommentById(commentId);

        if (Objects.nonNull(updateRequest.getText())) {
            comment.setText(updateRequest.getText());
        }

        return commentMapper.toDto(comment);
    }

    @Transactional
    public void deleteByUser(final long userId, final long commentId) {
        final User user = findUserById(userId);
        final Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("User id=%d not owner of Comment id=%d".formatted(user.getId(), comment.getId()));
        }

        commentRepository.deleteById(comment.getId());
    }

    @Transactional
    public void deleteByAdmin(final long commentId) {
        commentRepository.deleteById(commentId);
    }

    private Comment makeComment(final User user, final Event event, final CommentNewDto dto) {
        final Comment comment = new Comment();
        comment.setUser(user);
        comment.setEvent(event);
        comment.setText(dto.getText());
        comment.setCreatedOn(getNow());

        return comment;
    }

    private Comment findCommentById(final long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment with id=%d was not found".formatted(id)));
    }

    private User findUserById(final long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=%d was not found".formatted(id)));
    }

    private Event findEventById(final long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=%d was not found".formatted(id)));
    }

    LocalDateTime getNow() {
        return LocalDateTime.now();
    }
}
