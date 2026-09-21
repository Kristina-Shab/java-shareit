package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items.stream().map(ItemMapper::toAnswerDto).toList())
                .build();
    }

    public static ItemRequest toNewItemRequest(ItemRequestCreateDto itemRequest, User requestor) {
        return ItemRequest.builder()
                .description(itemRequest.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestShortDto toItemRequestShortDto(ItemRequest itemRequest) {
        return ItemRequestShortDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }
}
