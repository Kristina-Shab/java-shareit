package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestShortDto create(ItemRequestCreateDto item, Long userId);

    Collection<ItemRequestDto> getByRequestor(Long userId);

    Collection<ItemRequestDto> getAllOther(Long userId);

    ItemRequestDto getById(Long requestId, Long userId);
}
