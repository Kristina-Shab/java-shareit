package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> findById(Long id);

    UserDto create(UserCreateDto user);

    UserDto update(Long id, UserUpdateDto user);

    void delete(Long id);
}
