package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestDto> json;

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

    private ItemRequestDto makeItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .description("Запрос")
                .created(LocalDateTime.of(2026, 9, 17, 10, 0))
                .build();
    }
}
