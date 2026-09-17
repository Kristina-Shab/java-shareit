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
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentDtoJsonTest {
    private final JacksonTester<CommentCreateDto> createJson;
    private final JacksonTester<CommentDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testCommentDtoSerialize() throws Exception {
        CommentDto dto = makeCommentDto();
        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отзыв");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Автор");
        assertThat(result)
                .extractingJsonPathStringValue("$.created").isEqualTo("2026-09-17T10:00:00");
    }

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

    private CommentDto makeCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("Отзыв")
                .authorName("Автор")
                .created(LocalDateTime.of(2026, 9, 17, 10, 0))
                .build();
    }

    private CommentCreateDto makeValidCreateDto() {
        return CommentCreateDto.builder()
                .text("Отзыв")
                .build();
    }
}
