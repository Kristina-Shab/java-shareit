package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto create(BookingCreateDto dto, Long userId) {
        Item bookingItem = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + dto.getItemId() + " не найдена"));
        User booker = getUserId(userId);
        if (bookingItem.getOwner().getId().equals(userId)) {
            throw new ValidationException("Нельзя бронировать свою вещь");
        }
        if (!dto.getEnd().isAfter(dto.getStart())) {
            throw new ValidationException("Дата окончания должна быть позже даты начала");
        }
        checkAvailability(bookingItem, dto.getStart(), dto.getEnd());
        Booking booking = BookingMapper.toBooking(dto, bookingItem, booker);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long userId, boolean approved) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenException("Пользователя с id " + userId + " не существует"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new ForbiddenException("Бронирование не принадлежит пользователю.");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ConflictException("Статус бронирования не WAITING");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        getUserId(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        if (!userId.equals(booking.getItem().getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new ForbiddenException("Информация доступна только владельцу вещи или автору бронирования");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getByBooker(Long userId, State state) {
        getUserId(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingsByBooker = switch (state) {
            case State.CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, now, now);
            case State.PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case State.FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case State.WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    userId, BookingStatus.WAITING);
            case State.REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    userId, BookingStatus.REJECTED);
            default -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };
        return bookingsByBooker.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getByOwner(Long userId, State state) {
        getUserId(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingsByOwner = switch (state) {
            case State.CURRENT -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, now, now);
            case State.PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case State.FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case State.WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                    userId, BookingStatus.WAITING);
            case State.REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                    userId, BookingStatus.REJECTED);
            default -> bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        };
        return bookingsByOwner.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    private User getUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private void checkAvailability(Item bookingItem, LocalDateTime start, LocalDateTime end) {
        if (!bookingItem.isAvailable()) {
            throw new ValidationException("Вещь с id " + bookingItem.getId() + " недоступна для бронирования");
        }
        boolean exists = bookingRepository.existsByItemIdAndStatusInAndStartBeforeAndEndAfter(
                bookingItem.getId(),
                List.of(BookingStatus.APPROVED, BookingStatus.WAITING),
                end,
                start);
        if (exists) {
            throw new ConflictException("Вещь с id " + bookingItem.getId() + " уже забронирована на эти даты");
        }
    }
}
