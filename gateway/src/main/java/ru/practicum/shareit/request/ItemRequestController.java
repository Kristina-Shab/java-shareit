package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getByRequestor(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestClient.getByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestClient.getAllOther(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                             @PathVariable Long id) {
        return itemRequestClient.getById(id, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestCreateDto itemRequest,
                                         @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestClient.create(itemRequest, userId);
    }
}