package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingCreateDto dto, Long userId);

    BookingDto approve(Long bookingId, Long userId, boolean approved);

    BookingDto findById(Long bookingId, Long userId);

    List<BookingDto> getByBooker(Long userId, State state);

    List<BookingDto> getByOwner(Long userId, State state);
}
