package ru.practicum.user;

import java.util.List;

public interface UserService {
    List<UserDto> get(List<Long> ids, int from, int size);

    UserDto create(UserDto userDto);

    void delete(long userId);
}
