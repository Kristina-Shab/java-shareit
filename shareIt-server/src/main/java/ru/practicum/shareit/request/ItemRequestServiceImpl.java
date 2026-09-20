package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestShortDto create(ItemRequestCreateDto item, Long userId) {
        User requestor = getUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toNewItemRequest(item, requestor);
        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestShortDto(createdItemRequest);
    }

    @Override
    public Collection<ItemRequestDto> getByRequestor(Long userId) {
        getUser(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestor(userId);
        return requests.stream()
                .map(r -> ItemRequestMapper.toItemRequestDto(r, getAnswer(r.getId())))
                .toList();
    }

    @Override
    public Collection<ItemRequestDto> getAllOther(Long userId) {
        getUser(userId);
        List<ItemRequest> requests = itemRequestRepository.findByOtherRequestor(userId);
        return requests.stream()
                .map(r -> ItemRequestMapper.toItemRequestDto(r, getAnswer(r.getId())))
                .toList();
    }

    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));
        return ItemRequestMapper.toItemRequestDto(request, getAnswer(requestId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private List<Item> getAnswer(Long requestId) {
        return itemRepository.findAllByRequestId(requestId);
    }
}
