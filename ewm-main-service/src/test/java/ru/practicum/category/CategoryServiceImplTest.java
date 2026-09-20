package ru.practicum.category;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
class CategoryServiceImplTest {

    private static final Long CATEGORY_ID = 1L;

    private static final CategoryDto CATEGORY_DTO = CategoryDto.builder()
            .id(CATEGORY_ID)
            .name("Test Category")
            .build();

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        Mockito.when(categoryMapper.toDto(Mockito.any())).thenReturn(CATEGORY_DTO);
    }

    @Test
    void getAll_emptyListTest() {
        Mockito.when(categoryRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        final List<CategoryDto> actual = categoryService.getAll(0, 10);

        Assertions.assertThat(actual).isEqualTo(List.of());
        Mockito.verify(categoryRepository).findAll(Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void getAll_notEmptyListTest() {
        final List<Category> categories = List.of(buildCategory());

        Mockito.when(categoryRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(categories));

        final List<CategoryDto> actual = categoryService.getAll(0, 10);

        Assertions.assertThat(actual).isEqualTo(List.of(CATEGORY_DTO));

        Mockito.verify(categoryRepository).findAll(Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void getByIdTest() {
        Mockito.when(categoryRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(buildCategory()));

        final CategoryDto actual = categoryService.getById(CATEGORY_ID);

        Assertions.assertThat(actual).isEqualTo(CATEGORY_DTO);

        Mockito.verify(categoryRepository).findById(Mockito.eq(CATEGORY_ID));
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void createTest() {
        Mockito.when(categoryMapper.fromDto(Mockito.any())).thenReturn(buildCategory());
        Mockito.when(categoryRepository.save(Mockito.any())).thenReturn(buildCategory());

        final CategoryDto actual = categoryService.create(CATEGORY_DTO);

        Assertions.assertThat(actual).isEqualTo(CATEGORY_DTO);

        Mockito.verify(categoryRepository).save(Mockito.any(Category.class));
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void patchTest() {
        Mockito.when(categoryRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(buildCategory()));
        Mockito.when(categoryRepository.save(Mockito.any())).thenReturn(buildCategory());

        final CategoryDto actual = categoryService.patch(CATEGORY_ID, CATEGORY_DTO);

        Assertions.assertThat(actual).isEqualTo(CATEGORY_DTO);

        Mockito.verify(categoryRepository).findById(Mockito.eq(CATEGORY_ID));
        Mockito.verify(categoryRepository).save(Mockito.any(Category.class));
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void deleteTest() {
        Mockito.when(categoryRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(buildCategory()));
        Mockito.doNothing().when(categoryRepository).deleteById(CATEGORY_ID);

        categoryService.delete(CATEGORY_ID);

        Mockito.verify(categoryRepository).findById(Mockito.eq(CATEGORY_ID));
        Mockito.verify(categoryRepository).deleteById(Mockito.eq(CATEGORY_ID));
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    private Category buildCategory() {
        final Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setName("Test Category");

        return category;
    }
}