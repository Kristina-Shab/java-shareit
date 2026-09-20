package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestCreateDto> createJson;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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

    private ItemRequestCreateDto makeValidCreateDto() {
        return ItemRequestCreateDto.builder()
                .description("Запрос")
                .build();
    }
}
