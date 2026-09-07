package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;
import java.util.Optional;

public interface ItemService {
    Collection<ItemDto> getByOwner(Long userId);

    Optional<ItemDto> getById(Long id);

    Collection<ItemDto> search(String text);

    ItemDto create(ItemCreateDto item, Long userId);

    ItemDto update(Long id, ItemUpdateDto item, Long userId);
}
