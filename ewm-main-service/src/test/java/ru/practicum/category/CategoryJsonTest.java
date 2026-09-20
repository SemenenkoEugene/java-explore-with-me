package ru.practicum.category;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class CategoryJsonTest {

    @Autowired
    private JacksonTester<CategoryDto> categoryDtoJacksonTester;

    @SneakyThrows
    @Test
    void categoryDtoTest() {
        final CategoryDto categoryDto = CategoryDto.builder()
                .id(1L)
                .name("TestCategory")
                .build();

        final JsonContent<CategoryDto> jsonContent = categoryDtoJacksonTester.write(categoryDto);

        Assertions.assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("TestCategory");
    }
}