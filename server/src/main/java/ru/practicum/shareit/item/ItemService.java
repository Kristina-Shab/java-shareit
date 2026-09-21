package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;
import java.util.Optional;

public interface ItemService {
    Collection<ItemBookingsDto> getByOwner(Long userId);

    Optional<ItemBookingsDto> getById(Long id, Long userId);

    Collection<ItemDto> search(String text);

    ItemDto create(ItemCreateDto item, Long userId);

    ItemDto update(Long id, ItemUpdateDto item, Long userId);

    CommentDto createComment(Long itemId, CommentCreateDto dto, Long userId);
}
