package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDateDto;

@Data
@AllArgsConstructor
@Builder
public class ItemBookingsDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private BookingDateDto lastBooking;
    private BookingDateDto nextBooking;
}
