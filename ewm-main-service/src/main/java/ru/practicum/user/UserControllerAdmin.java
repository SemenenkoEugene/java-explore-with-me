package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerAdmin {
    private final UserService userService;

    @GetMapping
    public List<UserDto> get(@RequestParam(required = false) List<Long> ids,
                             @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                             @Valid @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Получен GET запрос на просмотр пользователей");
        return userService.get(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.debug("Получен POST запрос на создание пользователя {}", userDto);
        return userService.create(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId) {
        log.debug("Получен DELETE запрос для пользователя с ID {}", userId);
        userService.delete(userId);
    }
}
