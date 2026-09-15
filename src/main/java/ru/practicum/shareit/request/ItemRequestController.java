package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.Collection;

import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public Collection<ItemRequestDto> getByRequestor(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestService.getByRequestor(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAll(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestService.getAllOther(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                     @PathVariable Long id) {
        return itemRequestService.getById(id, userId);
    }

    @PostMapping
    public ItemRequestShortDto create(@Valid @RequestBody ItemRequestCreateDto itemRequest,
                                      @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestService.create(itemRequest, userId);
    }
}
