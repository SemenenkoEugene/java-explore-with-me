package ru.practicum.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerAdmin {
    private final UserService userService;

    @GetMapping
    public List<UserDto> get(@RequestParam(required = false) final List<Long> ids,
                             @Valid @RequestParam(defaultValue = "0") @Min(0) final int from,
                             @Valid @RequestParam(defaultValue = "10") @Min(1) final int size) {
        log.debug("Получен GET запрос на просмотр пользователей");
        return userService.get(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody final UserDto userDto) {
        log.debug("Получен POST запрос на создание пользователя {}", userDto);
        return userService.create(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final long userId) {
        log.debug("Получен DELETE запрос для пользователя с ID {}", userId);
        userService.delete(userId);
    }
}
