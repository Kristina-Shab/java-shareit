package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoJsonTest {
    private final JacksonTester<ItemCreateDto> createJson;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testItemCreateDtoDeserialize() throws Exception {
        String content = """
                {
                  "name": "Название",
                  "description": "Описание",
                  "available": true
                }
                """;
        ItemCreateDto dto = createJson.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Название");
        assertThat(dto.getDescription()).isEqualTo("Описание");
        assertThat(dto.getAvailable()).isTrue();
    }

    @Test
    void testValidateWhenAllFieldsValid() {
        ItemCreateDto dto = makeValidCreateDto();
        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidateWhenNameNull() {
        ItemCreateDto dto = makeValidCreateDto();
        dto.setName(null);

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("name");
    }

    @Test
    void testValidateWhenNameBlank() {
        ItemCreateDto dto = makeValidCreateDto();
        dto.setName("   ");

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("name");
    }

    @Test
    void testValidateWhenDescriptionNull() {
        ItemCreateDto dto = makeValidCreateDto();
        dto.setDescription(null);

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("description");
    }

    @Test
    void testValidateWhenDescriptionBlank() {
        ItemCreateDto dto = makeValidCreateDto();
        dto.setDescription("   ");

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("description");
    }

    @Test
    void testValidateWhenAvailableNull() {
        ItemCreateDto dto = makeValidCreateDto();
        dto.setAvailable(null);

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("available");
    }

    @Test
    void testValidateWhenAllFieldsInvalid() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name(null)
                .description(null)
                .available(null)
                .build();

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(3);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactlyInAnyOrder("name", "description", "available");
    }

    private ItemCreateDto makeValidCreateDto() {
        return ItemCreateDto.builder()
                .name("Название")
                .description("Описание")
                .available(true)
                .build();
    }
}
