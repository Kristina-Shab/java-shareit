package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentCreateDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentDtoJsonTest {
    private final JacksonTester<CommentCreateDto> createJson;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testCommentCreateDtoDeserialize() throws Exception {
        String content = """
                {
                  "text": "Отзыв"
                }
                """;
        CommentCreateDto dto = createJson.parseObject(content);

        assertThat(dto.getText()).isEqualTo("Отзыв");
    }

    @Test
    void TestValidateWhenAllFieldsValid() {
        CommentCreateDto dto = makeValidCreateDto();
        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void TestValidateWhenTextNull() {
        CommentCreateDto dto = makeValidCreateDto();
        dto.setText(null);

        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("text");
    }

    @Test
    void TestValidateWhenTextBlank() {
        CommentCreateDto dto = makeValidCreateDto();
        dto.setText("   ");

        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("text");
    }

    private CommentCreateDto makeValidCreateDto() {
        return CommentCreateDto.builder()
                .text("Отзыв")
                .build();
    }
}
