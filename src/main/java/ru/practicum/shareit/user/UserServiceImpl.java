package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserDto);
    }

    @Override
    public UserDto create(UserCreateDto userRequest) {
        if (userRepository.emailExists(userRequest.getEmail())) {
            throw new ConflictException("Пользователь с email: " + userRequest.getEmail() + " уже существует.");
        }
        User user = UserMapper.toUser(userRequest);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto update(Long id, UserUpdateDto userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        if (userRequest.getEmail() != null
                && !userRequest.getEmail().equals(existingUser.getEmail())
                && userRepository.emailExists(userRequest.getEmail())) {
            throw new ConflictException("Пользователь с email: " + userRequest.getEmail() + " уже существует.");
        }

        User updatedUser = UserMapper.updateUserFields(existingUser, userRequest);
        User savedUser = userRepository.update(id, updatedUser);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        userRepository.delete(id);
    }
}
