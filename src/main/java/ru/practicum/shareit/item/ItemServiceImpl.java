package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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

    @Override
    public Collection<ItemBookingsDto> getByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        LocalDateTime now = LocalDateTime.now();

        return itemRepository.findByOwnerId(userId).stream()
                .map(item -> buildItemBookingsDto(item, now))
                .toList();
    }

    @Override
    public Optional<ItemDto> getById(Long id) {
        return itemRepository.findById(id)
                .map(ItemMapper::toItemDto);
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
    public ItemDto create(ItemCreateDto itemRequest, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = ItemMapper.toNewItem(itemRequest, owner);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto update(Long id, ItemUpdateDto itemRequest, Long userId) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("У пользователя с id " + userId + " не найдена вещь с id " + id);
        }
        Item updatedItem = ItemMapper.updateItemFields(existingItem, itemRequest);
        Item savedItem = itemRepository.save(updatedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    private ItemBookingsDto buildItemBookingsDto(Item item, LocalDateTime now) {
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(item.getId());
        BookingDateDto lastBooking = findLastBooking(bookings, now).orElse(null);
        BookingDateDto nextBooking = findNextBooking(bookings, now).orElse(null);
        return ItemMapper.toItemBookingsDto(item, lastBooking, nextBooking);
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
}
