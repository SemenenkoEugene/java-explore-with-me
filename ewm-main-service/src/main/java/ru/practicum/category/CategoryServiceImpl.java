package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAll(final int from, final int size) {
        final Page<Category> categoryPage = categoryRepository.findAll(PageRequest.of(from, size));

        return Optional.of(categoryPage.getContent()).orElseGet(List::of).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getById(final long catId) {
        final Category category = findById(catId);

        return categoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public CategoryDto create(final CategoryDto categoryDto) {
        final Category category = categoryMapper.fromDto(categoryDto);
        final Category saveCategory = categoryRepository.save(category);
        return categoryMapper.toDto(saveCategory);
    }

    @Override
    public CategoryDto patch(final long catId, final CategoryDto categoryDto) {
        final Category category = findById(catId);

        if (Objects.nonNull(categoryDto.getName())) {
            category.setName(categoryDto.getName());
        }

        final Category saveCategory = categoryRepository.save(category);

        return categoryMapper.toDto(saveCategory);
    }

    @Override
    public void delete(final long catId) {
        final Category category = findById(catId);
        categoryRepository.deleteById(category.getId());
    }

    private Category findById(final long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=%d was not found".formatted(id)));
    }
}
