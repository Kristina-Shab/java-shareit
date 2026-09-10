package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto findById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getByBooker(
            @RequestParam(defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getByOwner(
            @RequestParam(defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.getByOwner(userId, state);
    }

    @PostMapping
    public BookingDto create(
            @Valid @RequestBody BookingCreateDto booking,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.approve(bookingId, userId, approved);
    }
}
