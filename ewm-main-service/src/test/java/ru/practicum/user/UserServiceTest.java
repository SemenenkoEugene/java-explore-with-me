package ru.practicum.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    private static final Long USER_ID = 1L;

    private static final UserDto USER_DTO = UserDto.builder().build();

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        Mockito.when(userMapper.toDto(Mockito.any())).thenReturn(USER_DTO);
    }


    @Test
    void get_emptyListTest() {
        final List<Long> ids = new ArrayList<>();

        Mockito.when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        final List<UserDto> actual = userService.get(ids, 0, 10);

        Assertions.assertThat(actual).isEqualTo(List.of());

        Mockito.verify(userRepository).findAll(Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void get_notEmptyListTest() {
        final List<Long> ids = List.of(1L, 2L, 3L);

        Mockito.when(userRepository.findAllByIdIn(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(buildUser())));

        final List<UserDto> actual = userService.get(ids, 0, 10);

        Assertions.assertThat(actual).isEqualTo(List.of(USER_DTO));

        Mockito.verify(userRepository).findAllByIdIn(Mockito.eq(ids), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createTest() {
        Mockito.when(userMapper.fromDto(Mockito.any())).thenReturn(buildUser());
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(buildUser());

        final UserDto actual = userService.create(USER_DTO);

        Assertions.assertThat(actual).isEqualTo(USER_DTO);

        Mockito.verify(userRepository).save(Mockito.any(User.class));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteTest() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(buildUser()));
        Mockito.doNothing().when(userRepository).deleteById(USER_ID);

        userService.delete(USER_ID);

        Mockito.verify(userRepository).findById(Mockito.eq(USER_ID));
        Mockito.verify(userRepository).deleteById(Mockito.eq(USER_ID));
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    private User buildUser() {
        final User user = new User();
        user.setId(USER_ID);

        return user;
    }

}