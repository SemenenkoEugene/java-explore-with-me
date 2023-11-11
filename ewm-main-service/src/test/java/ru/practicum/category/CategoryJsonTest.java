package ru.practicum.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CategoryJsonTest {

    @Autowired
    private JacksonTester<CategoryDto> categoryDtoJacksonTester;

    @Test
    void categoryDtoTest() throws IOException {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1L)
                .name("TestCategory")
                .build();

        JsonContent<CategoryDto> jsonContent = categoryDtoJacksonTester.write(categoryDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("TestCategory");
    }
}