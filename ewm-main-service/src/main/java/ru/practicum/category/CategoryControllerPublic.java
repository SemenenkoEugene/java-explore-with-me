package ru.practicum.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryControllerPublic {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAll(@Valid @RequestParam(defaultValue = "0") @Min(0) final int from,
                                    @Valid @RequestParam(defaultValue = "10") @Min(1) final int size) {
        log.debug("Получен GET запрос на просмотр категорий");
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable final long catId) {
        log.debug("Получен GET запрос на просмотр категории по ID {}", catId);
        return categoryService.getById(catId);
    }
}
