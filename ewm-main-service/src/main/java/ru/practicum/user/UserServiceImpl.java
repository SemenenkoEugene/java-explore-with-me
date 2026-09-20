package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> get(final List<Long> ids, final int from, final int size) {
        final Pageable pageable = PageRequest.of(from, size);

        final Page<User> page;

        if (ObjectUtils.isNotEmpty(ids)) {
            page = userRepository.findAllByIdIn(ids, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        return page.getContent().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto create(final UserDto userDto) {
        final User user = userMapper.fromDto(userDto);
        final User saveUser = userRepository.save(user);
        return userMapper.toDto(saveUser);
    }

    @Transactional
    @Override
    public void delete(final long userId) {
        final User user = findById(userId);
        userRepository.deleteById(user.getId());
    }

    private User findById(final long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=%d was not found".formatted(id)));
    }
}
