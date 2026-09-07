package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> getMyItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getByOwner(userId);
    }

    @GetMapping("/{id}")
    public Optional<ItemDto> getItem(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.getById(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto create(
            @Valid @RequestBody ItemCreateDto item,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.create(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @PathVariable Long id,
            @Valid @RequestBody ItemUpdateDto item,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.update(id, item, userId);
    }
}
