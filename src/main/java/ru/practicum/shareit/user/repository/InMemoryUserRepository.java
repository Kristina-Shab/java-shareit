package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public User save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User update(Long id, User newUser) {
        emails.remove(users.get(id).getEmail());
        users.put(id, newUser);
        emails.add(newUser.getEmail());
        return newUser;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
        emails.remove(user.getEmail());
    }

    @Override
    public boolean emailExists(String email) {
        return emails.contains(email);
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
