package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoJsonTest {
    private final JacksonTester<ItemCreateDto> createJson;
    private final JacksonTester<ItemDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testItemDtoSerialize() throws Exception {
        ItemDto dto = makeItemDto();
        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Название");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

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
    void TestValidateWhenAllFieldsValid() {
        ItemCreateDto dto = makeValidCreateDto();
        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void TestValidateWhenNameNull() {
        ItemCreateDto dto = makeValidCreateDto();
        dto.setName(null);

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("name");
    }

    @Test
    void TestValidateWhenNameBlank() {
        ItemCreateDto dto = makeValidCreateDto();
        dto.setName("   ");

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("name");
    }

    @Test
    void TestValidateWhenDescriptionNull() {
        ItemCreateDto dto = makeValidCreateDto();
        dto.setDescription(null);

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("description");
    }

    @Test
    void TestValidateWhenDescriptionBlank() {
        ItemCreateDto dto = makeValidCreateDto();
        dto.setDescription("   ");

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("description");
    }

    @Test
    void TestValidateWhenAvailableNull() {
        ItemCreateDto dto = makeValidCreateDto();
        dto.setAvailable(null);

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("available");
    }

    @Test
    void TestValidateWhenAllFieldsInvalid() {
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

    private ItemDto makeItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("Название")
                .description("Описание")
                .available(true)
                .build();
    }

    private ItemCreateDto makeValidCreateDto() {
        return ItemCreateDto.builder()
                .name("Название")
                .description("Описание")
                .available(true)
                .build();
    }
}
