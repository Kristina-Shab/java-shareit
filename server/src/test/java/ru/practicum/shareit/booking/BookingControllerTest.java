package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    BookingService bookingService;

    private static final Long USER_ID = 1L;
    private static final Long BOOKING_ID = 5L;
    private static final LocalDateTime START = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END = LocalDateTime.now().plusDays(3);

    @Test
    void testFindById() throws Exception {
        BookingDto dto = makeBookingDto(BOOKING_ID, BookingStatus.WAITING);
        when(bookingService.findById(BOOKING_ID, USER_ID)).thenReturn(dto);

        mvc.perform(get("/bookings/{id}", BOOKING_ID)
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService).findById(BOOKING_ID, USER_ID);
    }

    @Test
    void testGetByBooker() throws Exception {
        BookingDto dto = makeBookingDto(BOOKING_ID, BookingStatus.WAITING);
        when(bookingService.getByBooker(USER_ID, State.ALL)).thenReturn(List.of(dto));

        mvc.perform(get("/bookings").header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(BOOKING_ID))
                .andExpect(jsonPath("$[0].status").value("WAITING"));

        verify(bookingService).getByBooker(USER_ID, State.ALL);
    }

    @Test
    void testGetByBookerWithState() throws Exception {
        BookingDto dto = makeBookingDto(BOOKING_ID, BookingStatus.APPROVED);
        when(bookingService.getByBooker(USER_ID, State.CURRENT)).thenReturn(List.of(dto));

        mvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(BOOKING_ID))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));

        verify(bookingService).getByBooker(USER_ID, State.CURRENT);
    }

    @Test
    void testGetByOwner() throws Exception {
        BookingDto dto = makeBookingDto(BOOKING_ID, BookingStatus.WAITING);
        when(bookingService.getByOwner(USER_ID, State.ALL)).thenReturn(List.of(dto));

        mvc.perform(get("/bookings/owner").header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(BOOKING_ID))
                .andExpect(jsonPath("$[0].status").value("WAITING"));

        verify(bookingService).getByOwner(USER_ID, State.ALL);
    }

    @Test
    void testCreate() throws Exception {
        BookingCreateDto createDto = makeCreateDto();
        BookingDto dto = makeBookingDto(100L, BookingStatus.WAITING);
        when(bookingService.create(any(BookingCreateDto.class), eq(USER_ID))).thenReturn(dto);

        mvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .content(mapper.writeValueAsString(createDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService).create(any(BookingCreateDto.class), eq(USER_ID));
    }

    @Test
    void testApprove() throws Exception {
        BookingDto dto = makeBookingDto(BOOKING_ID, BookingStatus.APPROVED);
        when(bookingService.approve(BOOKING_ID, USER_ID, true))
                .thenReturn(dto);

        mvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(X_SHARER_USER_ID, USER_ID)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService).approve(BOOKING_ID, USER_ID, true);
    }

    private BookingDto makeBookingDto(Long id, BookingStatus status) {
        return BookingDto.builder()
                .id(id)
                .status(status)
                .build();
    }

    private BookingCreateDto makeCreateDto() {
        return BookingCreateDto.builder()
                .itemId(10L)
                .start(START)
                .end(END)
                .build();
    }
}
