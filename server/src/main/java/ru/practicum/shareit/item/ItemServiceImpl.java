package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Collection<ItemBookingsDto> getByOwner(Long userId) {
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();
        return itemRepository.findByOwnerId(userId).stream()
                .map(item -> buildItemBookingsDto(item, now))
                .toList();
    }

    @Override
    public Optional<ItemBookingsDto> getById(Long id, Long userId) {
        return itemRepository.findById(id).map(item -> {
            if (item.getOwner().getId().equals(userId)) {
                return buildItemBookingsDto(item, LocalDateTime.now());
            }
            return ItemMapper.toItemBookingsDto(item, null, null, getComments(id));
        });
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemDto create(ItemCreateDto itemRequest, Long userId) {
        User owner = getUser(userId);
        ItemRequest request = findRequest(itemRequest.getRequestId());
        Item item = ItemMapper.toNewItem(itemRequest, owner, request);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(Long id, ItemUpdateDto itemRequest, Long userId) {
        Item existingItem = getItem(id);
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("У пользователя с id " + userId + " не найдена вещь с id " + id);
        }
        Item updatedItem = ItemMapper.updateItemFields(existingItem, itemRequest);
        Item savedItem = itemRepository.save(updatedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public CommentDto createComment(Long itemId, CommentCreateDto dto, Long userId) {
        User author = getUser(userId);
        Item item = getItem(itemId);
        checkUserByBooker(itemId, userId);
        Comment comment = CommentMapper.toNewComment(dto, item, author);
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }

    private void checkUserByBooker(Long itemId, Long userId) {
        boolean exists = !bookingRepository.existsCompletedBooking(
                userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED
        );
        if (exists) {
            throw new ValidationException(
                    "Отзыв может оставить только пользователь с завершённым бронированием на указанную вещь");
        }
    }

    private ItemBookingsDto buildItemBookingsDto(Item item, LocalDateTime now) {
        List<Booking> bookings = bookingRepository.findByItem(item.getId());
        BookingDateDto lastBooking = findLastBooking(bookings, now).orElse(null);
        BookingDateDto nextBooking = findNextBooking(bookings, now).orElse(null);
        List<CommentDto> comments = getComments(item.getId());
        return ItemMapper.toItemBookingsDto(item, lastBooking, nextBooking, comments);
    }

    private Optional<BookingDateDto> findLastBooking(List<Booking> bookings, LocalDateTime now) {
        return bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED)
                .filter(booking -> booking.getEnd().isBefore(now))
                .findFirst()
                .map(BookingMapper::toBookingDateDto);
    }

    private Optional<BookingDateDto> findNextBooking(List<Booking> bookings, LocalDateTime now) {
        return bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED)
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::toBookingDateDto);
    }

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }


    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));
    }

    private ItemRequest findRequest(Long requestId) {
        if (requestId == null) {
            return null;
        }
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));
    }
}
