package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDtoJsonTest {
    private final JacksonTester<UserDto> json;

    @Test
    void testUserDtoSerialize() throws Exception {
        UserDto dto = makeUserDto();
        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Имя");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("Email@mail.ru");
    }

    private UserDto makeUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Имя")
                .email("Email@mail.ru")
                .build();
    }
}
