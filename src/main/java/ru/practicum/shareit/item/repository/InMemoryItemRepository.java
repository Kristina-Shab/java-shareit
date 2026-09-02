package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Set<Item>> itemsByOwner = new HashMap<>();


    @Override
    public Item save(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        itemsByOwner.computeIfAbsent(item.getOwner().getId(), k -> new HashSet<>()).add(item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item update(Long id, Item newItem) {
        Item oldItem = items.get(id);
        itemsByOwner.get(oldItem.getOwner().getId()).remove(oldItem);
        itemsByOwner.get(newItem.getOwner().getId()).add(newItem);
        items.put(id, newItem);
        return newItem;
    }

    @Override
    public Collection<Item> findByOwnerId(Long id) {
        return itemsByOwner.getOrDefault(id, Collections.emptySet());
    }

    @Override
    public Collection<Item> search(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }

    private long getNextId() {
        long currentMaxId = items.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
