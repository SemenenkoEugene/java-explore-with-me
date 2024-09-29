package ru.practicum.category;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final long ID = 1L;
    private static final int FROM = 0;
    private static final int SIZE = 10;

    private static final List<Category> CATEGORIES = List.of(buildCategory());
    private static final CategoryDto CATEGORY_DTO_WITH_ID = CategoryDto.builder()
            .id(ID)
            .name("Category 1")
            .build();
    private static final CategoryDto CATEGORY_DTO_WITHOUT_ID = CategoryDto.builder()
            .name("Category 1")
            .build();

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void getAll_shouldReturnEmptyList_whenNoCategoriesExist() {
        Mockito.when(categoryRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(Page.empty());

        final List<CategoryDto> actual = categoryService.getAll(0, 10);

        Assertions.assertThat(actual).isEmpty();
        Mockito.verify(categoryRepository, Mockito.times(1)).findAll(Mockito.any(PageRequest.class));
    }

    @Test
    void getAll_shouldReturnAllCategories_whenCategoriesExist() {
        Mockito.when(categoryRepository.findAll(PageRequest.of(FROM, SIZE))).thenReturn(new PageImpl<>(CATEGORIES));

        final List<CategoryDto> actual = categoryService.getAll(FROM, SIZE);

        Assertions.assertThat(actual).hasSize(1)
                .containsExactly(
                        CategoryDto.builder().id(1L).name("Category 1").build()
                );
        Mockito.verify(categoryRepository, Mockito.times(1)).findAll(PageRequest.of(FROM, SIZE));
    }

    @Test
    void getById_shouldReturnCategory_whenCategoryExists() {
        final Category category = buildCategory();
        Mockito.when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));

        final CategoryDto actual = categoryService.getById(ID);

        Assertions.assertThat(actual).isEqualTo(CATEGORY_DTO_WITH_ID);
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(ID);
    }

    @Test
    void getById_shouldReturnNull_whenCategoryDoesNotExist() {
        final long nonExistedId = 1000L;

        Assertions.assertThatThrownBy(() -> {
            throw new NotFoundException("Category with id " + nonExistedId + " was not found");
        });
        Mockito.verify(categoryRepository, Mockito.never()).findById(nonExistedId);

    }

    @Test
    void create_shouldCreateNewCategorySuccessfullyWithValidInput() {
        Mockito.when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(buildCategory());

        final CategoryDto actual = categoryService.create(CATEGORY_DTO_WITHOUT_ID);

        Assertions.assertThat(actual).isEqualTo(CATEGORY_DTO_WITH_ID);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(Mockito.any(Category.class));
    }

    @Test
    void patch_shouldUpdateName_whenNameIsProvided() {
        Mockito.when(categoryRepository.findById(ID)).thenReturn(Optional.of(buildCategory()));
        Mockito.when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(buildCategory());

        final CategoryDto actual = categoryService.patch(ID, CATEGORY_DTO_WITHOUT_ID);

        Assertions.assertThat(actual).isEqualTo(CATEGORY_DTO_WITH_ID);
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(ID);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(Mockito.any(Category.class));
    }

    @Test
    void delete_successfully_whenCategoryExists() {
        Mockito.when(categoryRepository.findById(ID)).thenReturn(Optional.of(new Category()));

        categoryService.delete(ID);

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(ID);
        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    private static Category buildCategory() {
        final Category category = new Category();
        category.setId(ID);
        category.setName("Category 1");

        return category;
    }

}