package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<Item> findById(Long id);

    Item update(Long id, Item newItem);

    Collection<Item> findByOwnerId(Long id);

    Collection<Item> search(String text);
}
