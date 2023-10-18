package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryControllerAdmin {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody CategoryDto categoryDto) {
        log.debug("Получен POST запрос на добавление категории {}", categoryDto.toString());
        return categoryService.create(categoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto patch(@PathVariable long catId,
                             @Valid @RequestBody CategoryDto categoryDto) {
        log.debug("Получен PATCH запрос на обновление категории {} c ID {}", categoryDto.toString(), catId);
        return categoryService.patch(catId, categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long catId) {
        log.debug("Получен DELETE запрос для категории c ID {}", catId);
        categoryService.delete(catId);
    }
}
