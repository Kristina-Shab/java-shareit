package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getMyItems(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(
            @PathVariable Long id,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        return itemClient.getItem(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        return itemClient.search(text, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody ItemCreateDto item,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        return itemClient.create(item, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemUpdateDto item,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        return itemClient.update(id, item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto dto,
            @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemClient.addComment(itemId, dto, userId);
    }
}
