package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoJsonTest {

    private final JacksonTester<BookingCreateDto> createJson;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testBookingCreateDtoDeserialize() throws Exception {
        String content = "{\"itemId\": 10, \"start\": \"2026-10-01T10:00:00\", \"end\": \"2026-10-05T10:00:00\"}";
        BookingCreateDto dto = createJson.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(10L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 10, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 10, 5, 10, 0));
    }

    @Test
    void testValidateWhenAllFieldsValid() {
        BookingCreateDto dto = makeValidCreateDto();
        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidateWhenItemIdNull() {
        BookingCreateDto dto = makeValidCreateDto();
        dto.setItemId(null);

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("itemId");
    }

    @Test
    void testValidateWhenItemIdNegative() {
        BookingCreateDto dto = makeValidCreateDto();
        dto.setItemId(-1L);

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("itemId");
    }

    @Test
    void testValidateWhenStartInPast() {
        BookingCreateDto dto = makeValidCreateDto();
        dto.setStart(LocalDateTime.now().minusDays(1));

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("start");
    }

    @Test
    void testValidateWhenEndIsNow() {
        BookingCreateDto dto = makeValidCreateDto();
        dto.setEnd(LocalDateTime.now());

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("end");
    }

    @Test
    void testValidateWhenAllFieldsInvalid() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(null)
                .start(null)
                .end(null)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(3);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactlyInAnyOrder("itemId", "start", "end");
    }

    private BookingCreateDto makeValidCreateDto() {
        return BookingCreateDto.builder()
                .itemId(10L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .build();
    }
}
