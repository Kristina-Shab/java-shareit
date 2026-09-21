package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    BookingClient bookingClient;

    private static final Long USER_ID = 1L;
    private static final Long BOOKING_ID = 5L;

    @Test
    void testFindById() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", BOOKING_ID));
        when(bookingClient.getBooking(BOOKING_ID, USER_ID)).thenReturn(response);

        mvc.perform(get("/bookings/{id}", BOOKING_ID)
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(BOOKING_ID, USER_ID);
    }

    @Test
    void testGetByBooker() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(List.of());
        when(bookingClient.getBookings(USER_ID, State.ALL)).thenReturn(response);

        mvc.perform(get("/bookings").header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());

        verify(bookingClient).getBookings(USER_ID, State.ALL);
    }

    @Test
    void testGetByBookerWithState() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(List.of());
        when(bookingClient.getBookings(USER_ID, State.CURRENT)).thenReturn(response);

        mvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk());

        verify(bookingClient).getBookings(USER_ID, State.CURRENT);
    }

    @Test
    void testGetByOwner() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(List.of());
        when(bookingClient.getBookingsByOwner(USER_ID, State.ALL)).thenReturn(response);

        mvc.perform(get("/bookings/owner").header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());

        verify(bookingClient).getBookingsByOwner(USER_ID, State.ALL);
    }

    @Test
    void testCreate() throws Exception {
        BookingCreateDto createDto = makeCreateDto();
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", 100L));
        when(bookingClient.bookItem(eq(USER_ID), any(BookingCreateDto.class))).thenReturn(response);

        mvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient).bookItem(eq(USER_ID), any(BookingCreateDto.class));
    }

    @Test
    void testApprove() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", BOOKING_ID));
        when(bookingClient.approveBooking(BOOKING_ID, USER_ID, true)).thenReturn(response);

        mvc.perform(patch("/bookings/{id}", BOOKING_ID)
                        .header(X_SHARER_USER_ID, USER_ID)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).approveBooking(BOOKING_ID, USER_ID, true);
    }

    private BookingCreateDto makeCreateDto() {
        return BookingCreateDto.builder()
                .itemId(10L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .build();
    }
}
