package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> get(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<User> page;
        if (ids != null && !ids.isEmpty()) {
            page = userRepository.findAllByIdIn(ids, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        return page.getContent().stream()
                .map(UserMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.INSTANCE.fromDto(userDto);
        User saveUser = userRepository.save(user);
        return UserMapper.INSTANCE.toDto(saveUser);
    }

    @Transactional
    @Override
    public void delete(long userId) {
        findById(userId);
        userRepository.deleteById(userId);
    }

    private void findById(long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + "was not found"));
    }
}
