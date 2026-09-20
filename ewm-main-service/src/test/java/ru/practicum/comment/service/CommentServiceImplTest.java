package ru.practicum.comment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.comment.*;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
class CommentServiceImplTest {

    private static final Long COMMENT_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long EVENT_ID = 3L;

    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final CommentDto COMMENT_DTO = CommentDto.builder()
            .id(COMMENT_ID)
            .text("someText")
            .build();
    private static final CommentNewDto COMMENT_NEW_DTO = CommentNewDto.builder().build();
    private static final CommentUpdateRequest COMMENT_UPDATE_REQUEST = CommentUpdateRequest.builder()
            .text("someText")
            .build();

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private CommentMapper commentMapper;

    @Spy
    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        Mockito.when(commentRepository.findAllByEventId(Mockito.anyLong())).thenReturn(List.of(buildComment()));
        Mockito.when(commentRepository.findById(Mockito.any())).thenReturn(Optional.of(buildComment()));
        Mockito.when(commentMapper.toDto(Mockito.any())).thenReturn(COMMENT_DTO);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(buildUser()));
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(buildEvent()));
    }

    @Test
    void getAllByEventId_happyPath() {
        final List<CommentDto> actual = commentService.getAllByEventId(EVENT_ID);

        Assertions.assertThat(actual).isEqualTo(List.of(COMMENT_DTO));
        Mockito.verify(commentRepository).findAllByEventId(EVENT_ID);
        Mockito.verify(commentMapper).toDto(Mockito.any());
    }

    @Test
    void getAllByEventId_commentListIsEmpty_returnEmpty() {
        Mockito.when(commentRepository.findAllByEventId(Mockito.anyLong())).thenReturn(List.of());

        final List<CommentDto> actual = commentService.getAllByEventId(EVENT_ID);

        Assertions.assertThat(actual).isEqualTo(List.of());
        Mockito.verify(commentRepository).findAllByEventId(EVENT_ID);
        Mockito.verify(commentMapper, Mockito.never()).toDto(Mockito.any());

    }

    @Test
    void create_userNotFound_returnException() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> commentService.create(USER_ID, EVENT_ID, COMMENT_NEW_DTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id=2 was not found");
    }

    @Test
    void create_eventNotFound_returnException() {
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> commentService.create(USER_ID, EVENT_ID, COMMENT_NEW_DTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Event with id=3 was not found");
    }

    private static Stream<Arguments> forSaveTestingData() {
        return Stream.of(
                Arguments.of("user.id", buildUser(), buildEvent(), COMMENT_NEW_DTO,
                        new FuncRef<Comment>(c -> c.getUser().getId()), USER_ID),
                Arguments.of("event.id", buildUser(), buildEvent(), COMMENT_NEW_DTO,
                        new FuncRef<Comment>(c -> c.getEvent().getId()), EVENT_ID),
                Arguments.of("text", buildUser(), buildEvent(), COMMENT_NEW_DTO.toBuilder().text("someText").build(),
                        new FuncRef<>(Comment::getText), "someText"),
                Arguments.of("createdOn", buildUser(), buildEvent(), COMMENT_NEW_DTO,
                        new FuncRef<>(Comment::getCreatedOn), NOW)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("forSaveTestingData")
    void create_specificDtoField_savesEntityCorrespondingField(final String testCaseId, final User user, final Event event,
                                                               final CommentNewDto commentNewDto, final FuncRef<Comment> testable,
                                                               final Object expectedValue) {
        final Comment savedComment = buildComment();
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(savedComment);
        Mockito.when(commentService.getNow()).thenReturn(NOW);
        final ArgumentCaptor<Comment> forSavingEntityArgumentCaptor = ArgumentCaptor.forClass(Comment.class);

        final CommentDto actualDto = commentService.create(user.getId(), event.getId(), commentNewDto);

        Mockito.verify(commentRepository).save(forSavingEntityArgumentCaptor.capture());
        final Comment forSavingComment = forSavingEntityArgumentCaptor.getValue();
        Assertions.assertThat(testable.lambda.apply(forSavingComment)).isEqualTo(expectedValue);
        Mockito.verify(commentMapper).toDto(savedComment);
        Assertions.assertThat(actualDto).isEqualTo(COMMENT_DTO);
    }

    @Test
    void patchByUser_userNotFound_returnException() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> commentService.patchByUser(USER_ID, COMMENT_ID, COMMENT_UPDATE_REQUEST))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id=2 was not found");
    }

    @Test
    void patchByUser_commentNotFound_returnException() {
        Mockito.when(commentRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> commentService.patchByUser(USER_ID, COMMENT_ID, COMMENT_UPDATE_REQUEST))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Comment with id=1 was not found");
    }

    @Test
    void patchByUser_commentUserIsNotEqualUser_returnException() {
        final User user = buildUser();
        user.setId(100L);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        Assertions.assertThatThrownBy(() -> commentService.patchByUser(100L, COMMENT_ID, COMMENT_UPDATE_REQUEST))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("User id=100 not owner of Comment id=1");

    }

    @Test
    void patchByUser_happyPath_always() {
        final Comment comment = buildComment();
        comment.setText(COMMENT_UPDATE_REQUEST.getText());

        final CommentDto actual = commentService.patchByUser(USER_ID, COMMENT_ID, COMMENT_UPDATE_REQUEST);

        Assertions.assertThat(actual).isEqualTo(COMMENT_DTO);
        Mockito.verify(userRepository).findById(USER_ID);
        Mockito.verify(commentRepository).findById(COMMENT_ID);
        Mockito.verify(commentMapper).toDto(Mockito.any());
    }

    @Test
    void getNow_always_isNotNull() {
        Assertions.assertThat(commentService.getNow()).isNotNull();
    }

    @Test
    void patchByAdmin_commentNotFound_returnException() {
        Mockito.when(commentRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> commentService.patchByAdmin(COMMENT_ID, COMMENT_UPDATE_REQUEST))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Comment with id=1 was not found");
    }

    @Test
    void patchByAdmin_happyPath_always() {
        final Comment comment = buildComment();
        comment.setText(COMMENT_UPDATE_REQUEST.getText());

        final CommentDto actual = commentService.patchByAdmin(COMMENT_ID, COMMENT_UPDATE_REQUEST);

        Assertions.assertThat(actual).isEqualTo(COMMENT_DTO);
        Mockito.verify(commentRepository).findById(COMMENT_ID);
        Mockito.verify(commentMapper).toDto(Mockito.any());
    }

    @Test
    void deleteByUser_userNotFound_returnException() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> commentService.deleteByUser(USER_ID, COMMENT_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id=2 was not found");
    }

    @Test
    void deleteByUser_commentNotFound_returnException() {
        Mockito.when(commentRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> commentService.deleteByUser(USER_ID, COMMENT_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Comment with id=1 was not found");
    }

    @Test
    void deleteByUser_commentUserIsNotEqualUser_returnException() {
        final User user = buildUser();
        user.setId(100L);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        Assertions.assertThatThrownBy(() -> commentService.deleteByUser(100L, COMMENT_ID))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("User id=100 not owner of Comment id=1");
    }

    @Test
    void deleteByUser_happyPath_always() {
        commentService.deleteByUser(USER_ID, COMMENT_ID);

        Mockito.verify(userRepository).findById(USER_ID);
        Mockito.verify(commentRepository).findById(COMMENT_ID);
        Mockito.verify(commentRepository).deleteById(COMMENT_ID);
    }

    @Test
    void deleteByAdmin_happyPath_always() {
        commentService.deleteByAdmin(COMMENT_ID);

        Mockito.verify(commentRepository).deleteById(COMMENT_ID);
    }

    private static Comment buildComment() {
        final Comment stub = new Comment();
        stub.setId(COMMENT_ID);
        stub.setText("someText");
        stub.setUser(buildUser());
        stub.setEvent(buildEvent());
        stub.setCreatedOn(NOW);

        return stub;
    }

    private static User buildUser() {
        final User stub = new User();
        stub.setId(USER_ID);
        stub.setName("someName");
        stub.setEmail("someEmail");

        return stub;
    }

    private static Event buildEvent() {
        final Event stub = new Event();
        stub.setId(EVENT_ID);

        return stub;
    }

    private record FuncRef<T>(Function<T, Object> lambda) {

    }

}