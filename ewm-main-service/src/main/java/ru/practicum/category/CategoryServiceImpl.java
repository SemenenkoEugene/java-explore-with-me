package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAll(int from, int size) {
        return categoryRepository.findAll(PageRequest.of(from, size)).getContent().stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getById(long catId) {
        return CategoryMapper.INSTANCE.toDto(findById(catId));
    }

    @Transactional
    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = CategoryMapper.INSTANCE.fromDto(categoryDto);
        Category saveCategory = categoryRepository.save(category);
        return CategoryMapper.INSTANCE.toDto(saveCategory);
    }

    @Override
    public CategoryDto patch(long catId, CategoryDto categoryDto) {
        Category category = findById(catId);

        Optional.ofNullable(categoryDto.getName()).ifPresent(category::setName);
        Category saveCategory = categoryRepository.save(category);

        return CategoryMapper.INSTANCE.toDto(saveCategory);
    }

    @Override
    public void delete(long catId) {
        findById(catId);
        categoryRepository.deleteById(catId);
    }

    private Category findById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
    }
}
