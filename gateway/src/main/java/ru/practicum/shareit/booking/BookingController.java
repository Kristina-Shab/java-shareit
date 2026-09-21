package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.booking.dto.State;

import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(
            @PathVariable Long bookingId,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByBooker(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        State bookingState = State.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный статус: " + state));
        return bookingClient.getBookings(userId, bookingState);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        State bookingState = State.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный статус: " + state));
        return bookingClient.getBookingsByOwner(userId, bookingState);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody BookingCreateDto booking,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        return bookingClient.bookItem(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        return bookingClient.approveBooking(bookingId, userId, approved);
    }
}
