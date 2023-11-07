package ru.practicum.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void get_emptyListTest() {
        List<Long> ids = new ArrayList<>();

        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        userService.get(ids, 0, 10);

        verify(userRepository, times(1)).findAll(any(Pageable.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void get_notEmptyListTest() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        when(userRepository.findAllByIdIn(eq(ids), any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        userService.get(ids, 0, 10);

        verify(userRepository, times(1)).findAllByIdIn(eq(ids), any(Pageable.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createTest() {
        UserDto userDto = UserDto.builder().build();

        when(userRepository.save(any(User.class))).thenReturn(new User());

        userService.create(userDto);

        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

}