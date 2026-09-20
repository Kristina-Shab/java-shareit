package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final UserService userService;
    private final UserRepository userRepository;

    private static final String NAME = "Имя";
    private static final String EMAIL = "name@mail.com";
    private static final String NEW_NAME = "Новое имя";
    private static final String NEW_EMAIL = "newname@mail.com";

    @Test
    void TestFindById() {
        User user = createUser();

        Optional<UserDto> result = userService.findById(user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getName()).isEqualTo(NAME);
        assertThat(result.get().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void TestCreate() {
        UserCreateDto dto = UserCreateDto.builder()
                .name(NAME)
                .email(EMAIL)
                .build();

        UserDto result = userService.create(dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);

        User savedUser = userRepository.findById(result.getId()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo(NAME);
        assertThat(savedUser.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void TestUpdate() {
        User user = createUser();
        UserUpdateDto dto = UserUpdateDto.builder()
                .name(NEW_NAME)
                .email(NEW_EMAIL)
                .build();

        UserDto result = userService.update(user.getId(), dto);

        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(NEW_NAME);
        assertThat(result.getEmail()).isEqualTo(NEW_EMAIL);

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo(NEW_NAME);
        assertThat(updatedUser.getEmail()).isEqualTo(NEW_EMAIL);
    }

    @Test
    void TestDelete() {
        User user = createUser();
        Long userId = user.getId();
        assertThat(userRepository.findById(userId)).isPresent();

        userService.delete(userId);

        assertThat(userRepository.findById(userId)).isEmpty();
    }

    private User createUser() {
        return userRepository.save(User.builder()
                .name(NAME)
                .email(EMAIL)
                .build());
    }
}
