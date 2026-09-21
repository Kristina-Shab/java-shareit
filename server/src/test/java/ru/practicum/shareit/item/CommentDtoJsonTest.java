package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentDtoJsonTest {
    private final JacksonTester<CommentDto> json;

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

    private CommentDto makeCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("Отзыв")
                .authorName("Автор")
                .created(LocalDateTime.of(2026, 9, 17, 10, 0))
                .build();
    }
}
