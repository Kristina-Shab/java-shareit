package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestCreateDto> createJson;
    private final JacksonTester<ItemRequestDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testItemRequestDtoSerialize() throws Exception {
        ItemRequestDto dto = makeItemRequestDto();
        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.description").isEqualTo("Запрос");
        assertThat(result)
                .extractingJsonPathStringValue("$.created").isEqualTo("2026-09-17T10:00:00");
    }

    @Test
    void testItemRequestCreateDtoDeserialize() throws Exception {
        String content = """
                {
                  "description": "Запрос"
                }
                """;
        ItemRequestCreateDto dto = createJson.parseObject(content);

        assertThat(dto.getDescription()).isEqualTo("Запрос");
    }

    @Test
    void TestValidateWhenAllFieldsValid() {
        ItemRequestCreateDto dto = makeValidCreateDto();
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void TestValidateWhenDescriptionNull() {
        ItemRequestCreateDto dto = makeValidCreateDto();
        dto.setDescription(null);

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("description");
    }

    @Test
    void TestValidateWhenDescriptionBlank() {
        ItemRequestCreateDto dto = makeValidCreateDto();
        dto.setDescription("   ");

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("description");
    }

    private ItemRequestDto makeItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .description("Запрос")
                .created(LocalDateTime.of(2026, 9, 17, 10, 0))
                .build();
    }

    private ItemRequestCreateDto makeValidCreateDto() {
        return ItemRequestCreateDto.builder()
                .description("Запрос")
                .build();
    }
}
