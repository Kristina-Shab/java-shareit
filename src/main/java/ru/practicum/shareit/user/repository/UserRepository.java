package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long id);

    User update(Long id, User newUser);

    void delete(Long id);

    boolean emailExists(String email);
}
