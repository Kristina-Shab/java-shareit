package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDtoJsonTest {
    private final JacksonTester<UserCreateDto> createJson;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testUserCreateDtoDeserialize() throws Exception {
        String content = """
                {
                  "name": "Имя",
                  "email": "Email@mail.ru"
                }
                """;
        UserCreateDto dto = createJson.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Имя");
        assertThat(dto.getEmail()).isEqualTo("Email@mail.ru");
    }

    @Test
    void testValidateWhenAllFieldsValid() {
        UserCreateDto dto = makeValidCreateDto();
        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidateWhenNameNull() {
        UserCreateDto dto = makeValidCreateDto();
        dto.setName(null);

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("name");
    }

    @Test
    void testValidateWhenNameBlank() {
        UserCreateDto dto = makeValidCreateDto();
        dto.setName("   ");

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("name");
    }

    @Test
    void testValidateWhenEmailNull() {
        UserCreateDto dto = makeValidCreateDto();
        dto.setEmail(null);

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("email");
    }

    @Test
    void testValidateWhenEmailInvalid() {
        UserCreateDto dto = makeValidCreateDto();
        dto.setEmail("not-an-email");

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString()).contains("email");
    }

    @Test
    void testValidateWhenAllFieldsInvalid() {
        UserCreateDto dto = UserCreateDto.builder()
                .name(null)
                .email(null)
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(2);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .containsExactlyInAnyOrder("name", "email");
    }

    private UserCreateDto makeValidCreateDto() {
        return UserCreateDto.builder()
                .name("Имя")
                .email("Email@mail.ru")
                .build();
    }
}
